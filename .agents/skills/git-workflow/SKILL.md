---
name: bz-lottery-git-workflow
description: Use when the user asks to commit, push, create a PR, start or switch a feature branch, inspect git status, or prepare changes for review in the bz-lottery repository.
---

# bz-lottery Git Workflow Skill

## When To Use

Use this Skill for branch creation, committing, pushing, PR creation, release of working changes, `.gitignore` maintenance, and git status review.

For ordinary code edits, do not force a branch or pull unless the user asks, the task requires a clean feature branch, or the current branch/worktree state makes it necessary.

## Workflow

1. Inspect current state:
   ```powershell
   git status --short
   git branch --show-current
   ```
2. If starting a new feature branch, sync the default branch first when the worktree is clean:
   ```powershell
   git checkout main
   git pull origin main
   git checkout -b codex/YYYYMMDD_task-summary
   ```
3. Before staging, review changed and untracked files:
   ```powershell
   git diff --stat
   git status --short
   ```
4. Check for secrets and generated AI/tool files.
5. Run the relevant backend, frontend, or deploy verification when feasible.
6. Stage only intended files.
7. Commit with `type(scope): subject` and a Codex co-author trailer when Codex materially contributed.
8. Push with `git push -u origin HEAD` when requested.
9. Create PRs with `gh pr create` when requested.

## Branch Naming

Use:

```text
codex/YYYYMMDD_task-summary
```

Rules:

- `YYYYMMDD` is the current local date.
- `task-summary` is short English kebab-case or lower snake case.
- Keep the summary under about 30 characters.

Example:

```text
codex/20260611_standardize-skills
```

## Commit Messages

Use:

```text
type(scope): subject
```

Common types:

- `feat`
- `fix`
- `refactor`
- `docs`
- `test`
- `chore`

Common scopes:

- `backend`
- `frontend`
- `deploy`
- `api`
- `data`
- `docs`
- `git`

When Codex materially contributed to implementation, documentation, debugging, or the commit operation, append:

```text
Co-authored-by: Codex <noreply@openai.com>
```

Only add other AI co-author trailers when that tool actually contributed to the same commit.

## PR Body

PR descriptions should include:

```markdown
## Background

## Changes

## Verification

## Risks

## Rollback
```

Use `gh pr create --title "..." --body "..."`. Create draft PRs when the work is intentionally incomplete.

## Verification Policy

Verification is best-effort but must be explicit.

- Backend changes: prefer `mvn -pl <module> -am compile`.
- Frontend changes: prefer `npm run build`.
- Deploy changes: prefer `docker compose ... config --quiet`.

If verification fails because of environment mismatch, missing local tooling, or unrelated failures, report it clearly and ask before pushing when risk is meaningful.

## .gitignore Checks

Before staging, ensure generated tool files are not accidentally committed. Common ignored patterns include:

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

Do not silently rewrite `.gitignore` for broad patterns. If a new generated file or directory should be ignored, show the proposed `.gitignore` addition and include it only when it is clearly safe or the user confirms.

## Conflict Handling

If push is rejected because the remote moved:

```powershell
git pull --rebase origin main
git push -u origin HEAD --force-with-lease
```

Use `--force-with-lease` only after reviewing the rebase result. Never use `--force`.

## Deliverable

For commit, push, or PR tasks, report:

- Branch name
- Commit hash when created
- Pushed branch when pushed
- PR URL when created
- Verification result
- Any uncommitted or untracked files left behind
