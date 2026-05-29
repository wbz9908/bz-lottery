# .agents — bz-lottery 部署 AI 研发规范

本目录存放面向 AI 编程助手的部署与基础设施规范文档。所有文档按领域拆分，由 `SKILL.md` 统一编排加载顺序。

## 目录结构

```
.agents/
├── SKILL.md                          # 技能入口：加载顺序、全局规则、交付标准
├── README.md                         # 本文件：目录导航与维护说明
├── .gitignore                        # Obsidian 工作区元数据忽略
├── infrastructure/
│   └── deploy-rules.md               # 部署规范：Docker Compose、Nginx、环境变量
└── commit/
    └── git-commit-guidelines.md      # 提交规范：分支、信息格式、PR 要求
```

## 文档分类

| 领域 | 文档 | 用途 |
|------|------|------|
| 入口 | `SKILL.md` | AI 技能定义、加载顺序、全局规则、交付标准 |
| 基础设施 | `infrastructure/deploy-rules.md` | Docker Compose 编排、Nginx 配置、端口约定、启停流程 |
| 提交 | `commit/git-commit-guidelines.md` | 分支命名、提交信息、PR 模板 |

## AI 辅助开发工作流

当使用 AI 编程助手（如 Claude Code、GitHub Copilot 等）进行部署配置开发时，助手的 `SKILL.md` 会被自动加载，确保 AI 生成配置遵循项目规范：

1. **编排变更阶段** → AI 先读取 [deploy-rules] 了解目录结构和端口约定
2. **脚本维护阶段** → AI 遵循分层约定，保持统一入口模式
3. **提交阶段** → AI 参照 [git-commit-guidelines] 生成规范的提交信息

## 维护规范

1. **新增规范文档**：在对应领域目录下新建 `.md` 文件，使用统一的 YAML frontmatter 模板，同步更新 `SKILL.md` 的 Reference 章节和本 README。
2. **修改现有规范**：先评估影响范围，更新 `last_updated` 字段，必要时在文档内标注变更记录。
3. **端口变更**：新增服务或修改端口后同步更新 `deploy-rules.md` 中的端口表。
4. **编码**：所有文档使用 UTF-8 无 BOM 编码，换行符 LF。

## 文档模板

所有规范文档（SKILL.md 除外）使用统一的 YAML frontmatter：

```yaml
---
category: <infrastructure|commit>
version: "1.0.0"
last_updated: "YYYY-MM-DD"
---
```
