---
category: infrastructure
version: "1.0.0"
last_updated: "2026-05-29"
---

# 部署与基础设施规范

## 摘要
定义本地联调环境、Docker 编排、Nginx 配置和生产部署草图的统一规范，确保环境可复现、可维护。

## 目录结构约定

```text
deploy
├── compose/          # Docker Compose 编排文件，按职责分三层
│   ├── compose.dev-infra.yml   # 开发基础设施（数据库、缓存、消息队列等）
│   ├── compose.dev-tools.yml   # 工程化工具（Jenkins、Nexus）
│   └── compose.edge.yml        # 边缘入口层（Nginx）
├── docker/           # Docker 资产，按类型分层
│   ├── build/        # Dockerfile 和构建期文件
│   ├── conf/         # 运行期配置、初始化脚本、静态资源
│   └── data/         # 运行期持久化数据
├── env/              # 环境变量模板与实例
│   └── .env.example
├── prod/             # 生产部署草图（与开发环境分开演进）
│   ├── compose/
│   ├── env/
│   ├── nginx/
│   ├── scripts/
│   └── storage/
└── scripts/          # 统一入口脚本（PowerShell）
    ├── common.ps1
    ├── up-dev.ps1
    ├── up-tools.ps1
    ├── up-jenkins.ps1
    ├── up-edge.ps1
    ├── up-full.ps1
    ├── down.ps1
    ├── init-nexus.ps1
    ├── set-nginx-dev-mode.ps1
    └── set-nginx-static-mode.ps1
```

## 规则

1. **统一入口**：始终通过 `deploy/scripts/` 下的脚本启停环境，不手动拼 `docker compose` 命令。
2. **环境隔离**：开发依赖、工程工具、边缘入口分层编排，互不耦合。
3. **配置外置**：敏感信息和可变参数通过 `deploy/env/.env` 注入，不硬编码在 compose 文件中。
4. **生产分离**：`prod/` 与开发环境配置独立演进，后续可独立拆分为 deploy 仓库。
5. **幂等启动**：启停脚本需支持重复执行，状态异常时自动重建。
6. **数据持久化**：开发环境持久化数据统一放在 `docker/data/`，便于清理和重建。

## Nginx 模式切换

Nginx 支持两种前端模式：

| 模式 | 行为 | 切换命令 |
|------|------|----------|
| `static` | 直接提供 `docker/conf/nginx/html` 下的静态资源 | `.\deploy\scripts\set-nginx-static-mode.ps1` |
| `dev` | 反向代理到前端 Vite 开发服务器（`:9510`） | `.\deploy\scripts\set-nginx-dev-mode.ps1` |

## 常用命令

```powershell
# 启动开发基础设施（数据库、缓存等）
.\deploy\scripts\up-dev.ps1

# 启动工程工具（Jenkins、Nexus）
.\deploy\scripts\up-tools.ps1

# 启动边缘入口（Nginx）
.\deploy\scripts\up-edge.ps1

# 启动全部服务
.\deploy\scripts\up-full.ps1

# 停止所有服务
.\deploy\scripts\down.ps1
```

## 基础设施端口

| 服务 | 端口 | 说明 |
|------|------|------|
| PostgreSQL | `5432` | 数据库 |
| Redis | `6379` | 缓存 |
| Kafka | `9092` | 消息队列 |
| RocketMQ NameServer | `9876` | RocketMQ 注册中心 |
| Nacos | `8848` | 注册/配置中心 |
| MinIO | `9000` | 对象存储（API） |
| MinIO Console | `9001` | 对象存储（控制台） |
| Jenkins | `8080` | CI/CD |
| Nexus | `8081` | 制品仓库 |
| Nginx | `80` / `443` | 统一入口 |

## 检查清单

1. 是否通过 `scripts/` 统一入口启停环境。
2. 新增服务是否按约定放入正确的 compose 分层文件。
3. 敏感配置是否通过 `.env` 注入而非硬编码。
4. 持久化数据路径是否符合 `docker/data/` 约定。
5. 是否更新了端口列表和基础设施文档。
6. 生产配置是否与开发环境保持分离。
