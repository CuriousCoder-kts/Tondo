---

# 1：项目初始化与用户模块
> 日期：2026-06-19 ~ 2026-06-20  
阶段：项目骨架搭建 + 用户认证系统
>

---

## 一、项目创建
### 1.1 基本信息
| 项目 | 内容 |
| --- | --- |
| 项目名称 | Tondo |
| 包名 | com.tondo |
| 构建工具 | Maven |
| Java 版本 | 17 |
| IDE | IntelliJ IDEA |


### 1.2 技术栈选型
| 技术 | 版本 | 用途 |
| --- | --- | --- |
| Spring Boot | 3.2.5 | 应用框架 |
| MyBatis-Plus | 3.5.9 | ORM 框架 |
| MySQL | 8.0 | 关系型数据库 |
| Redis | 7.x | 缓存（已引入，未使用） |
| Spring Security | 6.2.4 | 认证与授权 |
| JWT (jjwt) | 0.12.6 | 无状态令牌 |
| WebSocket | Spring 内置 | 实时通信（已引入，未使用） |
| MinIO | 8.6.0 | 对象存储（已引入，未使用） |
| Lombok | 1.18.32 | 简化代码 |


### 1.3 关键依赖（pom.xml 核心）
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.5</version>
</parent>
<!-- MyBatis-Plus -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.9</version>
</dependency>
<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>

```

---

## 二、踩坑记录
### 坑 1：Spring Boot 4.1.0 与 MyBatis-Plus 不兼容
+ **现象**：启动时报 `No qualifying bean of type 'UserMapper' available`，Mapper 接口未被注册
+ **原因**：Spring Boot 4.1.0 刚发布，底层 Spring Framework 7.x，MyBatis-Plus 尚未发布适配版本
+ **解决**：回退到 Spring Boot 3.2.5，这是 Spring Boot 3.x 系列最稳定的版本，生态兼容性最好
+ **决策原则**：选择框架的第一原则不是“最新”，而是“生态兼容性最好”

### 坑 2：MyBatis-Plus Starter 依赖名混乱
+ **现象**：`mybatis-plus-boot-starter` 找不到；`mybatis-plus-spring-boot3-starter` 版本号与构件名需要严格对应
+ **解决**：3.5.5 和 3.5.9 都用 `mybatis-plus-spring-boot3-starter`，3.5.9 成功

### 坑 3：MySQL JDBC 连接字符串不支持 utf8mb4
+ **现象**：`java.io.UnsupportedEncodingException: utf8mb4`
+ **原因**：Java 标准字符集名称是 `UTF-8`，不是 `utf8mb4`（`utf8mb4` 是 MySQL 内部名称）
+ **解决**：连接 URL 改为 `characterEncoding=UTF-8`
+ **修正前**：`characterEncoding=utf8mb4`
+ **修正后**：`characterEncoding=UTF-8`

### 坑 4：Spring Boot 3.2.x 的安全配置写法变化
+ **现象**：旧版 `http.csrf().disable()` 链式写法报错
+ **解决**：改用 Lambda 表达式风格：

```java
http.csrf(csrf -> csrf.disable())
    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/user/register", "/api/user/login").permitAll()
        .requestMatchers("/api/admin/**").hasRole("ADMIN")
        .anyRequest().authenticated()
    )
```

---

## 三、数据库设计
### 3.1 用户表 `user`
```sql
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `phone` VARCHAR(20) NOT NULL,
    `password_hash` VARCHAR(128) NOT NULL,
    `nickname` VARCHAR(50) NOT NULL,
    `avatar_url` VARCHAR(255) DEFAULT NULL,
    `status_label` VARCHAR(50) DEFAULT NULL,
    `confusion_tags` JSON DEFAULT NULL,
    `companion_style` VARCHAR(20) DEFAULT NULL,
    `trust_level` TINYINT NOT NULL DEFAULT 1,
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER',
    `is_frozen` TINYINT NOT NULL DEFAULT 0,
    `signed_community_rule` TINYINT NOT NULL DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_nickname` (`nickname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3.2 JSON 列索引方案
+ **问题**：MySQL 不允许直接在 JSON 列上建普通索引（错误码 3152）
+ **解决**：创建虚拟列 `confusion_tags_text VARCHAR(500) GENERATED ALWAYS AS (confusion_tags->>'$') VIRTUAL`，再在虚拟列上建索引
+ **价值**：保留 JSON 灵活性 + 获得索引能力 + 对业务代码零侵入 + 为全文索引铺路

### 3.3 设计特点
+ **无 gender 字段**：从架构上切断荷尔蒙驱动的异性连接
+ **companion_style**：陪伴匹配的核心维度（STRICT/ENCOURAGING/QUIET）
+ **trust_level**：三级信任体系（1=手机验证 2=身份认证 3=认证陪伴者）

