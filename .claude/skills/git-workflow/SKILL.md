---
name: git-workflow
description: Use when the user asks to commit, push, create a PR, start a new feature branch, or says "提交代码" "创建PR" "提交PR" "推上去" "新建分支". Also use when user says "PR已审批" "PR已合并" "审批通过了" "合并了" — pull latest main. Also use before any code editing task — pull latest and create a feature branch proactively.
---

# Git 工作流

## 摘要

统一 git 操作流程：编辑前拉取最新 → 创建规范化分支 → 推送并创建 PR → 切回 main → 清理。同时自动维护 `.gitignore`，避免 AI 工具产生的文件被误提交。

## 前置检查：gh CLI

所有 PR 操作依赖 GitHub CLI (`gh`)。首次使用时先检查：

```bash
gh --version
gh auth status
```

如果 `gh` 不可用，提示用户安装：

```
❌ 未检测到 GitHub CLI (gh)，PR 创建需要它。

安装方式：
  Windows:  winget install --id GitHub.cli
  macOS:    brew install gh
  Linux:    sudo apt install gh  # (Debian/Ubuntu)
           或访问 https://cli.github.com/

安装后执行 gh auth login 登录 GitHub 账号即可使用。
```

如果 `gh auth status` 显示未登录、token 无效或认证失败，立即停止提交、推送和 PR 流程，提示用户执行 `gh auth login -h github.com` 后重试。

## 规则

1. **编辑前先同步**：任何代码修改前，先 `git pull origin main`（或当前默认分支）。
2. **分支命名规范**：`{AI名称}/{YYYYMMDD}_{任务简述}`
   - AI 名称：正在使用的 AI 工具小写名（`claude`、`codex`、`cursor` 等）
   - 日期：当天日期，格式 `YYYYMMDD`
   - 任务简述：英文小写 + 短横线，简洁描述任务（≤30 字符）
   - 示例：`claude/20260611_add-lottery-cache`、`codex/20260611_fix-auth-bug`
3. **使用 `gh` CLI** 完成提交、推送和 PR 创建（不手动操作 GitHub 网页）。`gh` 不可用时先提示安装。
4. **PR 创建后切回 main**：`git checkout main`。
5. **PR 已审批/合并**：当用户说「PR 已审批」「已合并」「审批通过了」等，执行 `git checkout main && git pull origin main` 将最新代码拉到本地。
6. **自动 .gitignore 维护**：检测到工具产生的临时文件/目录时自动补充到 `.gitignore` 并提示用户检查。
7. **提交前编译检查（best-effort）**：优先通过 IDE MCP 构建（使用 IDE 配置的 SDK/环境），系统 shell 命令作为备选。编译失败**不阻断推送**，仅给出警告提示用户自行在 IDE 中验证。详见下方「编译检查策略」。
8. **变更摘要**：push 前输出本次改动文件列表和行数统计，让用户确认。

## 检查清单

### 编辑前
- [ ] 已执行 `git pull origin main` 拉取最新代码
- [ ] 当前在 main 分支上（或确认无未提交修改后切回 main）
- [ ] `gh --version` 可用（如需创建 PR）
- [ ] `gh auth status` 已登录且 token 有效；失败时停止提交、推送和 PR 流程

### 分支与提交
- [ ] 分支名符合 `{AI}/{YYYYMMDD}_{简述}` 格式
- [ ] AI 名称已确认（当前 AI 小写名）
- [ ] 任务简述简洁准确（英文，≤30 字符）
- [ ] 提交信息符合 `type(scope): subject` 格式
- [ ] AI 参与时已追加 `Co-authored-by` 署名
- [ ] 一次提交聚焦一个逻辑主题

### 推送前
- [ ] 变更摘要已展示给用户确认
- [ ] 无敏感信息（API key、token、密码）
- [ ] 无 AI 工具产生的临时文件混入
- [ ] `.gitignore` 已检查并更新
- [ ] 编译检查已尝试（best-effort，失败不阻断，仅提醒用户在 IDE 中验证）

### PR 创建
- [ ] PR 标题清晰描述变更
- [ ] PR 正文包含：背景、变更点、验证结果、风险
- [ ] 使用 `gh pr create` 创建
- [ ] 创建后切回 main 分支
- [ ] 告知用户 PR 链接

### PR 已审批/合并（用户主动说时触发）
- [ ] 立即执行 `git checkout main && git pull origin main`
- [ ] 确认拉取成功，告知用户本地已同步最新

