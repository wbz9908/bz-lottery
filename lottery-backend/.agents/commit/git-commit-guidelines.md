---
category: commit
version: "1.2.0"
last_updated: "2026-06-06"
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
- 当 AI 工具实际参与实现、文档编写、排查问题或提交操作时，在提交信息末尾追加对应的协作署名：

```text
Co-authored-by: Codex <noreply@openai.com>
Co-authored-by: Claude <noreply@anthropic.com>
```

- 多个 AI 或真人共同参与同一个提交时，可以追加多行 `Co-authored-by`。
- 仅在对应工具实际参与该提交时添加署名；不要为了展示效果给未参与的工具署名。
- 若工具没有官方或稳定的 noreply 邮箱，可使用项目约定邮箱，但 GitHub Contributors 是否展示取决于 GitHub 是否能识别该邮箱。

1. 提交粒度：

- 一次提交聚焦一个逻辑主题；
- 禁止混入无关改动。

1. 提交前校验：

- 编译通过；
- 关键测试通过；
- 无敏感信息泄漏。

1. PR 要求：

- 写清背景、变更点、验证结果、风险与回滚方案。

## 检查清单

1. 是否在正确分支上提交。
2. 提交信息是否可读可检索。
3. 是否包含无关文件或临时文件。
4. 是否附带必要验证说明。
5. 是否给出风险和回滚路径。
6. AI 参与的提交是否补充对应 `Co-authored-by` 署名。

## AI 协作署名参考

| 工具 | 建议署名 | 说明 |
|------|----------|------|
| Codex | `Co-authored-by: Codex <noreply@openai.com>` | 用于 Codex 实际参与的提交 |
| Claude | `Co-authored-by: Claude <noreply@anthropic.com>` | 用于 Claude / Claude Code 实际参与的提交 |
| Cursor | `Co-authored-by: Cursor <noreply@cursor.com>` | 如项目需要展示 Cursor 参与，可采用项目约定 |
| GitHub Copilot | `Co-authored-by: GitHub Copilot <copilot@github.com>` | 如提交主要由 Copilot 辅助完成，可采用项目约定 |
| 其他 AI | `Co-authored-by: Tool Name <noreply@example.com>` | 先确认工具是否有官方推荐邮箱；没有则使用团队约定 |

## 示例

```text
feat(lottery): 增加抽奖请求幂等校验
fix(user): 修复登录态过期后接口返回码错误
docs(api): 补充抽奖接口错误码说明

feat(lottery): 增加抽奖请求幂等校验

Co-authored-by: Codex <noreply@openai.com>
```