---

## 四、用户模块代码结构
### 4.1 文件清单
```plain
com.tondo
├── common/
│   ├── exception/
│   │   └── GlobalExceptionHandler.java    ← 待完善
│   └── response/
│       └── Result.java                    ✅ 统一响应体
├── module/
│   └── user/
│       ├── controller/
│       │   └── UserController.java        ✅
│       ├── service/
│       │   ├── UserService.java           ✅ 接口
│       │   └── impl/
│       │       └── UserServiceImpl.java   ✅
│       ├── mapper/
│       │   └── UserMapper.java            ✅ 继承 BaseMapper
│       └── entity/
│           ├── User.java                  ✅ 对应 user 表
│           └── dto/
│               ├── RegisterDTO.java       ✅ 含校验注解
│               └── LoginDTO.java          ✅ 含校验注解
└── infrastructure/
    └── security/
        ├── JwtUtil.java                   ✅ Token 生成/解析/验证
        ├── JwtAuthenticationFilter.java   ✅ 每次请求拦截验证
        └── SecurityConfig.java            ✅ 权限规则配置
```

### 4.2 API 接口清单
| 方法 | 路径 | 说明 | 认证 |
| --- | --- | --- | --- |
| POST | `/api/user/register` | 用户注册 | 无需 |
| POST | `/api/user/login` | 用户登录，返回 JWT | 无需 |
| GET | `/api/user/me` | 获取当前用户信息 | 需要 Bearer Token |


### 4.3 注册请求体
```json
{
    "phone": "13800000001",
    "password": "123456",
    "nickname": "测试用户"
}
```

### 4.4 登录请求体
```json
{
    "phone": "13800000001",
    "password": "123456"
}
```

### 4.5 JWT 设计
+ **算法**：HS512（HMAC-SHA512）
+ **存放字段**：`sub`(userId)、`role`(角色)、`iat`(签发时间)、`exp`(过期时间)
+ **有效期**：7 天（604800000ms）
+ **认证方式**：请求头 `Authorization: Bearer <token>`
+ **解析方式**：`JwtAuthenticationFilter` 从 token 提取 userId 和 role，存入 `SecurityContextHolder`

### 4.6 Spring Security 权限规则
```java
.requestMatchers("/api/user/register", "/api/user/login").permitAll()  // 放行
.requestMatchers("/api/admin/**").hasRole("ADMIN")                    // 管理员专用
.anyRequest().authenticated()                                         // 其他需认证
```

---

## 五、测试验证
### 5.1 注册测试
```plain
POST /api/user/register
Status: 200 OK
Response: 用户数据（密码字段为 null），id=1
数据库验证：INSERT 成功，bcrypt 密码哈希已存储
```

### 5.2 登录测试
```plain
POST /api/user/login
Status: 200 OK
Response: JWT 字符串（三段 Base64，约 200+ 字符）
```

### 5.3 认证测试
```plain
GET /api/user/me
Headers: Authorization: Bearer <token>
Status: 200 OK
Response: 完整用户信息（含 createdAt、updatedAt）
```

---

## 六、当前状态
| 已完成 | 待完成 |
| --- | --- |
| ✅ 项目骨架 + 依赖管理 | ⬜ 全局异常处理完善（BusinessException） |
| ✅ MySQL 8 张表建表 | ⬜ MinIO 工具类 |
| ✅ 用户注册/登录/JWT 认证 | ⬜ 困惑卡片模块 |
| ✅ Spring Security 权限控制 | ⬜ 陪伴模块 |
| ✅ MyBatis-Plus 集成 | ⬜ WebSocket 私聊 |
| ✅ 统一响应体 Result | ⬜ 管理后台 |


---

> **下一阶段**：困惑卡片模块（发布卡片、首页信息流、回复系统、感谢机制）
>

---

好的，我把困惑卡片模块的开发日志和面试问答整理出来。

---

# 2：困惑卡片模块
> 日期：2026-06-20  
阶段：核心内容系统
>

---

## 一、模块概述
困惑卡片是 Tondo 的核心内容单元。用户通过结构化的表单发布自己在生活、工作、学习中遇到的困惑，而非传统的自由文本发帖。结构化表单包含：**具体事件描述、情绪标签、已尝试的方法、需要什么帮助（共鸣/建议/同伴）**。

---

## 二、新增文件清单
```plain
com.tondo
├── module/
│   └── card/
│       ├── controller/
│       │   └── CardController.java        ✅ 三个接口
│       ├── service/
│       │   ├── CardService.java           ✅ 接口
│       │   └── impl/
│       │       └── CardServiceImpl.java   ✅ 实现
│       ├── mapper/
│       │   └── CardMapper.java            ✅ 继承 BaseMapper
│       └── entity/
│           ├── Card.java                  ✅ 对应 confusion_card 表
│           └── dto/
│               └── CreateCardDTO.java     ✅ 含校验注解
└── common/
    └── config/
        └── MybatisPlusConfig.java         ✅ 分页插件配置
```

