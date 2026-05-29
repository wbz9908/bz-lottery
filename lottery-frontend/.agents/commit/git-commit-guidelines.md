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

- 编译通过（`npm run build` 无报错）；
- 关键功能手工验证通过；
- 无敏感信息泄漏。

1. PR 要求：

- 写清背景、变更点、验证结果、风险与回滚方案。

## 前端常用 scope

| scope | 说明 |
|-------|------|
| `frontend` | 前端通用改动 |
| `ui` | UI 组件或样式改动 |
| `api` | 接口对接相关 |
| `router` | 路由相关 |
| `store` | 状态管理相关 |

## 检查清单

1. 是否在正确分支上提交。
2. 提交信息是否可读可检索。
3. 是否包含无关文件或临时文件（如 `.env.local`、`node_modules`）。
4. 是否附带必要验证说明。
5. 是否给出风险和回滚路径。

## 示例

```text
feat(frontend): 新增抽奖页面及抽奖动画
fix(ui): 修复奖品列表分页切换后数据不刷新
refactor(api): 统一错误拦截和登录态检测
docs(frontend): 补充前端环境变量配置说明
```