### PR 合并后
- [ ] 提示用户是否删除远端分支
- [ ] 提示用户是否清理本地分支

## 操作流程

### PR 审批通过后同步（简短流程）

当用户说「PR 已审批」「合并了」「审批通过了」等，直接执行：

```bash
git checkout main && git pull origin main
```

输出确认：
```
✅ 已切换到 main 并拉取最新代码。
  origin/main: abc1234 → def5678
```

### 完整流程（开始新任务）

```bash
# 0. 前置检查
gh --version  # 若不可用 → 提示安装
gh auth status  # 若未登录或 token 无效 → 提示 gh auth login

# 1. 同步最新
git checkout main && git pull origin main

# 2. 创建分支
git checkout -b claude/20260611_add-lottery-cache

# 3. ... 进行代码修改 ...

# 4. 编译检查（优先 IDE MCP，失败不阻断）
#    后端: mcp__intellij-idea__build_project
#    前端: mcp__webstorm__build_project

# 5. 变更摘要（让用户确认）
git diff --stat main...HEAD

# 6. 提交（AI 参与时追加 Co-authored-by 署名）
git add <files>
git commit -m "feat(lottery): 增加抽奖缓存层

Co-authored-by: Claude <noreply@anthropic.com>"

# 7. 推送
git push -u origin HEAD

# 8. 创建 PR
gh pr create \
  --title "feat(lottery): 增加抽奖缓存层" \
  --body "## 背景
...

## 变更点
- ...

## 验证结果
- [ ] 编译通过
- [ ] 测试通过

## 风险
- 低风险，仅新增缓存层"

# 9. 切回 main
git checkout main
```

### 仅创建 PR（已推送但未创建 PR）

```bash
gh pr create --title "..." --body "..."
git checkout main
```

## 编译检查策略

### 为什么不能用系统 shell 直接编译

AI 通过 Bash 工具执行的 `mvn` / `npm` 命令使用的是**系统环境变量**，可能与 IDE 内配置的环境不一致：

| 风险 | 说明 |
|------|------|
| JDK 版本不匹配 | 系统 `JAVA_HOME` 可能是 JDK 21，但项目需要 JDK 25 |
| Maven 配置差异 | `settings.xml`、本地仓库路径、mirror 配置不同 |
| Node 版本不对 | 系统 `node` 可能是 v18，但项目需要 v22+ |
| PATH 缺失 | `mvn` / `npm` 不在 PATH 中，命令直接失败 |
| 依赖未下载 | IDE 自动管理依赖，shell 环境可能缺包 |

**因此：编译检查是 best-effort，失败不阻断推送，仅提醒用户自行在 IDE 中验证。**

### 构建方式优先级

| 优先级 | 方式 | 后端 | 前端 | 说明 |
|--------|------|------|------|------|
| **1（推荐）** | IDE MCP | `mcp__intellij-idea__build_project` | `mcp__webstorm__build_project` | 使用 IDE 配置的 SDK，最可靠 |
| **2（备选）** | 系统 Shell | `mvn -q -DskipTests compile` | `npm run build` | 可能失败，仅作参考 |
| **3（兜底）** | 跳过 | — | — | 编译检查不可用时直接跳过 |

### 处理流程

```
尝试 IDE MCP 构建
├── 成功 → ✅ 编译通过，继续推送流程
├── 不可用（MCP 未连接/超时）→ 尝试方案 2
│   ├── 成功 → ✅ 编译通过
│   └── 失败/不可用 → ⚠️ 警告用户：无法验证编译，请在 IDE 中手动编译确认
└── 失败 → ⚠️ 警告用户：IDE 构建失败，详见构建输出。不阻断推送
```

**重要：无论编译成功或失败，都不阻断 git 操作。** 编译失败时输出以下提示：

```
⚠️ 编译检查未通过（原因：xxx）
请手动在 IDE 中编译验证后再推送，或确认忽略此警告继续。
是否继续推送？(y/n)
```

## .gitignore 自动维护

### 需要忽略的工具文件/目录

| 模式 | 来源 | 说明 |
|------|------|------|
| `.codegraph/` | CodeGraph MCP | 代码索引数据 |
| `.headroom/` | Headroom MCP | 压缩缓存 |
| `.claude/worktrees/` | Claude Code | 工作树隔离目录 |
| `.claude/scheduled_tasks.json` | Claude Code | 定时任务持久化 |
| `.claude/plans/` | Claude Code | Plan mode 输出 |
| `.claude/settings.local.json` | Claude Code | 本地设置覆盖（用户专属） |
| `.codex/` | Codex CLI | Codex 运行时文件 |
| `.cursor/` | Cursor IDE | Cursor 运行时文件 |
| `.deepseek/` | DeepSeek | DeepSeek 工具 |
| `.obsidian/` | Obsidian | 笔记工具 |
| `*.swp` / `*.swo` | Vim | 交换文件 |
| `node_modules/` | Node.js | 依赖（根目录兜底） |

