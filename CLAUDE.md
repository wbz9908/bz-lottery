# CLAUDE.md

本文件为 Claude Code（claude.ai/code）在本仓库中协作提供指导。

## 项目概述

bz-lottery 是一个个人全栈学习项目——抽奖平台，后端使用 Spring Boot 3 + Spring Cloud Alibaba 微服务（Java 25，虚拟线程），前端使用 Vue 3 + Vite 6。基础设施通过 Docker Compose 运行（PostgreSQL 17、Redis、Nacos、Kafka、RocketMQ、MinIO）。

## 任务前置要求

每个子项目都有 `.agents/` 目录，包含必须预读的参考文件。在修改代码之前，先阅读对应的 SKILL.md，然后按其引用链依次阅读：

| 范围 | SKILL.md | 阅读顺序 |
|------|----------|----------|
| 后端 | `lottery-backend/.agents/SKILL.md` | architecture → business → api → code → data → commit |
| 前端 | `lottery-frontend/.agents/SKILL.md` | api → code → commit |
| 部署 | `deploy/.agents/SKILL.md` | infrastructure → commit |

所有文件必须使用 UTF-8 编码。只做最小化、聚焦的修改——不做无关的重构。

## 常用命令

### 后端

```bash
cd lottery-backend

# 编译全部模块
mvn -q -DskipTests compile

# 编译单个模块及其依赖
mvn -pl lottery-lottery -am compile

# 运行单个模块的测试
mvn -pl lottery-lottery test

# 启动单个服务
mvn -pl lottery-lottery -am spring-boot:run
```

模块启动顺序：`gateway` → `user` → `activity` → `lottery` → `award` → `pay` → `workflow` → `file` → `monitor`。

### 前端

```bash
cd lottery-frontend
npm install
npm run dev        # 端口 9510，代理 /lottery-* → gateway:9008
npm run build
npm run preview    # 端口 9511
```

### 基础设施

```powershell
.\deploy\scripts\up-dev.ps1     # PostgreSQL, Redis, Nacos, Kafka, RocketMQ, MinIO
.\deploy\scripts\up-edge.ps1    # Nginx
.\deploy\scripts\down.ps1       # 停止所有服务
```

## 架构

### 服务地图

```
Vue 3 (:9510) → Vite proxy → Gateway (:9008) → Nacos 注册中心
                                                    ↓
  user ── activity ── lottery ── award ── pay ── workflow ── file ── monitor ── ai
    ↓        ↓          ↓         ↓       ↓        ↓          ↓        ↓         ↓
  [lottery-common: ApiResponse, TraceId, 异常处理器, VirtualThread 配置]
  [lottery-domain: 共享实体/POJO]
```

- **Gateway**（`lottery-gateway`）：统一入口，端口 `:9008`。路由配置、CORS、鉴权过滤器、限流。
- **Common**（`lottery-common`）：`ApiResponse<T>` 响应体（`code`、`message`、`data`、`timestamp`、`traceId`），全局异常处理器，TraceId 注入，VirtualThread + WebMvc 配置。
- **Domain**（`lottery-domain`）：共享领域实体。
- **User**：认证（Spring Security + Sa-Token 风格），两种角色：`LOTTERY_USER` 和 `LOTTERY_ADMIN`。
- **Activity**：活动生命周期（创建、编辑、发布、下架）。
- **Lottery**：核心抽奖引擎——策略、概率、库存扣减、抽奖记录。
- **Award**：奖品目录、库存、图片、发放规则。
- **Workflow**：WebSocket 实时推送抽奖结果。
- **File**：MinIO 对象存储。
- **Pay/AI/Monitor**：支付集成、AI 功能、健康指标。

### 分层规范（每个模块严格遵循）

```
controller → service（接口 + 实现）→ mapper（MyBatis-Plus）
```

Controller 禁止直接调用 Mapper。跨服务调用使用 Dubbo RPC。

### 数据库

通过 Flyway 管理迁移（`V{n}__description.sql`）和可重复脚本（`R__description.sql`）。所有表均包含 `id`（int8 自增）、`created_at`、`updated_at`、`deleted`（软删除）。

| 表名 | 用途 |
|------|------|
| `lottery_draw_record` | 抽奖事务日志，含完整快照 |
| `lottery_prize` | 奖品目录及库存 |
| `lottery_system_config` | 全局 KV 配置（策略参数） |
| `user_account` | 用户身份与凭证 |
| `sys_role` / `sys_menu` | RBAC：角色、菜单 |
| `user_role_rel` / `role_menu_rel` | M:N 关联关系 |

### 关键约定

- **TraceId**：通过过滤器按请求注入，跨服务传播。所有关键日志均包含 TraceId。
- **并发控制**：虚拟线程（Java 25）。库存使用原子 SQL 更新（`UPDATE SET stock = stock - 1 WHERE ... AND stock > 0`）。唯一索引作为并发兜底。禁止 read-modify-write 模式。
- **幂等性**：抽奖请求使用 `requestNo`。MQ 消费者必须具备幂等性和重试机制。
- **DTO**：使用 Lombok `@Data` / `@Value` / `@Builder`。禁止手写 getter/setter。
- **依赖注入**：使用 `@RequiredArgsConstructor` 构造器注入。
- **HTTP 客户端**：外部 API 调用使用 `java.net.http.HttpClient`（JDK 25），不使用 `RestTemplate` 默认客户端（已知与国内平台 CDN 存在兼容问题）。
- **批量查询**：每条 `IN` 子句最多 500 项。超出则拆分合并。
- **N+1 问题**：禁止循环调用 Repository/Mapper。在循环外部批量查询，转为 Map 查找。
- **分页**：统一使用 0 起始索引。`size` 参数加 `@Max(100)` 限制。

### 前端

- 仅使用 Composition API + `<script setup>`。禁止 Options API。
- 所有 API 调用使用共享 Axios 实例（`api/http.js`），统一处理 Token 注入和错误码。
- 路由定义在 `router/route-table.js`，通过 `meta.roles` 实现权限守卫。
- 两套角色布局：`/admin`（LOTTERY_ADMIN）和 `/app`（LOTTERY_USER）。
- 每个数据获取组件都需处理加载中、空数据和错误三种状态。

### 提交规范

- 分支命名：`codex/{模块}-{任务}`
- 提交信息：`type(scope): subject`——`feat`、`fix`、`refactor`、`docs`、`test`、`chore`
- AI 参与标识：追加 `Co-authored-by: Claude <noreply@anthropic.com>`（以及/或 Codex 对应标识）
