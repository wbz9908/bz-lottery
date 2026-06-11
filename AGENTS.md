# AGENTS.md — Codex 使用指南

本文件是 bz-lottery 仓库的 Codex 根入口。以后在项目根目录打开 Codex 会话时，先读取本文件，再根据任务目标选择 `.agents/skills/` 下的工作流型技能。

## 技能路由

| 任务目标 | 读取入口 | 说明 |
| --- | --- | --- |
| 需求不清、改动较大、需要先定方案 | `.agents/skills/technical-clarification/SKILL.md` | 生成技术澄清文档，明确范围、方案、风险和验证 |
| 根据明确需求执行开发 | `.agents/skills/development-pipeline/SKILL.md` | 拆解任务、实施改动、验证、必要时触发审查 |
| 审查当前改动或提交前检查 | `.agents/skills/code-review/SKILL.md` | 基于当前 diff 做问题优先的代码审查 |
| Git 操作 | `.agents/skills/git-workflow/SKILL.md` | 分支、提交、推送、PR、`.gitignore` 检查 |

## 通用原则

1. 优先选择最贴近任务目标的技能；不要因为涉及某个目录就创建“后端/前端/部署”领域技能。
2. 技能负责工作流，项目技术约束写在本文件或具体技能的检查项里。
3. 只做与当前需求直接相关的最小改动，不顺手重构无关代码。
4. 不覆盖用户已有改动；发现未提交变更时，先判断是否与当前任务相关。
5. 涉及数据库结构变更时，必须使用 Flyway 迁移，并同步更新相关文档或说明。
6. 涉及接口契约变更时，同步更新 Swagger/OpenAPI 注解和前后端调用。
7. 修改后按技能要求执行验证；如无法验证，交付时明确说明原因和风险。
8. 交付内容必须包含变更范围、验证结果、未验证项和风险。

## 项目约束

| 领域 | 要求 |
| --- | --- |
| 后端 | Java 25、Spring Boot 3.5.7、Spring Cloud 2025.0.1、Dubbo 3.3.6 |
| 数据 | PostgreSQL、MyBatis-Plus 3.5.12、Flyway；结构变更必须走迁移脚本 |
| 缓存与消息 | Redis + Redisson、RocketMQ、Kafka |
| 前端 | Vue 3、TypeScript、Vite；使用现有 HTTP 客户端和路由守卫 |
| 部署 | Docker Compose、Nginx、PowerShell 脚本；配置和密钥外置 |
| 日志 | 关键路径携带 `traceId` 和业务主键 |
| 并发 | 库存和余额类变更使用原子更新或等效并发保护 |

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

Claude 专用文档由 Claude 维护；Codex 规范不修改 `CLAUDE.md` 和 `.claude/`。