---

## 三、API 接口清单
| 方法 | 路径 | 说明 | 认证 |
| --- | --- | --- | --- |
| POST | `/api/cards` | 发布困惑卡片 | 需要 Token |
| GET | `/api/cards` | 获取卡片列表（分页/筛选/排序） | 需要 Token |
| GET | `/api/cards/{id}` | 获取单张卡片详情 | 需要 Token |


### 3.1 发布卡片请求体
```json
{
    "title": "对职业方向感到迷茫",
    "eventDescription": "不少于30字的具体事件描述...",
    "emotionTags": "[\"焦虑\",\"迷茫\"]",
    "attemptDescription": "不少于20字的已尝试方法...",
    "needType": "ADVICE",
    "confusionTags": "[\"职业方向\"]"
}
```

### 3.2 列表接口参数
| 参数 | 默认值 | 说明 |
| --- | --- | --- |
| page | 1 | 页码 |
| size | 10 | 每页条数 |
| confusionTag | 无 | 按困惑领域筛选（可选） |
| sort | new | 排序：new=最新，hot=热度 |


---

## 四、踩坑记录
### 坑 1：MyBatis-Plus 3.5.9 分页插件类找不到
+ **现象**：`PaginationInnerInterceptor` 导包报红，编译提示符号未找到
+ **原因**：MyBatis-Plus 从 3.5.9 版本开始，将 `jsqlparser`（SQL 解析器）从主包剥离为独立模块 `mybatis-plus-jsqlparser`，仅引入主 starter 不会自动传递该依赖
+ **解决**：在 `pom.xml` 中手动添加同版本依赖：

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-jsqlparser</artifactId>
    <version>3.5.9</version>
</dependency>

