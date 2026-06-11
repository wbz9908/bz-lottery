# .agents — Codex 技能

本目录存放 bz-lottery 项目的 Codex 技能。技能按“工作流/能力”组织，而不是按前端、后端、部署目录组织。

## 目录结构

```text
.agents/
├── README.md
└── skills/
    ├── technical-clarification/
    │   └── SKILL.md
    ├── development-pipeline/
    │   └── SKILL.md
    ├── code-review/
    │   └── SKILL.md
    └── git-workflow/
        └── SKILL.md
```

## 技能列表

| 技能 | 用途 |
| --- | --- |
| `technical-clarification` | 需求不清或改动较大时，先产出技术澄清文档 |
| `development-pipeline` | 根据明确需求执行开发、验证和必要修正 |
| `code-review` | 对当前 diff 或待提交改动做代码审查 |
| `git-workflow` | 分支、提交、推送、PR、`.gitignore` 检查 |

## 维护规则

1. 每个技能必须使用 Codex Skill 标准结构：`SKILL.md` + 可选 `references/`、`scripts/`、`assets/`。
2. `SKILL.md` 必须包含 YAML frontmatter，至少提供 `name` 和 `description`。
3. `description` 要写清楚触发场景，避免过宽导致无关任务误触发。
4. 技能正文描述工作流、输入、输出、验证和风险处理。
5. 长篇规范、模板或检查表可放到技能自己的 `references/` 目录。
6. 所有文档使用 UTF-8 无 BOM 编码，换行符使用 LF。
7. 不在 `lottery-backend/`、`lottery-frontend/`、`deploy/` 下维护重复的 `.agents/`。
