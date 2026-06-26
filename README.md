# Tondo

面向年轻群体的情感成长社区：匿名卡片分享、1:1 陪伴、私信、打卡、通知与内容治理。

## 技术栈

| 层 | 技术 |
|---|---|
| 后端 | Spring Boot 3、Spring Security + JWT、MyBatis-Plus、Flyway |
| 中间件 | MySQL 8、Redis、RabbitMQ、MinIO |
| 实时 | STOMP over WebSocket |
| 前端 | Vue 3 + Pinia + Element Plus（`tondo-frontend`） |

## 快速启动

### 1. 启动依赖

```bash
docker compose up -d
```

包含 MySQL(3306)、Redis(6379)、MinIO(9090/9091)、RabbitMQ(5672/15672)。

### 2. 启动后端

```bash
./mvnw spring-boot:run
```

默认端口 `8080`。Flyway 会自动执行 `db/migration` 下脚本。

### 3. 启动前端

在 `tondo-frontend` 目录：

```bash
npm install
npm run dev
```

### 4. 常用地址

- API 文档：http://localhost:8080/swagger-ui.html
- 健康检查：http://localhost:8080/actuator/health
- RabbitMQ 控制台：http://localhost:15672 （guest/guest）
- MinIO 控制台：http://localhost:9091 （minioadmin/minioadmin）

## 核心模块

| 模块 | 说明 |
|---|---|
| user | 注册登录、Refresh Token、登出黑名单、资料 |
| card | 匿名卡片、回复、感谢 |
| companion | 1:1 陪伴邀请、打卡 |
| message | 陪伴私信（REST 历史 + WebSocket 实时） |
| notification | 通知落库 + RabbitMQ 异步推送 + WebSocket 铃铛 |
| governance | 举报处理、管理端审计日志 |
| file | 头像上传（MinIO） |

## 私信与通知链路

1. 客户端 STOMP 发送到 `/app/chat.private`
2. `ChatController` → `PrivateMessageService` 落库
3. 同步推送聊天气泡：`/user/{id}/queue/private`
4. 异步通知：`NotificationService` → RabbitMQ → Consumer → `/user/{id}/queue/notifications`

## 生产部署

使用 `prod` profile，通过环境变量注入密钥：

```bash
export TONDO_JWT_SECRET=your-long-random-secret
export TONDO_DB_PASSWORD=...
java -jar tondo.jar --spring.profiles.active=prod
```

详见 `application-prod.yaml`。

## 测试

```bash
./mvnw test
```

测试 profile 使用 H2 内存库，关闭 RabbitMQ 与 MinIO。

