---
category: commit
version: "1.0.0"
last_updated: "2026-05-29"
---

# 提交规范

## 摘要

统一分支、提交和 PR 质量标准，确保改动可追溯、可评审、可回滚。

## 规则

1. 分支规范：

- 默认使用 `codex/` 前缀；
- 命名建议：`codex/{module}-{task}`。

1. 提交信息：

- 建议 `type(scope): subject`；
- 常用类型：`feat`、`fix`、`refactor`、`docs`、`test`、`chore`。

1. 提交粒度：

- 一次提交聚焦一个逻辑主题；
- 禁止混入无关改动。

1. 提交前校验：

- compose 文件语法正确（`docker compose config` 验证）；
- 脚本在目标环境测试通过；
- 无敏感信息泄漏（凭证、密钥、内部地址）。

1. PR 要求：

- 写清背景、变更点、验证结果、风险与回滚方案。

## 部署常用 scope

| scope | 说明 |
|-------|------|
| `deploy` | 部署通用改动 |
| `compose` | Docker Compose 编排变更 |
| `nginx` | Nginx 配置变更 |
| `script` | 部署脚本变更 |
| `prod` | 生产环境配置变更 |

## 检查清单

1. 是否在正确分支上提交。
2. 提交信息是否可读可检索。
3. 是否包含无关文件或临时文件。
4. 是否附带必要验证说明。
5. 是否给出风险和回滚路径。

## 示例

```text
feat(compose): 新增 Redis Sentinel 高可用编排
fix(nginx): 修复前端静态资源缓存策略错误
chore(deploy): 升级 PostgreSQL 镜像版本到 16
docs(prod): 补充生产环境部署检查清单
```
