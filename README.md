
<div align="center">

# 🎰 bz-lottery — 抽奖平台

**Spring Boot 3 + Vue 3 全栈微服务练习项目**

</div>

---

## 📖 项目介绍

**bz-lottery** 是一个个人学习项目，采用前后端分离架构搭建的抽奖平台原型。后端基于 Spring Boot 3 + Spring Cloud Alibaba 微服务体系，前端基于 Vue 3 + Vite 6 构建，涵盖了从活动管理、奖品配置、概率运算到实时通知的抽奖核心链路。

开发模式为 **个人 + AI 辅助编程**，借助 AI 工具在架构设计、代码生成、调试等环节提效，项目整体设计、技术选型与核心逻辑由个人把控。

项目开源至 GitHub，用于记录 Spring Cloud 微服务 + Vue 3 前后端分离的全栈实践，包含微服务拆分、容器化本地开发环境、权限路由、WebSocket 实时推送等技术场景的练习实现。

> **定位**：个人全栈练习作品。

---

## 🧱 技术栈

### 后端

| 类别 | 选型 | 版本 |
|------|------|------|
| 基础框架 | Spring Boot | 3.5.7 |
| JDK | Java | 25 (Virtual Threads) |
| 微服务 | Spring Cloud Alibaba | 2025.0.0.0 |
| 网关 | Spring Cloud Gateway | — |
| 认证授权 | Spring Security + Sa-Token 风格 | 6.5.7 |
| ORM | MyBatis-Plus | 3.5.12 |
| 数据库 | PostgreSQL | 17 |
| 缓存 | Redis + Redisson | 7.4 / 3.50.0 |
| 消息队列 | RocketMQ / Kafka | 5.3.2 / 3.9.2 |
| 实时推送 | WebSocket (Spring) | — |
| 注册配置 | Nacos | 3.1.1 |
| RPC | Apache Dubbo | 3.3.6 |
| 文件存储 | MinIO | — |
| API 文档 | OpenAPI (SpringDoc) | — |

### 前端

| 类别 | 选型 | 版本 |
|------|------|------|
| 框架 | Vue | 3.5 |
| 构建工具 | Vite | 6.2 |
| 路由 | Vue Router | 4.6 |
| 状态管理 | Pinia (stores) | — |
| UI 组件 | Element Plus | — |
| HTTP 客户端 | Axios | — |

### 基础设施与 DevOps

| 类别 | 选型 |
|------|------|
| 容器编排 | Docker Compose |
| CI/CD | Jenkins（练习搭建） |
| 制品管理 | Nexus Repository（练习搭建） |
| 部署环境 | 本地 dev / 边缘 edge / 生产草图 |
| 操作系统 | Linux / Windows (PowerShell 脚本) |

---

## 🎯 核心功能

### 业务功能

- **抽奖活动管理** — 创建、编辑、上下架抽奖活动，支持多活动并行
- **奖品配置** — 定义奖品种类、库存、图片、发放规则
- **概率设置** — 支持按奖品维度配置中奖概率，灵活调整权重
- **用户参与抽奖** — 基于权限校验的一次/多次抽奖，实时扣减库存
- **实时中奖通知** — WebSocket 推送中奖结果，即时反馈
- **中奖记录管理** — 用户维度和全局维度的中奖历史查询
- **账号与权限管理** — 基于角色的路由级权限控制（`LOTTERY_USER` / `LOTTERY_ADMIN`）
- **运营后台** — 活动总览、运营操作台，支持管理级操作

### 系统能力

- **虚拟线程 (Virtual Threads)** — Java 25 结构化并发，提升 I/O 密集型场景吞吐
- **统一响应格式** — `ApiResponse<T>` 全局封装，code + message + data + timestamp
- **全局异常处理** — 统一异常拦截与错误码映射
- **TraceId 链路追踪** — 请求级别的 Trace ID 注入，便于日志串联
- **OpenAPI 文档** — 自动生成接口文档，支持 Swagger UI 在线调试
- **网关统一路由** — Spring Cloud Gateway 集中管理微服务路由与跨域

---

## 🚀 本地启动

### 前置条件

- JDK ≥ 25
- Node.js ≥ 22
- Maven ≥ 3.9
- Docker & Docker Compose（启动基础设施依赖）

### 1. 启动基础设施 (Docker)

项目使用 Docker Compose 管理本地开发依赖。在项目根目录执行：

```bash
# 启动数据库、缓存、消息队列、文件存储等
./deploy/scripts/up-dev.ps1          # Windows PowerShell
# 或
deploy/scripts/up-dev.ps1

# （可选）启动构建工具链 Nexus + Jenkins
./deploy/scripts/up-tools.ps1
```

启动的服务包括：PostgreSQL 17、Redis 7.4、Nacos 3.1、Kafka 3.9、RocketMQ 5.3、MinIO。

环境变量模板见 `deploy/env/.env.example`，脚本会自动复制为 `.env` 使用。

### 2. 启动后端

