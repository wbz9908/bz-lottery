# AGENTS.md — Codex 使用指南

本文件是 bz-lottery 仓库的 Codex 根入口。以后在项目根目录打开 Codex 会话时，先读取本文件，再按任务类型读取 `.agents/skills/` 下的对应技能。

## 技能路由

| 任务类型 | 读取入口 | 说明 |
| --- | --- | --- |
| 后端开发 | `.agents/skills/backend/SKILL.md` | Java 25、Spring Boot、微服务、数据库、接口与业务规则 |
| 前端开发 | `.agents/skills/frontend/SKILL.md` | Vue 3、TypeScript、Vite、接口对接与页面实现 |
| 部署与基础设施 | `.agents/skills/deploy/SKILL.md` | Docker Compose、Nginx、环境脚本、端口与生产草图 |
| Git 操作 | `.agents/skills/git-workflow/SKILL.md` | 分支、提交、推送、PR、`.gitignore` 检查 |

## 通用原则

1. 先读技能文档，再读技能中列出的 `references/` 文档，然后再修改代码或配置。
2. 只做与当前需求直接相关的最小改动，不顺手重构无关代码。
3. 不覆盖用户已有改动；发现未提交变更时，先判断是否与当前任务相关。
4. 涉及数据库结构变更时，必须使用 Flyway 迁移，并同步更新后端数据 reference。
5. 涉及接口契约变更时，同步更新 Swagger/OpenAPI 注解和对应 reference。
6. 修改后按技能要求执行验证；如无法验证，交付时明确说明原因和风险。
7. 交付内容必须包含变更范围、验证结果、未验证项和风险。

## 项目速查

| 领域 | 技术 |
| --- | --- |
| 后端语言 | Java 25 |
| 后端框架 | Spring Boot 3.5.7、Spring Cloud 2025.0.1 |
| 微服务 | Nacos、Dubbo 3.3.6、Sentinel、Spring Cloud Gateway |
| 数据 | MyBatis-Plus 3.5.12、PostgreSQL、Flyway |
| 缓存 | Redis + Redisson |
| 消息 | RocketMQ、Kafka |
| 前端 | Vue 3、TypeScript、Vite |
| 构建 | Maven 3.9+、npm |

## 常用验证命令

```powershell
# 后端全量编译
cd lottery-backend
mvn -q -DskipTests compile

# 后端指定模块编译
mvn -pl <module> -am compile

# 前端构建
cd lottery-frontend
npm run build

# 部署配置语法检查
docker compose -f deploy/compose/compose.dev-infra.yml config --quiet
```

## 文档维护

Codex 相关规范统一维护在 `.agents/skills/`。不要在 `lottery-backend/`、`lottery-frontend/`、`deploy/` 下新增独立 `.agents/` 目录。

Claude 专用文档由 Claude 维护，本次 Codex 规范不修改 `CLAUDE.md` 和 `.claude/`。