```

+ **排查思路**：确认依赖已下载 → 查阅官方 Release Notes 发现模块拆分变更 → 定位到缺少 `jsqlparser` → 显式引入 → 解决

---

## 五、技术要点
### 5.1 结构化表单设计
卡片发布不是空白文本框，而是五个必填字段的组合。这个设计在产品层面确保了每条内容都是“经过思考的求助”，而非纯情绪宣泄。

### 5.2 分页与筛选
+ 使用 MyBatis-Plus 的 `Page` 对象实现物理分页
+ 按困惑领域标签筛选时，通过 MySQL 虚拟列 `confusion_tags_text` 的 LIKE 查询实现
+ 排序支持最新（`created_at DESC`）和热度（`heat_score DESC`），热度分后续由感谢数和时间衰减计算

### 5.3 热度分设计（预留）
`heat_score` 字段目前插入时为 0。后续实现感谢机制后，通过定时任务或实时计算更新热度分。简单公式：`感谢数 / (发布小时数 + 2)`，类似 Hacker News 的排名算法。

---

## 六、当前项目完整进度
| 模块 | 接口数 | 状态 |
| --- | --- | --- |
| 用户系统 | 3 | ✅ 完成 |
| 困惑卡片 | 3 | ✅ 完成 |
| 回复系统 | 3 | ⬜ 待开发 |
| 陪伴系统 | 4 | ⬜ 待开发 |
| 管理后台 | 2 | ⬜ 待开发 |


---

## 七、下一阶段
回复系统：回复困惑卡片、感谢机制、“我已走出来”标记。

---

# 3：回复系统
> 日期：2026-06-20  
阶段：互动与感谢机制
>

---

## 一、模块概述
回复系统是 Tondo 社区互动的核心。用户在浏览困惑卡片后，可以通过**引导式回复**分享亲身经验，而非简单的评论。系统还包含**感谢机制**（替代传统点赞）和**“我已走出来”标记**，共同构建温暖、克制的社区氛围。

---

## 二、新增文件清单
```plain
com.tondo
├── module/
│   └── card/
│       ├── controller/
│       │   └── ReplyController.java           ✅ 回复与感谢接口
│       ├── service/
│       │   ├── ReplyService.java              ✅ 接口
│       │   ├── ThanksService.java             ✅ 感谢接口
│       │   └── impl/
│       │       ├── ReplyServiceImpl.java      ✅ 实现
│       │       └── ThanksServiceImpl.java     ✅ 实现
│       ├── mapper/
│       │   ├── ReplyMapper.java               ✅ 继承 BaseMapper
│       │   └── ThanksRecordMapper.java        ✅ 继承 BaseMapper
│       └── entity/
│           ├── Reply.java                     ✅ 对应 reply 表
│           ├── ThanksRecord.java              ✅ 对应 thanks_record 表
│           └── dto/
│               ├── CreateReplyDTO.java        ✅ 含校验
│               └── ResolveCardDTO.java        ✅ 含校验
│   (修改) CardService / CardServiceImpl / CardController
```

---

## 三、API 接口清单
| 方法 | 路径 | 说明 | 认证 |
| --- | --- | --- | --- |
| POST | `/api/cards/{cardId}/replies` | 回复困惑卡片 | 需要 Token |
| GET | `/api/cards/{cardId}/replies` | 获取卡片的所有回复 | 需要 Token |
| POST | `/api/cards/{cardId}/replies/{replyId}/thanks` | 感谢某条回复 | 需要 Token |
| POST | `/api/cards/{id}/thanks` | 感谢卡片 | 需要 Token |
| PUT | `/api/cards/{id}/resolve` | 标记“我已走出来” | 需要 Token |


### 3.1 回复请求体
```json
{
    "experienceSituation": "我当时的情况...",
    "experienceAction": "我做了什么...",
    "experienceResult": "结果与反思...",
    "replyType": "EXPERIENCE"
}
```

### 3.2 标记“我已走出来”请求体
```json
{
    "resolutionContent": "经过三个月探索，我终于..."
}
```

---

## 四、技术要点与设计决策
### 4.1 引导式回复结构
回复不是一个自由文本输入框，而是三个必填字段：**我当时的情况 → 我的行动 → 结果与反思**。这强制回复者回忆并总结自己的经验，避免空洞的“加油”或说教。`replyType` 字段预留了 `EXPERIENCE`（经验分享）和 `SUPPORT`（情感支持）两种类型，未来可据此调整展示策略。

### 4.2 感谢机制与幂等性
“感谢”替代传统的“点赞”，更符合社区互助的调性。技术上通过 `thanks_record` 表记录每条感谢行为，用户、目标类型、目标 ID 三者构成唯一约束 `uk_user_target`，保证同一用户对同一目标只能感谢一次。业务代码中也做了先查询后插入的判断，双重防重复。

### 4.3 事务管理
`ReplyServiceImpl.createReply()` 和 `ThanksServiceImpl.thankCard()` 都标注了 `@Transactional`，确保“插入回复+更新卡片回复数”或“插入感谢记录+更新计数”是原子操作，避免数据不一致。

### 4.4 卡片状态机更新
标记“我已走出来”时，服务层校验当前用户必须是卡片作者，然后将 `status` 设置为 `RESOLVED`，同时写入复盘内容与时间。这一功能为未来的正向内容分发（推荐已解决的卡片给有同类困惑的人）奠定了基础。

### 4.5 并发计数更新
当前卡片回复数、感谢数的更新采用简单的 `UPDATE` 递增（查询后设置新值）。在 MVP 阶段用户量小的情况下没有问题。后续高并发场景可改为 SQL 原子更新（`UPDATE ... SET count = count + 1`）或使用 Redis 累加后异步落库，代码层无需大改。

---

## 五、踩坑记录
### 坑 1：MyBatis-Plus 更新时覆盖未修改字段
+ **现象**：更新卡片回复数时，`cardMapper.updateById(card)` 会将所有字段全量更新，包括 `created_at` 等原始值。
+ **原因**：MyBatis-Plus 的 `updateById` 策略默认为全字段更新。
+ **影响**：业务上无实际危害（因为设置了相同的值），但生成的 SQL 冗余且可能误更新并发场景下的新值。
+ **解决**：后续可改为精确更新：`LambdaUpdateWrapper` 设置 `set(ReplyCount, replyCount+1)` 且条件 `eq(id, cardId)`，避免全量覆盖。

---

## 六、测试验证
全部 5 个接口通过 Postman 测试，控制台日志证明：

1. 回复插入成功，卡片回复数同步从 0 → 1。
2. 回复列表按时间正序返回，屏蔽了已隐藏回复。
3. 首次感谢回复/卡片成功，感谢数递增，感谢记录入库。
4. **重复感谢被阻止**：第二次感谢时抛出 `RuntimeException("已经感谢过了")`，幂等性生效。
5. 标记“我已走出来”成功，卡片状态变为 `RESOLVED`，复盘内容与时间写入。

---

## 七、当前项目完整进度
| 模块 | 接口数 | 状态 |
| --- | --- | --- |
| 用户系统 | 3 | ✅ 完成 |
| 困惑卡片 | 3 | ✅ 完成 |
| 回复与感谢 | 5 | ✅ 完成 |
| 陪伴系统 | 4 | ⬜ 待开发 |
| 管理后台 | 2 | ⬜ 待开发 |


---

