# Copilot Instructions for Kailv Backend

## 项目架构概览
- Spring Boot + MyBatis-Plus，分层结构：controller（接口）、service（业务）、mapper（数据访问）、model（实体）
- 主要业务域：认证、客户、产品、订单、库存、统计、AI对话
- 数据库表结构见 `src/main/resources/schema.sql`，初始化数据见 `data.sql`
- API 规范详见 `api_docs/openapi.yaml`，接口与 controller 一一对应

## 关键开发约定
- 所有 controller 路径均以 `/api/` 开头，RESTful 风格
- 业务逻辑尽量下沉到 service 层，数据操作通过 mapper（MyBatis-Plus BaseMapper）
- 统一异常处理与权限校验由 Spring Security 配置（见 `config/SecurityConfig.java`）
- AI 对话接口 `/api/ai/chat` 支持 DeepSeek API，API Key 配置见 `application.yml`（支持环境变量覆盖）
- 所有时间字段统一用 `LocalDateTime`，数据库类型为 `TIMESTAMP`

## 构建与测试
- 构建/测试命令：
  ```sh
  cd backend
  mvn clean package
  mvn test
  ```
- 单元/集成测试位于 `src/test/java/com/taizhou/kailv/api/controller/` 等目录
- H2 内存数据库自动初始化，表结构由 `schema.sql` 保证
- 推荐用 Postman（见 `postman/`）或 curl 进行接口联调

## AI/DeepSeek 集成说明
- 配置 API Key：`src/main/resources/application.yml` 的 `ai.deepseek.api-key`，或设置环境变量 `DEEPSEEK_API_KEY`
- 默认模型为 `deepseek-chat`，可通过 `ai.deepseek.model` 配置
- 未配置 API Key 时，AI接口返回 mock 内容
- DeepSeek API 调用代码见 `AiController.java`，如需扩展上下文/流式回复可参考官方文档

## 重要文件/目录
- `src/main/java/com/taizhou/kailv/api/controller/`：所有 REST API 入口
- `src/main/java/com/taizhou/kailv/api/service/`：业务逻辑接口与实现
- `src/main/java/com/taizhou/kailv/api/mapper/`：MyBatis-Plus 数据访问
- `src/main/resources/schema.sql`：数据库表结构
- `api_docs/openapi.yaml`：接口规范
- `application.yml`：全局配置

## 其它约定
- 所有金额、数量等涉及精度的字段建议用 `BigDecimal` 或 `DECIMAL` 类型
- 订单、库存等操作建议加事务（@Transactional）
- 代码风格遵循阿里 Java 开发手册，命名统一中英文混用（如有历史遗留）

---
如需扩展新接口，建议先补充 OpenAPI 文档，再实现 controller/service/mapper，最后补测。
