# .agents — Codex 技能

本目录存放 bz-lottery 项目的 Codex 技能。根目录 `AGENTS.md` 负责选择技能；每个技能负责定义触发场景、工作流、验证要求和引用文档。

## 目录结构

```text
.agents/
├── README.md
└── skills/
    ├── backend/
    │   ├── SKILL.md
    │   └── references/
    ├── frontend/
    │   ├── SKILL.md
    │   └── references/
    ├── deploy/
    │   ├── SKILL.md
    │   └── references/
    └── git-workflow/
        └── SKILL.md
```

## 技能列表

| 技能 | 用途 |
| --- | --- |
| `backend` | 后端 Java/Spring Boot 微服务开发、接口、业务规则、数据库变更 |
| `frontend` | Vue 3 + TypeScript 前端开发、接口对接、页面和状态管理 |
| `deploy` | Docker Compose、Nginx、环境脚本、端口与部署配置 |
| `git-workflow` | 分支、提交、推送、PR、`.gitignore` 检查 |

## 维护规则

1. 每个技能必须使用 Codex Skill 标准结构：`SKILL.md` + 可选 `references/`。
2. `SKILL.md` 必须包含 YAML frontmatter，至少提供 `name` 和 `description`。
3. 领域规范放在对应技能的 `references/` 目录内，避免散落在项目子目录。
4. 新增或修改引用文档时，同步更新对应技能的引用列表。
5. 所有文档使用 UTF-8 无 BOM 编码，换行符使用 LF。
6. 不在 `lottery-backend/`、`lottery-frontend/`、`deploy/` 下维护重复的 `.agents/`。

## 引用文档模板

```yaml
---
category: <architecture|business|api|code|data|infrastructure>
version: "1.0.0"
last_updated: "YYYY-MM-DD"
---
```
