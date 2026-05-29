---
name: bz-lottery-deploy-dev
version: "1.0.0"
description: 面向 bz-lottery 部署与基础设施的 AI 研发技能。用于 Docker Compose 编排、Nginx 配置、环境脚本维护、生产部署规划等场景。触发后先读取 .agents 规范与 reference 文档并执行。
license: internal
---

# bz-lottery 部署研发技能

## 摘要

本技能用于统一部署与基础设施的研发流程和交付质量。执行任何任务前，先读取规范，再实施改动，再执行验证，最后按统一格式交付结果。

## 规则

1. 所有新增或修改的文档文件统一使用 UTF-8 编码（建议 UTF-8 无 BOM）。
2. 按目录顺序读取 `.agents` 下规范文档：
   - `infrastructure` 目录：[deploy-rules]
   - `commit` 目录：[git-commit-guidelines]
3. 先读后改，先评估影响范围再修改配置。
4. 只做与当前需求直接相关的最小改动。
5. 涉及 Docker Compose 变更必须验证 `docker compose config` 语法正确。
6. 涉及端口或网络变更必须同步更新文档中的端口表。
7. 涉及 Nginx 配置变更必须测试两种模式（static/dev）均正常。
8. 生产配置与开发配置严格分离，禁止混入。
9. 交付输出必须包含：变更范围、验证结果、风险说明。

## 检查清单

1. 是否确认本次改动文档均为 UTF-8 编码。
2. 是否已按目录顺序读取 reference 文档。
3. 是否通过 `scripts/` 统一入口启停环境。
4. 是否验证 compose 文件语法正确。
5. 是否测试变更在目标场景下功能正常。
6. 是否在交付中说明未验证项和风险项。

## 常用命令

```powershell
# 启动开发基础设施
.\deploy\scripts\up-dev.ps1

# 启动工程工具
.\deploy\scripts\up-tools.ps1

# 启动边缘入口
.\deploy\scripts\up-edge.ps1

# 启动全部
.\deploy\scripts\up-full.ps1

# 停止全部
.\deploy\scripts\down.ps1

# 验证 compose 文件语法
docker compose -f deploy/compose/compose.dev-infra.yml config --quiet
```

## Reference

| 引用标记 | 路径 |
|----------|------|
| [deploy-rules] | `.agents/infrastructure/deploy-rules.md` |
| [git-commit-guidelines] | `.agents/commit/git-commit-guidelines.md` |
