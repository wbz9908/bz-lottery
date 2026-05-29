# AGENTS.md — 前端 AI 开发指南

本文件为 AI 编程助手（Claude Code、GitHub Copilot、Cursor 等）提供前端模块的开发指引。

## 规范文档

前端 AI 研发规范存放在 `lottery-frontend/.agents/` 目录下，按领域拆分：

```
.agents/
├── SKILL.md                     # 技能入口：加载顺序、全局规则、交付标准
├── README.md                    # 规范文档导航与维护说明
├── api/
│   └── api-spec.md              # 前端视角的 API 对接规范（响应结构、错误码、分页）
├── code/
│   └── frontend-dev.md          # 前端开发规范（Vue 3、TypeScript、组件、状态管理）
└── commit/
    └── git-commit-guidelines.md # 提交规范：分支、信息格式、PR 要求
```

## 工作流

AI 助手执行前端任务时，应按以下顺序加载规范：

1. `api/` → 确认接口调用方式、响应结构、错误码处理与后端一致
2. `code/` → 编码实现（Composition API、TypeScript、组件结构、状态管理）
3. `commit/` → 生成规范的提交信息

## 关键原则

- **先读规范，再写代码** — 执行任何任务前必须先读取对应的 reference 文档
- **统一 API 调用** — 所有请求使用封装的 http 客户端，不直接裸调 axios/fetch
- **三态处理** — 每个数据组件必须处理加载态（loading）、空态（empty）、错误态（error）
- **Composition API** — 统一使用 `<script setup lang="ts">` 语法，不用 Options API
- **类型安全** — 启用 TypeScript 严格模式，避免 `any` 泛滥
- **构建验证** — 改动后执行 `npm run build` 确认无编译错误
- **交付明确** — 每次交付必须说明变更范围、验证结果、未验证项和风险

## 技术栈速查

| 领域 | 技术 |
|------|------|
| 框架 | Vue 3（Composition API） |
| 语言 | TypeScript |
| 构建 | Vite |
| 路由 | Vue Router |
| 状态管理 | Pinia |
| HTTP 客户端 | Axios（封装） |
| 样式 | CSS Modules / `<style scoped>` |
