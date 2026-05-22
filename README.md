# bz-lottery

一个基于真实现网业务场景的**微服务抽奖平台**，主要用于后端架构实践与技术学习。

## 技术栈

| 分类 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 25（虚拟线程 / Record / 模式匹配） |
| 框架 | Spring Boot + Spring Cloud | 3.5.7 + 2025.0.1 |
| 网关 | Spring Cloud Gateway（WebFlux） | — |
| 服务发现 | Nacos | 3.1.1 |
| RPC | Apache Dubbo | 3.3.6 |
| 认证 | Sa-Token + Spring Security（Keycloak OAuth2） | 1.45.0 / 6.5.7 |
| ORM | MyBatis-Plus | 3.5.12 |
| 数据库 | PostgreSQL | 17 |
| 缓存 / 锁 | Redis + Redisson | 7.4 / 3.50.0 |
| 消息队列 | Kafka + RocketMQ | 3.9.2 / 5.3.2 |
| 文件存储 | MinIO | RELEASE.2025-07 |
| 工作流 | Activiti | 7.1.0.M6 |
| AI | 智谱 GLM SDK | 0.3.3 |
| 可观测性 | SkyWalking + Prometheus + Actuator | 9.3.0 |
| 构建 | Maven | 3.9+ |

## 模块架构

```
                    ┌──────────────┐
                    │    Nginx     │  (port 9010)
                    └──────┬───────┘
                           │
                    ┌──────┴───────┐
                    │   Gateway    │  (port 9008)  Spring Cloud Gateway
                    └──────┬───────┘
           ┌───────────────┼───────────────┐
           │               │               │
    ┌──────┴──────┐ ┌──────┴──────┐ ┌──────┴──────┐
    │ lottery-user│ │lottery-ai  │ │lottery-     │
    │   :9101     │ │   :9108    │ │award :9104  │
    │ 用户/认证    │ │ AI分析     │ │ 奖品查询    │
    └─────────────┘ └─────────────┘ └─────────────┘
           │
    ┌──────┴──────┐
    │lottery-     │
    │lottery:9103 │  ★ 核心抽奖引擎
    └─────────────┘

   ┌──────────────┬──────────────┬──────────────┬──────────────┬──────────────┐
   │   activity   │     pay      │   workflow   │    file      │   monitor    │
   │    :9102     │    :9105     │    :9106     │    :9107     │    :9109     │
   │   (规划中)    │   (规划中)    │   (规划中)    │   (规划中)    │   (规划中)    │
   └──────────────┴──────────────┴──────────────┴──────────────┴──────────────┘

   lottery-common  ← 所有模块共享（ApiResponse / 异常 / CORS / Web 配置）
```

### 模块职责

| 模块 | 端口 | 状态 | 职责 |
|------|------|------|------|
| `lottery-gateway` | 9008 | 可用 | 统一入口、认证校验、CORS、TraceId 传播 |
| `lottery-common` | — | 可用 | 公共响应体、全局异常处理、跨域配置 |
| `lottery-user` | 9101 | 可用 | 注册/登录/登出、Keycloak OAuth2 对接、RBAC 权限 |
| `lottery-lottery` | 9103 | 可用 | 核心抽奖引擎：策略模式、保底阶梯、库存扣减 |
| `lottery-award` | 9104 | 可用 | 奖品查询、奖品列表 |
| `lottery-ai` | 9108 | 可用 | GLM AI 抽奖行为分析，支持 SSE 流式输出 |
| `lottery-activity` | 9102 | 规划中 | 活动/场次管理 |
| `lottery-pay` | 9105 | 规划中 | 支付服务 |
| `lottery-workflow` | 9106 | 规划中 | Activiti 审批流 |
| `lottery-file` | 9107 | 规划中 | MinIO 文件上传下载 |
| `lottery-monitor` | 9109 | 规划中 | 健康监控 |

## 核心设计要点

### 抽奖策略（`lottery-lottery`）

采用**策略模式**，支持动态切换：

| 策略 | 说明 |
|------|------|
| `ProbabilityOnlyDrawStrategy` | 纯概率抽奖，基于奖品权重随机选取 |
| `GuaranteeLadderDrawStrategy` | 保底阶梯策略，支持 LUCKY / MID_TIER / SPECIAL 三级保底，可降级 |

策略通过 `lottery_system_config` 表配置 `LOTTERY_DRAW_STRATEGY` 项切换，`LotteryStrategyResolver` 在启动时从 DB 加载，支持运行时 `reload()`。

### 并发安全