```bash
# 进入后端根目录
cd lottery-backend

# 编译全部模块
mvn clean compile -U

# 按模块顺序启动（推荐 IDE 逐模块启动）
# 启动顺序：lottery-gateway → lottery-user → lottery-activity → lottery-lottery
#            → lottery-award → lottery-pay → lottery-workflow → lottery-file
#            → lottery-monitor

# （可选）打包
mvn clean package -DskipTests
```

> **说明**：网关默认端口 `9008`，各微服务通过 Nacos 注册发现。  
> 各模块启动类位于 `com.lottery.{module}.{Module}Application.java`。

### 3. 启动前端

```bash
# 进入前端目录
cd lottery-frontend

# 安装依赖
npm install

# 启动开发服务器（默认端口 9510）
npm run dev
```

> 开发服务器自动代理 `/lottery-*` 请求到网关 `http://localhost:9008`，支持 HMR 热更新。  
> 可通过 `.env` 文件调整端口和网关地址：
> ```
> VITE_DEV_PORT=9510
> VITE_GATEWAY_TARGET=http://localhost:9008
> ```

### 4. 访问

| 入口 | 地址 |
|------|------|
| 前端页面 | `http://localhost:9510` |
| 后端 API (网关) | `http://localhost:9008` |
| Swagger UI | `http://localhost:9008/swagger-ui.html` |
| Nacos 控制台 | `http://localhost:8080` |
| MinIO 控制台 | `http://localhost:9001` |

---

## 📁 项目目录结构

```
bz-lottery/
├── lottery-backend/                    # 后端 — Maven 多模块
│   ├── pom.xml                         # 父 POM，聚合所有子模块
│   ├── .mvn/                           # Maven Wrapper 配置
│   ├── lottery-gateway/                # 网关层 — Spring Cloud Gateway
│   │   └── src/main/java/com/lottery/gateway/
│   │       ├── LotteryGatewayApplication.java
│   │       ├── config/                 # 网关配置（路由、跨域、限流）
│   │       ├── filter/                 # 网关过滤器（鉴权、日志）
│   │       └── support/                # 辅助组件
│   ├── lottery-common/                 # 公共模块 — 基础设施抽象
│   │   └── src/main/java/com/lottery/common/
│   │       ├── concurrent/             # 虚拟线程 + 结构化并发
│   │       ├── config/                 # 公共配置（OpenAPI、VirtualThread、WebMvc）
│   │       ├── exception/              # 业务异常 + 全局处理器
│   │       ├── response/               # 统一响应 ApiResponse
│   │       └── web/                    # TraceId 过滤器、ResponseBody 增强
│   ├── lottery-user/                   # 用户与认证模块
│   │   └── src/main/java/com/lottery/user/
│   │       ├── controller/             # 登录、注册、用户管理接口
│   │       ├── domain/entity/          # 用户实体
│   │       ├── infrastructure/mapper/  # MyBatis-Plus Mapper
│   │       ├── model/request/response/ # 请求/响应 DTO
│   │       ├── security/               # 认证过滤器 + 权限上下文
│   │       ├── service/                # 用户业务逻辑
│   │       └── config/                 # 安全配置
│   ├── lottery-activity/               # 活动管理模块
│   │   └── src/main/java/com/lottery/activity/
│   │       └── controller/             # 活动 CRUD 接口
│   ├── lottery-lottery/                # 核心抽奖引擎
│   │   └── src/main/java/com/lottery/lottery/
│   │       ├── application/            # 应用层 — 抽奖策略 + DTO
│   │       ├── controller/             # 抽奖接口
│   │       ├── domain/entity/          # 抽奖领域实体
│   │       └── infrastructure/mapper/  # 数据访问
│   ├── lottery-award/                  # 奖品管理模块
│   │   └── src/main/java/com/lottery/award/
│   │       ├── application/            # 奖品服务 + DTO
│   │       ├── controller/             # 奖品 CRUD 接口
│   │       ├── domain/entity/          # 奖品实体
│   │       └── infrastructure/mapper/  # 数据访问
│   ├── lottery-pay/                    # 支付/积分兑换模块
│   │   └── ...
│   ├── lottery-workflow/               # 工作流与实时推送模块
│   │   ├── WebSocket 支持
│   │   └── ...
│   ├── lottery-file/                   # 文件存储模块（MinIO）
│   │   └── ...
│   ├── lottery-ai/                     # AI 智能模块
│   │   └── ...
│   └── lottery-monitor/                # 服务监控模块
│       └── ...
│
├── lottery-frontend/                   # 前端 — Vue 3 + Vite
│   ├── index.html                      # 入口 HTML
│   ├── vite.config.js                  # Vite 配置（代理、HMR）
│   ├── package.json                    # 前端依赖
│   ├── .env / .env.example             # 环境变量
│   ├── Dockerfile.dev                  # 前端开发镜像
│   └── src/
│       ├── main.js                     # Vue 应用入口
│       ├── App.vue                     # 根组件
│       ├── style.css                   # 全局样式
│       ├── api/                        # HTTP 接口层（Axios）
│       │   ├── http.js                 # Axios 实例 + 拦截器
│       │   ├── auth.js                 # 认证相关 API
│       │   └── lottery.js             # 抽奖相关 API
│       ├── router/                     # 路由
│       │   ├── index.js                # 路由实例 + 权限守卫
│       │   └── route-table.js          # 路由表定义（含角色元信息）
│       ├── stores/                     # Pinia 状态管理
│       │   └── session.js              # 会话/权限状态
│       └── views/                      # 页面视图
│           ├── LoginView.vue           # 登录页
│           ├── WorkspaceLayout.vue     # 工作台布局（侧边栏导航）
│           └── workspace/
│               ├── OverviewView.vue    # 🏠 指挥总览
│               ├── LotteryView.vue     # 🎰 抽奖工作台
│               ├── PrizeCenterView.vue # 🏆 奖池中心
│               ├── OperationsView.vue  # ⚙️ 运营权限台（Admin）
│               └── ProfileView.vue     # 👤 账号与权限
│
├── deploy/                             # 部署与基础设施
│   ├── compose/                        # Docker Compose 编排
│   │   ├── compose.dev-infra.yml       # 开发基础设施（PG / Redis / Nacos / Kafka / RocketMQ / MinIO）
│   │   ├── compose.dev-tools.yml       # 工程化工具（Jenkins / Nexus）
│   │   └── compose.edge.yml            # 边缘入口（Nginx）
│   ├── docker/                         # Docker 构建资产
│   │   ├── build/                      # Dockerfile
│   │   ├── conf/                       # 运行时配置
│   │   └── data/                       # 持久化数据
│   ├── env/                            # 环境变量模板
│   │   └── .env.example
│   ├── scripts/                        # 一键管理脚本（PowerShell）
│   │   ├── up-dev.ps1                  # 启动开发环境
│   │   ├── up-tools.ps1                # 启动工具链
│   │   ├── up-jenkins.ps1              # 启动 Jenkins
│   │   ├── up-edge.ps1                 # 启动边缘网关
│   │   ├── up-full.ps1                 # 全量启动
│   │   ├── down.ps1                    # 停止所有
│   │   └── set-nginx-*.ps1             # Nginx 模式切换
│   └── prod/                           # 生产部署草图
│       ├── compose/                    # 生产编排
│       ├── env/                        # 生产环境变量
│       ├── conf/nginx/                 # Nginx 配置
│       ├── scripts/                    # 部署/回滚脚本
│       └── data/                       # 持久化数据目录
│
├── scripts/                            # 通用工具脚本
│   ├── cd/                             # 持续交付相关
│   └── ci/                             # CI 校验脚本
│       ├── backend-verify.sh           # 后端编译+测试
│       ├── frontend-build.sh           # 前端构建
│       └── smoke-check.sh              # 冒烟测试
│
├── lottery-backend/.agents/            # 后端 AI 研发规范（架构、业务、API、编码、数据）→ SKILL.md
├── lottery-frontend/.agents/           # 前端 AI 研发规范（API 对接、Vue 3 开发）→ SKILL.md
├── deploy/.agents/                     # 部署 AI 研发规范（Docker、Nginx、基础设施）→ SKILL.md
│
├── backend/                            # （构建输出目录，与源目录同级）
├── frontend/                           # （构建输出目录，与源目录同级）
├── LICENSE
└── README.md                           # 本文件
```

