# AGENTS.md — 后端 AI 开发指南

本文件为 AI 编程助手（Claude Code、GitHub Copilot、Cursor 等）提供后端模块的开发指引。

## 规范文档

后端 AI 研发规范存放在 `lottery-backend/.agents/` 目录下，按领域拆分：

```
.agents/
├── SKILL.md                           # 技能入口：加载顺序、全局规则、交付标准
├── README.md                          # 规范文档导航与维护说明
├── architecture/
│   └── architecture-rules.md          # 架构规范：模块边界、分层约束、跨服务调用
├── business/
│   └── business-rules.md              # 业务规则：抽奖链路核心口径
├── api/
│   └── api-spec.md                    # API 规范：路径、请求/响应体、错误码、鉴权
├── code/
│   ├── coding-standards.md            # 编码规范：命名、分层、异常、日志、测试
│   └── java-dev.md                    # Java 开发规范：Lombok、并发、N+1、校验、SQL 等
├── data/
│   ├── data-baseline.md               # 数据基线：Flyway 迁移、约束、幂等规则
│   ├── ddl-all-tables.md              # 全量表结构 DDL：所有建表语句（可直接执行）
│   └── schema-index.md                # 表结构索引：所有表的用途、关系、索引概览
└── commit/
    └── git-commit-guidelines.md       # 提交规范：分支、信息格式、PR 要求
```

## 工作流

AI 助手执行后端任务时，应按以下顺序加载规范：

1. `architecture/` → 确认改动不破坏模块边界和分层约束
2. `business/` → 确认业务规则校验完整
3. `api/` → 确认接口设计符合规范
4. `code/` → 编码实现（命名、异常、日志、性能）
5. `data/` → 如涉及数据库变更，编写 Flyway 脚本并更新 DDL 文档
6. `commit/` → 生成规范的提交信息

## 关键原则

- **先读规范，再写代码** — 执行任何任务前必须先读取对应的 reference 文档
- **最小改动** — 只做与当前需求直接相关的最小改动，不顺手重构无关代码
- **数据库变更必须走 Flyway** — 禁止绕过迁移脚本直接改库，同步更新 `.agents/data/` 下的 DDL 和索引文档
- **接口变更必须更新文档** — API 改动同步更新 Swagger/OpenAPI 注解
- **先编译验证** — 改动后至少执行 `mvn -pl <module> compile` 确认无编译错误
- **关键路径加 traceId** — 日志中携带 `traceId` 与业务主键，便于排查
- **交付明确** — 每次交付必须说明变更范围、验证结果、未验证项和风险

## 技术栈速查

| 领域 | 技术 |
|------|------|
| 语言 | Java 25 |
| 框架 | Spring Boot 3.5.7、Spring Cloud 2025.0.1 |
| 微服务 | Nacos、Dubbo 3.3.6、Sentinel、Spring Cloud Gateway |
| 数据 | MyBatis-Plus 3.5.12、PostgreSQL、Flyway |
| 缓存 | Redis + Redisson |
| 消息 | RocketMQ、Kafka |
| 构建 | Maven 3.9+ |
