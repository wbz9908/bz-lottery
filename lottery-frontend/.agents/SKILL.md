---
name: bz-lottery-frontend-dev
version: "1.0.0"
description: 面向 bz-lottery 前端项目的 AI 研发技能。用于 Vue 3 + TypeScript + Vite 前端开发、接口对接、组件开发、状态管理等场景。触发后先读取 .agents 规范与 reference 文档并执行。
license: internal
---

# bz-lottery 前端研发技能

## 摘要

本技能用于统一前端研发流程和交付质量。执行任何任务前，先读取规范，再实施改动，再执行验证，最后按统一格式交付结果。

## 规则

1. 所有新增或修改的文档文件统一使用 UTF-8 编码（建议 UTF-8 无 BOM）。
2. 按目录顺序读取 `.agents` 下规范文档：
   - `api` 目录：[api-spec]
   - `code` 目录：[frontend-dev]
   - `commit` 目录：[git-commit-guidelines]
3. 先读后改，先评估影响范围再修改代码。
4. 只做与当前需求直接相关的最小改动。
5. 涉及接口对接必须先确认后端 API 契约（可参考 `api/api-spec.md`）。
6. 涉及路由变更必须同步检查导航菜单和权限配置。
7. 交付输出必须包含：变更范围、验证结果、风险说明。

## 检查清单

1. 是否确认本次改动文档均为 UTF-8 编码。
2. 是否已按目录顺序读取 reference 文档。
3. 是否使用 Composition API（`<script setup lang="ts">`）。
4. 是否处理了加载态、空态、错误态。
5. 是否使用统一封装的 HTTP 客户端进行 API 调用。
6. 是否执行 `npm run build` 验证无编译错误。
7. 是否在交付中说明未验证项和风险项。

## 开发命令速查

```bash
# 安装依赖
npm install

# 启动开发服务器（端口 9510）
npm run dev

# 生产构建
npm run build

# 预览生产构建（端口 9511）
npm run preview
```

## Reference

| 引用标记 | 路径 |
|----------|------|
| [api-spec] | `.agents/api/api-spec.md` |
| [frontend-dev] | `.agents/code/frontend-dev.md` |
| [git-commit-guidelines] | `.agents/commit/git-commit-guidelines.md` |