---

## 🧭 后续扩展计划

### 1. 🐳 Docker 容器化

- 各微服务模块补充 `Dockerfile`（多阶段构建，基于 `eclipse-temurin:25-jre-alpine`）
- 统一 Docker Compose 编排文件，一条命令启动全栈
- 集成 docker-compose healthcheck 依赖等待机制

### 2. 🤖 自动化 CI/CD

- 完善 Jenkins Pipeline（`deploy/Jenkinsfile`）：
  - 代码检测 → 单元测试 → 集成测试 → 镜像构建 → 部署
- 引入自动化冒烟测试（`scripts/ci/smoke-check.sh`）
- 支持 Blue-Green 部署与一键回滚（`deploy/prod/scripts/rollback-prod.ps1`）

### 3. 📊 服务监控与可观测性

- 集成 Prometheus + Grafana 监控微服务指标（JVM / 请求 / 缓存 / 消息队列）
- 集成 SkyWalking / OpenTelemetry 实现分布式链路追踪
- 日志采集接入 ELK / Loki 栈
- 核心业务指标（抽奖 QPS、中奖率、库存水位）仪表盘

### 4. ⚡ 高并发优化

- **缓存层**：抽奖活动配置与奖品库存预加载至 Redis，Redisson 分布式锁防超卖
- **消息队列**：抽奖请求异步化，RocketMQ 削峰填谷，保障最终一致性
- **抽奖策略**：支持权重预计算、分段概率表、本地缓存预热
- **数据库层**：读写分离、库存字段乐观锁、定时库存快照
- **网关层**：基于 Spring Cloud Gateway 的限流（令牌桶 / 滑动窗口）、降级、熔断
- **弹性伸缩**：基于 CPU / 内存 / 队列深度的 Pod 水平扩缩容（K8s HPA）

---

<div align="center">
<sub>Built with Spring Boot 3 · Vue 3 · Docker · RocketMQ · Virtual Threads</sub>
</div>
