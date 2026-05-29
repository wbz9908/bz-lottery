---
category: api
version: "1.0.0"
last_updated: "2026-05-29"
---

# API 对接规范（前端视角）

## 摘要
从前端视角描述后端 API 的约定，确保前端调用、错误处理、分页逻辑与后端一致。

## 规则
1. 所有请求经网关代理，前端请求路径使用 `/lottery-*` 前缀（Vite 代理配置）。
2. `GET` 仅查询，`POST` 创建/动作，`PUT` 全量更新，`PATCH` 局部更新，`DELETE` 删除。
3. 返回体统一结构：`code`、`message`、`data`、`timestamp`、`traceId`。
4. 前端应按 `code` 判断成功/失败，不依赖 HTTP 状态码。
5. 分页统一参数：`pageNum`、`pageSize`（后端有上限，前端传值需合理）。
6. 登录态过期需统一拦截处理，跳转登录页。
7. 动作型请求建议附带请求号（`requestNo`），支持幂等重试。
8. 错误统一展示 `message` 字段内容，`traceId` 可用于排查。

## 统一响应结构

```typescript
interface ApiResponse<T> {
  code: string       // "0000" = 成功，其他 = 错误码
  message: string    // 提示信息
  data: T            // 业务数据
  timestamp: string  // 服务器时间
  traceId: string    // 链路追踪 ID
}
```

## 错误码速查

| code | 含义 | 前端处理 |
|------|------|----------|
| `0000` | 成功 | 正常展示数据 |
| `1001` | 参数错误 | 提示用户修正输入 |
| `2001` | 未登录 | 跳转登录页 |
| `2002` | 无权限 | 提示权限不足 |
| `3001` | 业务冲突 | 提示业务规则限制 |
| `5000` | 系统错误 | 提示稍后重试，记录 traceId |

## 分页约定

```typescript
// 请求
interface PageRequest {
  pageNum: number   // 第几页，从 1 开始
  pageSize: number  // 每页条数，建议不超过 100
}

// 响应
interface PageResponse<T> {
  records: T[]
  total: number
  pageNum: number
  pageSize: number
}
```

## Vite 代理配置参考

前端开发服务器（端口 `9510`）通过 Vite 代理转发 API 请求到后端网关（`http://localhost:9008`）：

```typescript
// vite.config.ts 中的代理配置
server: {
  port: 9510,
  proxy: {
    '/lottery-*': 'http://localhost:9008',
    '/swagger-ui*': 'http://localhost:9008',
    '/v3/api-docs*': 'http://localhost:9008',
    '/actuator*': 'http://localhost:9008',
  }
}
```