```
用户请求
    │
    ├── Redisson 分布式锁（per-user）          ← 本次新增
    │   lottery:draw:lock:user:{userId}
    │   等待 3s / 租约 10s
    │
    ├── requestNo 幂等检查                     ← 防重复提交
    │
    ├── 乐观锁库存扣减                          ← 最后防线
    │   UPDATE SET available_stock = available_stock - 1
    │   WHERE id=? AND available_stock > 0
    │
    └── 记录写入
```

### 认证链路

```
Client ─→ Gateway ─→ lottery-user (Sa-Token 验证)
                        │
                        ├── 本地账号密码 (BCrypt)
                        ├── Keycloak OAuth2 / OIDC
                        └── SaTokenAuthenticationFilter → Spring Security Context
```

## 本地开发

### 环境准备

```powershell
# 1. 启动基础设施（PostgreSQL / Redis / Nacos / Kafka / RocketMQ / MinIO）
.\deploy\scripts\up-dev.ps1

# 2. 启动 Nginx 反向代理（可选，不启动也可直连服务端口）
.\deploy\scripts\up-edge.ps1

# 3. IDE 中按需启动各微服务 Application 类
```

### 环境变量

参考 `deploy/env/.env.example`，首次运行自动从 example 复制。

---

## 优化待办清单

> 以下为本项目已识别的架构优化点，按优先级排列，待逐一实施。已完成项标记为 ✅。

### P0 — 影响正确性与稳定性

- [x] **抽奖服务分布式锁** — `LotteryDrawService.draw()` 已加入 Redisson 按用户粒度的分布式锁（2026-05-22）
- [ ] **网关 Token 缓存** — `GatewayAuthFilter` 每次请求都通过 WebClient 调用 `lottery-user` 验证 Token，高并发下 user 服务是瓶颈。建议引入 Caffeine 本地缓存 + Redis 二级缓存，减少回源调用
- [ ] **数据库隔离** — 当前多个服务共用同一 PostgreSQL 实例和 schema，Flyway 迁移脚本没有按服务隔离。建议至少按 schema 拆分，长期按数据库实例拆分

### P1 — 影响可维护性与可观测性

- [ ] **补全测试覆盖** — 目前仅 `lottery-lottery` 有 12 个单元测试，`lottery-user`、`lottery-award`、`lottery-ai` 等模块零测试。建议至少补齐核心服务的单元测试和 Controller 集成测试
- [ ] **Nacos 配置中心** — 当前 Nacos 仅用于服务发现（`nacos.config.enabled: false`），各服务各自维护 `application.yml`，大量重复配置。启用配置中心可实现共享配置与热刷新
- [ ] **业务指标埋点** — SkyWalking + Prometheus 已部署但代码中无自定义指标（`@Timed` / Counter / Gauge）。关键路径需要：抽奖 QPS/延迟、库存扣减失败次数、AI 调用延迟和 fallback 率、Token 校验耗时
- [ ] **日志持久化** — 所有模块 `logback-spring.xml` 仅 CONSOLE appender，容器重启后日志丢失。建议增加 JSON 文件 appender，便于后续接入 ELK/Loki

### P2 — 架构演进方向

- [ ] **清理未使用的消息队列** — Kafka + RocketMQ 两个容器在 `compose.dev-infra.yml` 中持续运行，但代码中无任何生产/消费逻辑。建议明确各自定位（如 Kafka 做事件流、RocketMQ 做事务消息），或暂时只保留一个
- [ ] **空壳服务按需部署** — `activity` / `pay` / `workflow` / `file` / `monitor` 五个模块目前仅有启动类和 `/ping` 接口，占用端口和 Nacos 注册资源。建议按 YAGNI 原则，有业务需求时再启用
- [ ] **引入 API 版本化** — 当前接口路径为 `/api/{module}/xxx`，无版本前缀。建议尽早加上 `/v1`，避免未来 Breaking Change 的迁移成本
- [ ] **生产环境 Nacos 配置** — `prod/compose/compose.prod.yml` 中 `NACOS_SERVER_ADDR` 默认空，无 Nacos 容器，意味着生产环境不启用服务发现。需确认这是设计意图还是遗漏
- [ ] **抽奖策略管理接口** — 当前策略切换需直接改 DB `lottery_system_config` 表。建议增加管理端接口，让运营动态切换策略并即时生效（调用 `LotteryStrategyResolver.reload()`）
- [ ] **引入 Resilience4j 熔断降级** — 网关调用 user 服务、AI 模块调用 GLM API，均无熔断保护。依赖不可用时可能导致线程堆积
- [ ] **前端 Nginx 模式切换** — `deploy/scripts/set-nginx-dev-mode.ps1` / `set-nginx-static-mode.ps1` 当前通过替换配置文件实现，可考虑用环境变量驱动，减少脚本维护成本
