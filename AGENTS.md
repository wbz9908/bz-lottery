# 仓库贡献指南

## 项目结构与模块组织

本仓库是一个前后端分离的抽奖平台原型。后端位于 `lottery-backend/`，是 Maven 多模块 Spring Boot 3 项目，包含 `lottery-gateway`、`lottery-common`、`lottery-domain`、`lottery-user`、`lottery-activity`、`lottery-lottery`、`lottery-award`、`lottery-pay` 等模块。Java 源码放在 `src/main/java`，资源文件放在 `src/main/resources`，测试放在 `src/test/java`。

前端位于 `lottery-frontend/`，使用 Vue 3 和 Vite。主要目录包括 `src/api`、`src/router`、`src/stores`、`src/views` 和 `src/style.css`。部署脚本、Compose 文件和环境模板位于 `deploy/`；项目说明和计划文档位于 `docs/`。

## 构建、测试与本地开发命令

- `cd lottery-backend && mvn clean compile -U`：编译全部后端模块。
- `cd lottery-backend && mvn -pl lottery-lottery -am test`：运行指定模块及其依赖模块的测试。
- `cd lottery-backend && mvn clean package -DskipTests`：跳过测试并打包后端产物。
- `cd lottery-frontend && npm install`：按仓库内 npm 配置安装前端依赖。
- `cd lottery-frontend && npm run dev`：启动 Vite 开发服务器，默认端口通常为 `9510`。
- `cd lottery-frontend && npm run build`：生成前端生产构建。
- `deploy/scripts/up-dev.ps1`：启动本地基础设施。

## 编码风格与命名约定

使用 Java 25 和 UTF-8。后端保持清晰分层：`controller -> service/application -> domain/infrastructure/mapper`；Controller 不应直接调用 Mapper。包名使用 `com.lottery.<module>`。Java 类名使用 `PascalCase`，方法和字段使用 `camelCase`。

前端遵循 Vue 3 Composition API、ES Modules 和 Vite 约定。API 封装放在 `src/api`，路由放在 `src/router`，共享状态放在 `src/stores`。

## 测试规范

后端测试使用 Maven/Spring 测试工具，命名为 `*Test.java`，例如 `LotteryDrawServiceTest.java`。新增测试应放在被修改模块的 `src/test/java` 下。优先使用 `mvn -pl <module> -am test` 做模块级验证，再执行更大范围构建。当前前端未配置专用测试脚本，最小验证为 `npm run build`。

## 提交与 Pull Request 规范

近期提交采用 Conventional Commit 风格，例如 `feat(skill): ...`、`docs(git): ...`、`docs(agents): ...`。提交应范围清晰、语气使用祈使式。Pull Request 应包含简要说明、验证命令和结果；如有关联问题需链接 issue；涉及可见 UI 变化时提供截图或录屏。

## 安全与配置提示

不要提交本地密钥或私有配置。使用 `.env.example` 作为模板，将环境差异保存在本地 `.env` 文件中。数据库结构变更必须通过 Flyway 迁移完成，迁移脚本放在 `lottery-backend/lottery-common/src/main/resources/db/migration`。
