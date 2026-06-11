---
category: code
version: "1.0.0"
last_updated: "2026-05-29"
---

# 前端开发规范

## 摘要
统一前端命名、组件、状态管理、样式和测试实践，保障代码可读性与可维护性。

## 技术栈

- Vue 3（Composition API + `<script setup>`）
- TypeScript
- Vite
- Vue Router
- Pinia（状态管理）

## 工具链

```bash
npm install          # 安装依赖
npm run dev          # 启动开发服务器（端口 9510）
npm run build        # 生产构建
npm run preview      # 预览生产构建（端口 9511）
```

## 命名约定

| 类型 | 规则 | 示例 |
|------|------|------|
| 组件文件 | PascalCase | `UserProfile.vue`, `LotteryDraw.vue` |
| 组合式函数 | `use` 前缀 + camelCase | `useUserStore()`, `useLotteryApi()` |
| 路由路径 | kebab-case | `/user-profile`, `/lottery-draw` |
| 路由名称 | camelCase | `userProfile`, `lotteryDraw` |
| TypeScript 接口 | PascalCase, `I` 前缀可选 | `UserInfo`, `ApiResponse<T>` |
| 常量 | UPPER_SNAKE_CASE | `API_BASE_URL`, `MAX_PAGE_SIZE` |

## 组件结构

```vue
<script setup lang="ts">
// 1. 导入
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import type { UserInfo } from '@/types'

// 2. Props & Emits
const props = defineProps<{
  userId: number
}>()

const emit = defineEmits<{
  update: [user: UserInfo]
}>()

// 3. 响应式状态
const loading = ref(false)
const user = ref<UserInfo | null>(null)

// 4. 计算属性
const displayName = computed(() => user.value?.nickname ?? '未知用户')

// 5. 方法
async function fetchUser() {
  loading.value = true
  try {
    user.value = await api.getUser(props.userId)
  } finally {
    loading.value = false
  }
}

// 6. 生命周期
onMounted(() => fetchUser())
</script>

<template>
  <div v-if="loading">加载中...</div>
  <div v-else-if="user">{{ displayName }}</div>
  <div v-else>用户不存在</div>
</template>

<style scoped>
/* 组件样式 */
</style>
```

## API 调用规范

### 统一封装

```typescript
// api/client.ts — 统一 HTTP 客户端
import axios from 'axios'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '',
  timeout: 10000,
})

// 请求拦截：添加 token 和 traceId
http.interceptors.request.use(config => {
  const token = useAuthStore().token
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截：统一错误处理
http.interceptors.response.use(
  response => {
    const { code, message, data } = response.data
    if (code !== '0000') {
      if (code === '2001') { /* 未登录 → 跳转登录 */ }
      throw new ApiError(code, message)
    }
    return data
  },
  error => { /* 网络错误处理 */ }
)
```

### 按模块拆分 API

```typescript
// api/lottery.ts
import http from './client'
import type { DrawRequest, DrawRecord } from '@/types'

export const lotteryApi = {
  draw: (req: DrawRequest) => http.post<DrawRecord>('/lottery-lottery/api/lottery/draw', req),
  records: (params: PageRequest) => http.get<PageResponse<DrawRecord>>('/lottery-lottery/api/lottery/records', { params }),
}
```

## 状态管理（Pinia）

```typescript
// stores/user.ts
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', () => {
  const user = ref<UserInfo | null>(null)
  const isLoggedIn = computed(() => !!user.value)

  async function login(username: string, password: string) {
    user.value = await authApi.login({ username, password })
  }

  function logout() {
    user.value = null
  }

  return { user, isLoggedIn, login, logout }
})
```

## 样式规范

| 规则 | 说明 |
|------|------|
| ✅ 使用 `<style scoped>` | 组件样式隔离，避免全局污染 |
| ✅ CSS 变量定义主题色 | `var(--primary-color)` |
| ❌ 禁止内联样式 | 除非动态计算值 |
| ❌ 禁止 `!important` | 通过选择器优先级解决 |

## 错误处理

```typescript
// ❌ 差：不处理错误
const data = await api.fetchUser(id)

// ✅ 好：正确处理加载/空/错误三态
try {
  loading.value = true
  error.value = null
  data.value = await api.fetchUser(id)
} catch (e) {
  error.value = e instanceof ApiError ? e.message : '网络异常，请稍后重试'
} finally {
  loading.value = false
}
```

## 约束检查清单

1. 是否使用 Composition API（`<script setup>`）。
2. 是否启用 TypeScript 类型检查。
3. 组件是否保持单一职责。
4. API 调用是否统一使用封装的 http 客户端。
5. 是否处理加载态、空态、错误态。
6. 分页列表是否设置合理的 `pageSize`（建议 20，不超过 100）。
7. 用户操作后是否有适当反馈（Toast/Message）。
8. 是否存在未处理的 promise rejection。