### 检测与处理流程

1. 在 `git add` 之前，扫描 working tree 中是否存在应被忽略但未在 `.gitignore` 中的文件/目录。
2. 发现新文件/目录后：
   - 将其追加到 `.gitignore` 末尾
   - 添加注释说明来源和日期
   - 输出消息通知用户：「⚠️ 检测到工具产生的文件/目录 `xxx`，已自动添加到 `.gitignore`，请检查确认。」
3. 已知的通用模式（如 `*.swp`）默认添加；项目特定的工具目录（如 `.codegraph`）在首次检测到时添加。

### .gitignore 更新示例

```gitignore
# === AI & Tooling (auto-maintained) ===
.codegraph/          # CodeGraph 代码索引 (added 2026-06-11)
.headroom/           # Headroom 压缩缓存 (added 2026-06-11)
.claude/worktrees/   # Claude Code 工作树 (added 2026-06-11)
```

## 变更摘要模板

推送前输出以下摘要供用户确认：

```
📋 变更摘要
━━━━━━━━━━━━━━━━━━━━━━━━━━
分支：claude/20260611_add-lottery-cache
文件：5 changed, 2 added (+120, -15)
━━━━━━━━━━━━━━━━━━━━━━━━━━
 M  lottery-backend/lottery-lottery/src/main/java/.../LotteryService.java (+45)
 A  lottery-backend/lottery-lottery/src/main/java/.../LotteryCache.java     (+60)
 M  lottery-backend/lottery-lottery/pom.xml                                  (+5, -3)
 M  lottery-backend/lottery-common/src/main/java/.../CacheConfig.java        (+8, -7)
 M  lottery-frontend/src/api/lottery.js                                      (+2, -5)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
确认推送？(y/n)
```

## Co-authored-by 署名（AI 协作标记）

GitHub 识别提交信息中的 `Co-authored-by` 尾行，在 Contribution 图表和 PR 页面显示对应头像。

| AI 工具 | 署名行 |
|---------|--------|
| Claude / Claude Code | `Co-authored-by: Claude <noreply@anthropic.com>` |
| Codex | `Co-authored-by: Codex <noreply@openai.com>` |
| Cursor | `Co-authored-by: Cursor <noreply@cursor.com>` |
| GitHub Copilot | `Co-authored-by: GitHub Copilot <copilot@github.com>` |

```bash
git commit -m "feat(lottery): 增加抽奖缓存层

Co-authored-by: Claude <noreply@anthropic.com>"
```

## 常用命令速查

```bash
# 前置检查
gh --version             # 确认 gh CLI 可用
gh auth status           # 确认已登录且 token 有效

# 查看当前分支
git branch --show-current

# 拉取最新 main
git checkout main && git pull origin main

# 创建并切换到新分支
git checkout -b claude/20260611_desc

# 查看变更摘要
git diff --stat main...HEAD
git log main..HEAD --oneline

# 查看未跟踪文件（检查是否有工具文件混入）
git status --short | grep '^?'

# 推送新分支
git push -u origin HEAD

# 创建 PR（Draft）
gh pr create --title "..." --body "..." --draft

# 创建 PR（正式）
gh pr create --title "..." --body "..."

# 查看已有 PR
gh pr list

# PR 合并后同步本地
git checkout main && git pull origin main

# 删除远端分支（合并后）
git push origin --delete claude/20260611_desc

# 删除本地分支
git branch -d claude/20260611_desc

# 清理已合并的本地分支
git branch --merged main | grep -v main | xargs git branch -d
```

## 冲突处理

1. 如果 push 被拒（远端有新提交），执行：
   ```bash
   git pull --rebase origin main
   # 解决冲突后
   git push -u origin HEAD --force-with-lease
   ```
2. 如果 PR 有冲突，在合并前解决而非创建后忽略。

## 风险说明

- `gh` CLI 是 PR 操作的前提，不可用时先提示安装
- `--force-with-lease` 比 `--force` 更安全（会检查远端是否被其他人更新）
- 切回 main 前确保 PR 已创建成功
- `.gitignore` 自动修改需用户确认，避免误屏蔽业务文件
