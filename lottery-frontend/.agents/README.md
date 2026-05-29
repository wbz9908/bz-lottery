# .agents — bz-lottery 前端 AI 研发规范

本目录存放面向 AI 编程助手的前端研发规范与参考文档。所有文档按领域拆分，由 `SKILL.md` 统一编排加载顺序。

## 目录结构

```
.agents/
├── SKILL.md                     # 技能入口：加载顺序、全局规则、交付标准
├── README.md                    # 本文件：目录导航与维护说明
├── .gitignore                   # Obsidian 工作区元数据忽略
├── api/
│   └── api-spec.md              # API 对接规范：响应结构、错误码、分页、代理配置
├── code/
│   └── frontend-dev.md          # 前端开发规范：Vue 3、TypeScript、组件、状态管理
└── commit/
    └── git-commit-guidelines.md # 提交规范：分支、信息格式、PR 要求
```

## 文档分类

| 领域 | 文档 | 用途 |
|------|------|------|
| 入口 | `SKILL.md` | AI 技能定义、加载顺序、全局规则、交付标准 |
| 接口 | `api/api-spec.md` | 前端视角的 API 对接规范、响应结构、错误码处理 |
| 编码 | `code/frontend-dev.md` | Vue 3 + TypeScript 命名、组件、状态管理、样式 |
| 提交 | `commit/git-commit-guidelines.md` | 分支命名、提交信息、PR 模板 |

## AI 辅助开发工作流

当使用 AI 编程助手（如 Claude Code、GitHub Copilot 等）进行前端开发时，助手的 `SKILL.md` 会被自动加载，确保 AI 生成代码遵循项目规范：

1. **接口对接阶段** → AI 先读取 [api-spec] 了解后端 API 契约
2. **编码实现阶段** → AI 严格按照 [frontend-dev] 编写 Vue 3 组件和 TypeScript 代码
3. **提交阶段** → AI 参照 [git-commit-guidelines] 生成规范的提交信息

## 维护规范

1. **新增规范文档**：在对应领域目录下新建 `.md` 文件，使用统一的 YAML frontmatter 模板，同步更新 `SKILL.md` 的 Reference 章节和本 README。
2. **修改现有规范**：先评估影响范围，更新 `last_updated` 字段，必要时在文档内标注变更记录。
3. **编码**：所有文档使用 UTF-8 无 BOM 编码，换行符 LF。

## 文档模板

所有规范文档（SKILL.md 除外）使用统一的 YAML frontmatter：

```yaml
---
category: <api|code|commit>
version: "1.0.0"
last_updated: "YYYY-MM-DD"
---
```
