# AGENTS.md — 部署 AI 开发指南

本文件为 AI 编程助手（Claude Code、GitHub Copilot、Cursor 等）提供部署与基础设施的开发指引。

## 规范文档

部署 AI 研发规范存放在 `deploy/.agents/` 目录下，按领域拆分：

```
.agents/
├── SKILL.md                        # 技能入口：加载顺序、全局规则、交付标准
├── README.md                       # 规范文档导航与维护说明
├── infrastructure/
│   └── deploy-rules.md             # 部署规范：Docker Compose 编排、Nginx 配置、端口约定
└── commit/
    └── git-commit-guidelines.md    # 提交规范：分支、信息格式、PR 要求
```

## 工作流

AI 助手执行部署相关任务时，应按以下顺序加载规范：

1. `infrastructure/` → 确认改动符合目录结构、分层约定和端口规划
2. `commit/` → 生成规范的提交信息

## 关键原则

- **先读规范，再写配置** — 执行任何任务前必须先读取对应的 reference 文档
- **统一入口** — 始终通过 `deploy/scripts/` 下的脚本启停环境，不手动拼 `docker compose` 命令
- **配置外置** — 敏感信息和可变参数通过 `deploy/env/.env` 注入，不硬编码在 compose 文件中
- **生产分离** — `deploy/prod/` 与开发环境严格分离，独立演进，不混入开发配置
- **幂等启停** — 启停脚本需支持重复执行，状态异常时自动重建
- **语法验证** — Docker Compose 变更后执行 `docker compose config --quiet` 验证
- **交付明确** — 每次交付必须说明变更范围、验证结果、未验证项和风险

## 目录速查

| 路径 | 用途 |
|------|------|
| `deploy/compose/` | Docker Compose 编排文件（dev-infra / dev-tools / edge 三层） |
| `deploy/docker/build/` | Dockerfile 和构建期文件 |
| `deploy/docker/conf/` | 运行期配置、初始化脚本、静态资源 |
| `deploy/docker/data/` | 运行期持久化数据 |
| `deploy/env/` | 环境变量模板与实例 |
| `deploy/scripts/` | 统一启停入口（PowerShell） |
| `deploy/prod/` | 生产部署草图（与开发环境分离） |
