---
category: code
version: "1.1.0"
last_updated: "2026-05-29"
---

# 编码规范

## 摘要
统一命名、分层、异常、日志、测试和性能实践，保障代码可读性与可维护性。

## 规则
1. 命名规范：
   - 类名大驼峰，方法名小驼峰，常量全大写下划线。
2. 分层规范：
   - `controller` 只处理协议层；
   - `service` 处理业务编排；
   - `repository/mapper` 处理数据访问。
3. 统一异常：
   - 业务异常和系统异常分离；
   - 统一全局异常处理。
4. 统一响应：
   - 使用项目统一响应对象，禁止接口各自定义返回结构。
5. 日志规范：
   - 关键日志带 `traceId` 与业务主键；
   - 敏感信息脱敏。
6. 性能规范：
   - 高频路径避免循环内远程调用；
   - 列表接口必须分页。
7. 测试规范：
   - 行为变更必须补测试；
   - 缺陷修复必须补回归用例。

## 检查清单
1. 是否符合命名规范和分层规范。
2. 是否引入重复逻辑可复用到 common。
3. 是否处理空值、边界值、并发竞争。
4. 是否补充必要测试与注释。
5. 是否存在无意义日志或敏感信息输出。

## 示例
```text
正确：
UserController -> UserService -> UserRepository

错误：
UserController 直接调用 UserMapper；
catch 后吞异常且不记录 traceId。
```

## 注释规范

### 核心原则

**注释解释 WHY，不解释 WHAT。** 代码通过命名描述"做什么"，注释负责说明"为什么这样做"——隐藏的约束、非显而易见的意图、违反直觉的设计决策。

如果删掉注释不会让未来的读者困惑，就不写。

### 应该注释的场景

| 场景 | 示例 |
|------|------|
| 非显而易见的 workaround 或 API 规避 | Spring CORS 不允许 `*` + `allowCredentials`，改用 `allowedOriginPatterns` |
| 违反直觉的反直觉设计决策 | 策略配置 typo 时静默降级为默认策略，而非报错 |
| 魔法数字的业务含义 | `prizeLevelSort <= 2` 表示高等级奖品需要人工审核 |
| 并发/线程安全相关设计 | `volatile` 字段保证跨线程策略切换可见性 |
| `@SuppressWarnings` 的原因 | 标注为什么警告被抑制以及确保安全的分析 |

### 不应注释的场景

- 代码通过命名已清晰表达意图的方法或变量
- 标准 Java 模式（try-with-resources、try-finally、构造注入）
- setter/getter、简单 DTO（Record）、标准 Controller、空 Mapper 接口
- 谁写的、什么时候写的、关联的工单号（这些属于 git blame 和 PR）

### 格式

```java
// 优先使用单行注释说明 WHY

// 非显而易见：Spring 的 StringHttpMessageConverter 无法序列化 ApiResponse 对象，
// 当 controller 返回 String 类型时，需要手动序列化为 JSON 字符串
String json = objectMapper.writeValueAsString(ApiResponse.success(body));
```

- 使用 `//` 单行注释，避免 JavaDoc `/** */` 块和 `@param`/`@return` 标签
- 注释放在要说明的代码块上方，保持与代码一致的缩进
- 注释内容使用中文，保持与项目其他规范文档一致

## 详细参考
Java 详细开发规范（命名约定、DTO 规范、批量查询、N+1 防范、并发安全、异常处理、
输入校验、Spring Boot 最佳实践等）见 [java-dev](java-dev.md)。
