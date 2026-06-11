---
name: bz-lottery-git-workflow
description: 当用户要求提交、推送、创建 PR、新建或切换功能分支、查看 git 状态，或准备 bz-lottery 仓库的评审变更时使用。
---

# bz-lottery Git 工作流技能

## 使用场景

当需要创建分支、提交、推送、创建 PR、整理待发布改动、维护 `.gitignore` 或检查 git 状态时，使用本技能。

普通代码编辑任务中，不要强制创建分支或拉取代码；只有用户要求、任务需要干净功能分支，或当前分支/工作区状态确实需要时才执行。

## 工作流

1. 检查当前状态：
   ```powershell
   git status --short
   git branch --show-current
   ```
2. 如果要新建功能分支，并且工作区干净，先同步默认分支：
   ```powershell
   git checkout main
   git pull origin main
   git checkout -b codex/YYYYMMDD_task-summary
   ```
3. 除非用户明确要求“直接提交到 main”，否则不要在 `main` 上提交业务或文档改动；应创建 `codex/YYYYMMDD_task-summary` 分支并推送该分支。
4. 暂存前检查已修改和未跟踪文件：
   ```powershell
   git diff --stat
   git status --short
   ```
5. 检查是否存在密钥或 AI/工具生成文件。
6. 可行时执行对应后端、前端或部署验证。
7. 只暂存本次任务需要提交的文件。
8. 使用 `type(scope): subject` 提交；当 Codex 实际参与时追加协作署名。
9. 用户要求推送时，使用 `git push -u origin HEAD` 推送当前功能分支。
10. 用户要求创建 PR 时，使用 `gh pr create`；没有明确要求直推主干时，优先通过 PR 合入。

## 分支命名

使用：

```text
codex/YYYYMMDD_task-summary
```

规则：

- `YYYYMMDD` 为当前本地日期。
- `task-summary` 使用简短英文，采用 kebab-case 或小写下划线。
- 简述尽量控制在 30 个字符以内。

示例：

```text
codex/20260611_standardize-skills
```

## 提交信息

使用：

```text
type(scope): subject
```

常用类型：

- `feat`
- `fix`
- `refactor`
- `docs`
- `test`
- `chore`

常用 scope：

- `backend`
- `frontend`
- `deploy`
- `api`
- `data`
- `docs`
- `git`

当 Codex 实际参与实现、文档编写、问题排查或提交操作时，追加：

```text
Co-authored-by: Codex <noreply@openai.com>
```

只有其他 AI 工具实际参与同一个提交时，才追加对应协作署名。

## PR 正文

PR 描述应包含：

```markdown
## 背景

## 变更

## 验证

## 风险

## 回滚
```

使用 `gh pr create --title "..." --body "..."`。如果工作有意保持未完成状态，则创建草稿 PR。

## 验证策略

验证是 best-effort，但必须明确说明。

- 后端变更：优先执行 `mvn -pl <module> -am compile`。
- 前端变更：优先执行 `npm run build`。
- 部署变更：优先执行 `docker compose ... config --quiet`。

如果因为环境不匹配、本地工具缺失或无关失败导致验证失败，需要明确说明；当风险明显时，推送前先让用户确认。

## .gitignore 检查

暂存前确认生成类工具文件没有被误提交。常见忽略模式包括：

```gitignore
.codegraph/
.headroom/
.codex/
.claude/worktrees/
.claude/scheduled_tasks.json
.claude/plans/
.claude/settings.local.json
.cursor/
.obsidian/
*.swp
*.swo
node_modules/
```

忽略规则必须一行只写一个模式；注释单独一行，不要把说明写在模式后面。错误示例：

```gitignore
.codegraph/          # CodeGraph MCP 代码索引
```

正确示例：

```gitignore
# CodeGraph MCP 代码索引
.codegraph/
```

不要为宽泛模式静默改写 `.gitignore`。如果发现新的生成文件或目录应被忽略，先展示建议添加项；只有明显安全或用户确认后才写入。对于 `.codegraph/`、`.codex/`、`.headroom/` 等已知 AI 工具运行目录，可以直接补充标准忽略规则，并在交付说明中标明。

## 冲突处理

如果推送因远端有新提交而被拒绝：

```powershell
git pull --rebase origin main
git push -u origin HEAD --force-with-lease
```

只有检查 rebase 结果后才使用 `--force-with-lease`。不要使用 `--force`。

## 交付要求

提交、推送或 PR 任务需要说明：

- 分支名
- 已创建提交时的 commit hash
- 已推送的分支
- 已创建 PR 时的 PR 链接
- 验证结果
- 是否仍有未提交或未跟踪文件
