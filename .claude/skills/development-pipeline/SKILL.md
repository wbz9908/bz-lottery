---
name: bz-lottery-development-pipeline
description: 当用户要求根据明确需求实施开发、修复问题、完成一组任务，或希望 Claude 按流程推进开发和验证时使用。
---

# bz-lottery 开发编排技能

## 使用场景

当需求已经足够明确，需要实际修改代码、配置或文档时，使用本技能。

如果需求仍不清楚，先使用 `technical-clarification`。如果用户要求提交、推送或创建 PR，再使用 `git-workflow`。

## 工作流

1. 确认需求和改动范围。
2. 检查当前工作区状态，避免覆盖用户已有改动。
3. 阅读相关代码、配置和文档，优先遵循现有项目模式。
4. 拆解任务，按风险从低到高或按依赖顺序实施。
5. 修改前说明即将编辑的文件和原因。
6. 实施最小改动，避免无关重构。
7. 根据改动类型执行验证。
8. 如发现明显风险或复杂 diff，使用 `code-review` 做提交前审查。
9. 交付变更范围、验证结果、未验证项和风险。

## 项目规则

1. 后端遵守 `controller -> service -> mapper` 分层，Controller 不直接访问 Mapper。
2. 数据库结构变更必须使用 Flyway。
3. 接口变化同步更新 Swagger/OpenAPI 注解和调用方。
4. 关键日志包含 `traceId` 和业务主键。
5. 库存、余额、抽奖结果等关键链路注意并发、幂等和一致性。
6. 前端使用 Vue 3 Composition API 和现有 HTTP 客户端。
7. 前端数据请求视图处理加载态、空态和错误态。
8. 部署配置保持开发、工具、边缘入口和生产配置分离。

## 验证策略

按改动范围选择最小有效验证：

```bash
# 后端指定模块
cd lottery-backend
mvn -pl <module> -am compile

# 后端全量
cd lottery-backend
mvn -q -DskipTests compile

# 前端
cd lottery-frontend
npm run build

# 部署
docker compose -f deploy/compose/compose.dev-infra.yml config --quiet
```

如果验证无法执行或失败，必须说明原因、影响范围和建议用户如何复核。

## 交付要求

交付内容包含：

- 变更范围
- 关键实现点
- 验证命令和结果
- 未验证项
- 风险和回滚建议
