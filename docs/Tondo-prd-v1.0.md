文档创建日期：2026-06-19

当前阶段：需求分析（产品发现）—— 已完成

下一步：用户故事拆解 + MVP 范围定义

# 一、项目缘起
## 1.1 面试官的启发
背八股文、刷标准项目的简历没有竞争力

面试官看重的是：从实际场景出发，完整选型实现，有踩坑落地的过程，有复盘总结

真正做过的事情才属于自己，写在简历里才有说服力

## 1.2 我们的共识
不做红海领域的重复造轮子

做真正能上线、能被真实使用的产品

深入挖掘社会群体的真实需求，找蓝海

# 二、目标群体与核心洞察
## 2.1 目标群体
18-32岁年轻人（大学生、职场新人、转行者、考研考公人群）

在一二线城市中打拼，面临职业方向迷茫、原生家庭拉扯、自我认知模糊、人际关系困扰

## 2.2 核心痛点
迷茫被污名化，脆弱无处安放：朋友圈只能展示成功，没有地方安全地表达挣扎

孤独感在人群中加剧：微信5000好友，深夜却找不到说真话的人

"怎么办"比"为什么"更迫切，但答案碎片化：经验散落在各平台，缺乏结构化组织

知道该怎么做，但缺人一起行动：看再多文章，不如有一个同伴互相推动

## 2.3 竞品短板
| 竞品类型 | 代表产品 | 短板 |
| --- | --- | --- |
| 问答/经验社区 | 知乎、小红书 | 用户关系是"仰望大V"，造成更焦虑；缺乏行动指引 |
| 陌生人社交 | Soul、陌陌 | 荷尔蒙驱动，目的性太强，无法承载严肃成长交流 |
| 心理咨询平台 | 简单心理、KY | 门槛高（费用+心理），不适合只是想找同路人的年轻人 |


# 三、产品定位与核心定义
## 3.1 一句话定义
一个专为20-35岁年轻人打造的，以"人生困惑"为连接纽带，集经验分享、同伴匹配、行动陪伴于一体的成长社区。

## 3.2 产品不是
1. ❌ 心理咨询平台（不以专家为中心）
2. ❌ 陌生人交友软件（不以异性社交为目的）
3. ❌ 知识付费平台（不以贩卖课程为核心）
4. ❌ 纯内容社区（内容只是起点，连接和行动才是终点）

## 3.3 产品是
1. 一个"脆弱可被接纳"的安全屋
2. 一个"经验可以传递"的人生问题库
3. 一个"同伴互相照亮"的陪伴网络
4. 一个"从知道到做到"的行动起点

## 3.4 核心竞争力（亮点）
结构化"困惑卡片"：不是空白发帖，而是有引导的自我梳理表单（事件+感受+尝试+需求）；

基于困境的同伴匹配：匹配的不是"喜欢电影的人"，而是"同样在跨行求职/原生家庭拉扯"的人；

从倾诉到行动的闭环：系统推荐行动路径，促成"陪伴计划"，提供打卡工具和复盘模板；

"成长月报"可视化：每月生成个人成长轨迹报告；

温暖克制的社区氛围："感谢"按钮替代"点赞"，引导"我也有过类似经历"式回复而非"你应该"式说教。

# 四、核心风险与架构层面规避方案
## 4.1 防止变成负能量漩涡
| 层级 | 机制 | 实现方式 |
| --- | --- | --- |
| 输入层 | 结构化困惑卡片，必填"我试过什么"和"我需要什么" | 表单字段约束 |
| 互动层 | 引导式回复提示、"感谢"按钮替代"点赞"、抑制说教式回复 | 前端+后端规则 |
| 转化层 | 根据需求推荐行动路径；"我已走出来"标记机制 | 内容状态机+推荐逻辑 |
| 分发层 | 首页内容混合算法（行动动态40%、走出来复盘30%、困惑卡片20%、平台内容10%） | 策略引擎 |
| 关怀层 | 每周建设性话题、连续刷负面内容超时提醒 | 定时任务+触发规则 |


## 4.2 防止变成荷尔蒙异性交友
数据模型层：用户表不设公开性别字段，核心身份标签是"状态+困惑领域+陪伴偏好"；

交互设计层：公开场景不显示性别；头像提供插画库，不鼓励真人照片；

匹配引擎：性别不是匹配维度；匹配基于困惑领域+目标+陪伴风格；必须选择具体陪伴目标才能发起邀请；

行为监控：私聊内容敏感词过滤 + 行为模式规则引擎（识别"加微信""在哪个城市"等组合）+ 举报熔断机制。

## 4.3 防止信息真伪问题
三级信任体系：Lv1手机号验证 → Lv2学信网/企业邮箱认证 → Lv3认证陪伴者（需提交经验并通过评价）；

经验结构化：过来人分享需填写时间线+具体行动+可验证产出，上传证明可得"已验证"徽章；

社区验证：每条经验的"有用/无用"二元投票，低质经验降权；

内容安全：接入第三方审核API（敏感词、自残/暴力内容），触发危机干预流程。

## 4.4 防止陪伴依赖与情感剥削
陪伴关系有默认期限（21天），到期自动结束，续期需双方确认；

系统记录帮助者/被帮助者角色次数，防止单向消耗；

初期鼓励公开区交流，私聊内容保留审计日志。

## 4.5 数据安全与隐私
困惑卡片默认不对外搜索引擎开放；

用户可随时真删除内容，支持数据导出；

敏感数据加密存储，定期备份。

# 五、商业价值与价值主张
## 5.1 对用户的价值（三层递进）
| 层次 | 价值 | 描述 |
| --- | --- | --- |
| 第一层：即时 | 情绪的容器 | 迷茫时有一个安全的地方倾倒，被"看见"即有疗愈作用 |
| 第二层：中期 | 经验的桥梁 | 过来人用亲身经历回应，积累关于自己人生的"攻略" |
| 第三层：长期 | 行动的引擎 | 从"知道"到"做到"，完成从求助者到帮助者的角色转变 |


## 5.2 商业潜力
付费陪伴营：有嘉宾带领的深度主题营（如"21天走出职业迷茫"）；

会员订阅：个人成长数据复盘、优质内容路线图、高级匹配功能；

过来人小额付费：向认证陪伴者支付小额感谢费，平台抽佣。

## 5.3 社会价值
弥补家庭支持弱化与专业心理服务昂贵之间的空白；

构建"普通人帮助普通人"的新型互助文化；

沉淀的困惑与经验库，是一部"当代年轻人精神成长史"。

# 六、技术架构与开发规划
## 6.1 技术栈选择
后端：Spring Boot + MyBatis + MySQL + Redis + RocketMQ + WebSocket；

前端：Vue3（或 React） + Tailwind CSS + 组件库；

部署：Docker + Docker Compose + 云服务器 + Nginx；

监控：Prometheus + Grafana 或云服务免费监控。

## 6.2 架构关键组件
| 组件 | 职责 |
| --- | --- |
| 身份与权限服务 | 三级认证体系、角色管理 |
| 内容服务 | 困惑卡片CRUD、经验分享、结构化表单 |
| 匹配引擎 | 基于困惑领域+目标+风格的同伴匹配 |
| 消息服务 | WebSocket实时通信 + MQ异步消息 |
| 社区治理服务 | 策略引擎、内容审核管道、行为监控规则引擎 |
| 隐私与合规层 | 数据加密、日志脱敏、删除与导出API |


## 6.3 分阶段开发路线
| 阶段 | 时间 | 内容 |
| --- | --- | --- |
| 第一阶段 | 1-5周 | 用户系统 + 困惑卡片CRUD + 首页信息流 + Docker化 |
| 第二阶段 | 6-10周 | 陪伴匹配 + 邀请流程 + WebSocket私聊 |
| 第三阶段 | 11-14周 | 测试优化 + 监控 + 部署上线 + 种子用户 |


保守估计总耗时：3-4个月（个人开发者）

# 七、当前进度与下一步
## 7.1 已完成
需求分析（产品发现阶段）；

目标群体与痛点定义；

产品定位与核心价值主张；

竞品分析；

核心亮点设计；

风险评估与架构层面规避方案；

技术架构初步规划。

## 7.2 待完成
用户故事完整列表（按角色+场景）；

MVP 范围定义（MoSCoW 优先级）；

系统架构图；

数据库 ER 图与核心表设计；

API 接口定义；

核心业务流程状态机；

迭代开发与上线；

文档版本：v1.0

下次计划：完成用户故事列表 + MVP 范围定义

# 八、完成用户故事列表 + MVP 范围定义
## 8.1 完整用户故事列表
按照用户角色和场景，把所有可能的功能都写成了用户故事，并进行了分组。

#### 模块一：困惑与表达（产品核心引擎）
| 编号 | 用户故事 | 价值 |
| --- | --- | --- |
| US-01 | 作为一个迷茫的年轻人，我想要用结构化的表单（事件+感受+尝试+需求）发布一张困惑卡片，以便厘清自己到底在烦恼什么，并获得有效的回应 | 帮助用户清晰地表达自己的问题，并通过社区的力量获取针对性的帮助。 |
| US-02 | 作为一个求助者，我希望能选择我的困惑领域标签（如职业、原生家庭），以便让有相关经验的人能精准地看到我 | 使求助信息更加精准地到达能够提供帮助的目标群体，提高解决问题的效率。 |
| US-03 | 作为一个求助者，我希望能明确自己需要什么（共鸣/建议/同伴），以便回复者知道该如何帮我 | 让求助者的需求更加具体化，从而引导回复者给出更符合期待的支持或建议。 |
| US-04 | 作为一个浏览者，我想要在首页看到按热度和时间排序的困惑卡片，以便了解大家的真实困境，并找到我关心的内容 | 为用户提供了一个窗口去洞察他人面临的挑战，同时也能快速定位到自己感兴趣的话题。 |
| US-05 | 作为一个浏览者，我希望能按困惑领域标签筛选卡片，以便聚焦在我想关注的话题上 | 允许用户根据个人兴趣或专业领域过滤信息，使得浏览体验更加个性化且高效。 |


####   
模块二：回应与经验（社区互动层）
| 编号 | 用户故事 | 价值 |
| --- | --- | --- |
| US-06 | 作为一个过来人，我想要用引导式表单（我当时的情况+我的行动+我的结果）回复一张困惑卡片 | 以便分享真正有用的亲身经验，而非说教 |
| US-07 | 作为一个阅读者，我希望能对有用的经验点击“感谢” | 以便表达谢意，并让优质经验被更多人看到 |
| US-08 | 作为一个曾经求助的人，我希望能在自己发布的困惑卡片上标记“我已走出来”，并补充我的复盘 | 以便给正在经历同样痛苦的人带来希望，并完成自己的成长记录 |
| US-09 | 作为一个阅读者，我希望能举报不友善或虚假的内容 | 以便保护社区氛围 |


####   
模块三：连接与陪伴（产品最核心的差异化价值）
| 编号 | 用户故事 | 价值 |
| --- | --- | --- |
| US-10 | 作为一个寻求同伴的人，我想要创建一个陪伴计划（设定目标、周期、频率），以便找到志同道合的人一起行动 | 让用户能够更容易地根据自己的需求和兴趣寻找合适的伙伴，促进共同成长。 |
| US-11 | 作为一个寻求同伴的人，我希望能基于困惑领域、目标和陪伴风格，被推荐匹配的同伴，以便找到真正能互相理解、一起坚持的人 | 提高匹配度，帮助用户找到更加契合的伙伴，增强相互之间的支持与鼓励。 |
| US-12 | 作为一个被邀请的人，我想要收到清晰、有具体目标的陪伴邀请，以便判断是否适合自己，并决定接受或拒绝 | 确保邀请信息足够明确，便于受邀者做出合适的选择。 |
| US-13 | 作为一个已匹配的同伴，我想要通过一对一私聊功能进行实时沟通，以便方便地交流和互相监督 | 增进彼此间的了解和支持，同时也能更好地协调双方的日程安排。 |
| US-14 | 作为一个陪伴者，我想要在陪伴计划中完成每日打卡，以便记录进度，并让同伴看到我的坚持 | 激励个人持续努力，同时也增加了透明度，有助于建立信任关系。 |
| US-15 | 作为一个陪伴计划参与者，我想要在计划结束时收到一份总结报告，以便回顾这段共同成长的旅程 | 提供了一个反思的机会，帮助参与者认识到这段时间内所取得的进步。 |


####   
模块四：个人成长与社区治理（长期价值与平台安全）
| 编号 | 用户故事 | 价值 |
| --- | --- | --- |
| US-16 | 作为一个长期用户，我想要每月收到一份“成长月报”，以便可视化地看到自己的困惑变化、得到的帮助和完成的行动 | 可视化展示个人成长历程，增加用户的参与感与成就感 |
| US-17 | 作为一个新用户，我想要进行身份认证（Lv1手机号，后续可升至Lv2学信网/企业邮箱），以便获得社区的信任，并解锁更高权重的经验分享 | 提高账号安全性，增强社区信任度；鼓励用户提供更高质量的内容分享 |
| US-18 | 作为一个社区成员，我想要在注册时签署社区公约，以便了解行为规范，共建温暖克制的氛围 | 明确告知用户行为准则，促进形成积极正面的社区文化 |
| US-19 | 作为一个管理员（We），我需要一个后台系统来查看举报、处理违规内容、配置首页内容混合比例，以便守护社区氛围 | 加强对社区内容质量及安全性的管理，维护良好的交流环境 |


##   
8.2 MVP 范围定义（MoSCoW 优先级划分）
  
我们用 MoSCoW 方法来切分。MVP 的目标是 “能用且安全地跑通核心闭环”。

### Must Have（必须要有，没这些产品不成立）


| 用户故事 | 功能点 | 核心理由 |
| --- | --- | --- |
| US-01 | 发布结构化困惑卡片 | 这是产品的核心输入，是这个产品区别于其他社区的根本。 |
| US-04 | 首页信息流（简单按时间+热度排序） | 用户必须能看到别人发的内容，才有社区。 |
| US-05 | 按标签筛选卡片 | 帮助用户在MVP阶段就形成话题聚焦，防止首页混乱。 |
| US-06 | 回复困惑卡片（引导式表单） | 有来有往，才能形成互动闭环。 |
| US-07 | “感谢”按钮 | 确立社区的独特互动基调，而非“点赞”。 |
| US-08 | “我已走出来”标记 | 这个设计是防止负能量漩涡的关键机制，第一天就要有。 |
| US-10,11,12,13 | 核心陪伴闭环：发布计划 → 匹配推荐 → 邀请 → 接受 → 一对一私聊 | 这是你和所有竞品的分水岭。没有它，产品就只是个发牢骚的论坛。它是核心差异化价值。 |
| US-18 | 社区公约 + 注册签署 | 从第一天就设立行为规范，是预防社区变质的法律和氛围基础。 |
| US-19 | 基础管理后台（举报处理+删帖） | 你必须有能力介入，这是你和用户的定心丸。 |


####   
MVP 的核心闭环：
  
用户发布结构化困惑卡片 → 收到他人基于经验的引导式回复 → 用户可发起或加入陪伴计划 → 通过匹配引擎找到同伴 → 进行一对一私聊 → 用户解决问题后可标记“我已走出来”并复盘。这个闭环跑通，产品就“活了”。

### Should Have（应该要有，重要但可以等MVP之后第一或第二个迭代再做）


| 用户故事 | 功能点 | 理由 |
| --- | --- | --- |
| US-14 | 陪伴计划内打卡 | 增强陪伴的仪式感和约束力，但目前先能用私聊沟通起来最重要 |
| US-17 | Lv2身份认证（学信网/企业邮箱） | 对信任至关重要，但前期用户少，可以先靠氛围运营，认证开发成本不低 |


###   
Could Have（可以有，锦上添花，以后再说）


| 用户故事 | 功能点 | 理由 |
| --- | --- | --- |
| US-16 | 成长月报 | 对用户长期留存价值巨大，但开发涉及数据聚合分析，MVP阶段用不上 |
| US-15 | 陪伴计划总结报告 | 同上，需要积累数据才有价值 |


###   
Won't Have（MVP阶段明确不做）
+ 复杂算法推荐
+ 语音/视频功能
+ 任何支付/付费功能
+ 高级NLP情绪分析
+ 用户自行创建行动小组（MVP只做一对一的陪伴计划）



## 8.3 MVP 的精确功能列表（开始画线）
  
根据以上分析，现在需要动手开发的，就只是一下这些功能：

### 用户系统
+ 手机号注册/登录（Lv1）
+ 个人主页（只显示：困惑领域标签、陪伴风格、当前/过往陪伴计划数，无性别字段）
+ 注册时签署社区公约

### 困惑卡片
+ 结构化发布（事件、感受标签、尝试、需求选择）
+ 标签选择（职业/原生家庭/人际关系/自我认知/其他）
+ 首页列表（按热度和时间排序，热度算法简单版：感谢数 / (发布时间的小时数 + 2)）
+ 按标签筛选
+ 详情页展示

### 互动系统
+ 引导式回复（复用过来人经验结构）
+ “感谢”按钮（仅对回复可见）
+ “我已走出来”标记（仅对自己的卡片可见，标记后需写一段复盘）
+ 对任何内容的举报按钮

### 陪伴系统（核心闭环）
+ 发布陪伴计划（目标、周期、频率）
+ 匹配推荐列表（基于困惑领域和目标的简单权重匹配）
+ 发送/接受/拒绝陪伴邀请
+ 一对一私聊（WebSocket 实现，文字即可）
+ 陪伴关系管理（查看当前陪伴、手动结束）

### 管理后台
+ 查看举报列表
+ 删除/折叠内容
+ 冻结用户私聊权限

## 8.4 下一步：系统设计
  
现在，MVP的范围已经非常清晰了。

下一次，我们就可以进入 “阶段二：系统设计”，具体是：

1. 画出 MVP 的系统架构图：用户端、后端微服务模块划分、数据库、缓存、消息队列
2. 设计核心数据库表结构：
+ 用户表（无性别字段）
+ 困惑卡片表（结构化字段）
+ 回复表（引导式字段+类型）
+ 陪伴计划表 + 陪伴关系表 + 私聊消息表
3. 定义最关键的两个状态机：
+ 困惑卡片状态机（草稿→已发布→已解决(已走出来)）
+ 陪伴关系状态机（等待确认→进行中→已完成/已取消）



# 九、MVP 系统架构设计
## 9.1 架构图
```plain
┌─────────────────────────────────────────────────┐
│                    前端层                        │
│  Vue3 单页应用 (端口 80/443)                     │
│  - 首页信息流                                   │
│  - 困惑卡片发布/详情                            │
│  - 陪伴计划/匹配/私聊                           │
│  - 管理后台                                     │
└──────────────┬──────────────────────────────────┘
               │ HTTPS / WSS
┌──────────────▼──────────────────────────────────┐
│              网关层 (Nginx)                      │
│  - 反向代理 + 负载均衡                          │
│  - SSL 终止                                     │
│  - WebSocket 代理 (路径 /ws)                    │
└──────────────┬──────────────────────────────────┘
               │
┌──────────────▼──────────────────────────────────┐
│            后端应用层 (Spring Boot)              │
│                                                  │
│  ┌─────────────┐  ┌──────────────┐              │
│  │ 用户服务     │  │ 内容服务      │              │
│  │ - 注册/登录 │  │ - 困惑卡片CRUD│              │
│  │ - JWT认证   │  │ - 回复CRUD    │              │
│  │ - 个人信息   │  │ - 热度计算    │              │
│  └─────────────┘  └──────────────┘              │
│                                                  │
│  ┌─────────────┐  ┌──────────────┐              │
│  │ 陪伴服务     │  │ 消息服务      │              │
│  │ - 计划发布   │  │ - WebSocket   │              │
│  │ - 匹配引擎   │  │   连接管理    │              │
│  │ - 邀请/确认  │  │ - 私聊消息    │              │
│  │ - 打卡(后续) │  │   收发与落库  │              │
│  └─────────────┘  └──────────────┘              │
│                                                  │
│  ┌─────────────┐  ┌──────────────┐              │
│  │ 社区治理服务 │  │ 通知服务      │              │
│  │ - 举报处理   │  │ - 模板消息    │              │
│  │ - 内容审核   │  │ - 系统提醒    │              │
│  │ - 策略配置   │  │ (后续接入MQ)  │              │
│  └─────────────┘  └──────────────┘              │
└──────────────┬──────────────────────────────────┘
               │
┌──────────────▼──────────────────────────────────┐
│              数据层                              │
│  ┌─────────┐ ┌─────────┐ ┌─────────────────┐    │
│  │ MySQL   │ │ Redis   │ │ RocketMQ(后续)  │    │
│  │ 主数据库│ │ - Session│ │ - 异步通知      │    │
│  │         │ │ - 热度  │ │ - 审核管道      │    │
│  │         │ │ - 匹配  │ │                 │    │
│  └─────────┘ └─────────┘ └─────────────────┘    │
└─────────────────────────────────────────────────┘
```

##   
9.2 模块间通信方式
| 通信场景 | 方式 | 说明 |
| --- | --- | --- |
| 前端 ↔ 后端（普通请求） | RESTful API over HTTPS | 所有 CRUD 操作 |
| 前端 ↔ 后端（实时消息） | WebSocket (STOMP) | 私聊消息推送、陪伴邀请通知 |
| 后端模块之间 | 同步调用（初期） | MVP 阶段微服务拆得太细成本太高，先用模块化单体，包结构隔离 |
| 后端模块之间 | 异步任务（后续） | RocketMQ |


##   
9.3 技术选型清单
| 层次 | 技术 | 版本 | 选型理由 |
| --- | --- | --- | --- |
| 后端框架 | Spring Boot | 3.x | Java 生态标准，你熟悉 |
| ORM | MyBatis-Plus | 3.5+ | 单表 CRUD 效率高，复杂 SQL 可手写 |
| 数据库 | MySQL | 8.0 | 关系型数据，成熟稳定 |
| 缓存 | Redis | 7.x | Session、热度、简单匹配缓存 |
| 消息队列 | RocketMQ | 5.x | 阿里系，Java 生态兼容好（MVP可暂不引入） |
| 实时通信 | WebSocket (STOMP) | Spring 内置 | 轻量，够用，不用上 Netty |
| 前端 | Vue3 + Vite | 3.x | 生态好，上手快 |
| UI 组件 | Element Plus | 最新 | 适合后台和表单密集型应用 |
| 部署 | Docker + Compose | 最新 | 一键部署，环境一致 |
| 反向代理 | Nginx | 最新 | 经典方案 |


#   
十、核心数据库表结构设计
## 10.1 用户表 user
```sql
CREATE TABLE `user` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                        `phone` VARCHAR(20) NOT NULL COMMENT '手机号（加密存储）',
                        `password_hash` VARCHAR(128) NOT NULL COMMENT 'bcrypt 密码哈希',
                        `nickname` VARCHAR(50) NOT NULL COMMENT '昵称（唯一）',
                        `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像URL（系统插画库选择，非真人照片）',
                        `status_label` VARCHAR(50) DEFAULT NULL COMMENT '当前状态标签：在校学生/在职/求职中/自由职业',
                        `confusion_tags` JSON DEFAULT NULL COMMENT '核心困惑领域标签：["职业方向","原生家庭","人际关系","自我认知","情绪管理"]',
                        `companion_style` VARCHAR(20) DEFAULT NULL COMMENT '陪伴偏好：STRICT/ENCOURAGING/QUIET',
                        `trust_level` TINYINT NOT NULL DEFAULT 1 COMMENT '信任等级：1=手机验证 2=身份认证 3=认证陪伴者',
                        `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：USER/ADMIN',
                        `is_frozen` TINYINT NOT NULL DEFAULT 0 COMMENT '是否被冻结：0=正常 1=冻结（禁止私聊）',
                        `signed_community_rule` TINYINT NOT NULL DEFAULT 0 COMMENT '是否签署社区公约：0=未签署 1=已签署',
                        `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_phone` (`phone`),
                        UNIQUE KEY `uk_nickname` (`nickname`),
                        INDEX `idx_trust_level` (`trust_level`),
                        INDEX `idx_status_label` (`status_label`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 设计要点：
+ 无 gender 字段，从架构上切断荷尔蒙驱动的连接
+ companion_style 是陪伴匹配的核心维度之一
+ trust_level 支持三级信任体系
+ signed_community_rule 确保用户注册时已签署公约

## 10.2 困惑卡片表 confusion_card
```sql
CREATE TABLE `confusion_card` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '卡片ID',
                                  `user_id` BIGINT NOT NULL COMMENT '发布者ID',
                                  `title` VARCHAR(100) DEFAULT NULL COMMENT '卡片标题（可选，用于列表展示）',
                                  `event_description` TEXT NOT NULL COMMENT '具体事件描述（不少于30字）',
                                  `emotion_tags` JSON NOT NULL COMMENT '情绪标签',
                                  `attempt_description` TEXT NOT NULL COMMENT '我尝试过什么方法（不少于20字）',
                                  `need_type` VARCHAR(20) NOT NULL COMMENT '我需要什么：EMPATHY/ADVICE/COMPANION',
                                  `confusion_tags` JSON NOT NULL COMMENT '困惑领域标签',
    -- 虚拟列，用于索引
                                  `confusion_tags_text` VARCHAR(500) GENERATED ALWAYS AS (confusion_tags->>'$') VIRTUAL,
                                  `status` VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
                                  `resolution_content` TEXT DEFAULT NULL,
                                  `resolved_at` DATETIME DEFAULT NULL,
                                  `thanks_count` INT NOT NULL DEFAULT 0,
                                  `reply_count` INT NOT NULL DEFAULT 0,
                                  `view_count` INT NOT NULL DEFAULT 0,
                                  `heat_score` DOUBLE NOT NULL DEFAULT 0,
                                  `is_pinned` TINYINT NOT NULL DEFAULT 0,
                                  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`id`),
                                  INDEX `idx_user_id` (`user_id`),
                                  INDEX `idx_status` (`status`),
                                  INDEX `idx_need_type` (`need_type`),
                                  INDEX `idx_heat_score` (`heat_score` DESC),
                                  INDEX `idx_created_at` (`created_at` DESC),
    -- 索引建在虚拟列上
                                  INDEX `idx_confusion_tags` (`confusion_tags_text`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='困惑卡片表';
```

### 设计要点：
+ 完全按照我们设计的结构化表单来建字段
+ need_type 决定了回复引导方式
+ status 支持状态流转（DRAFT → PUBLISHED → RESOLVED）
+ heat_score 用于首页排序（简单公式：感谢数 / (发布小时数 + 2)）

## 10.3 回复表 reply
```sql
CREATE TABLE `reply` (
                         `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '回复ID',
                         `card_id` BIGINT NOT NULL COMMENT '所属卡片ID',
                         `user_id` BIGINT NOT NULL COMMENT '回复者ID',
                         `parent_id` BIGINT DEFAULT NULL COMMENT '父回复ID（支持楼中楼，暂不用）',
                         `experience_situation` TEXT NOT NULL COMMENT '我当时的情况',
                         `experience_action` TEXT NOT NULL COMMENT '我的行动',
                         `experience_result` TEXT NOT NULL COMMENT '我的结果与反思',
                         `reply_type` VARCHAR(20) NOT NULL DEFAULT 'EXPERIENCE' COMMENT '回复类型：EXPERIENCE=经验分享 SUPPORT=情感支持',
                         `thanks_count` INT NOT NULL DEFAULT 0 COMMENT '被感谢次数',
                         `is_hidden` TINYINT NOT NULL DEFAULT 0 COMMENT '是否被隐藏（举报触发）',
                         `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         PRIMARY KEY (`id`),
                         INDEX `idx_card_id` (`card_id`),
                         INDEX `idx_user_id` (`user_id`),
                         INDEX `idx_card_id_thanks` (`card_id`, `thanks_count` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回复表';
```

### 设计要点：
+ 引导式表单结构，不是自由文本
+ reply_type 区分经验分享和纯支持，未来可扩展
+ 与困惑卡片的 need_type 配合，前端提示不同回复引导

## 10.4 感谢记录表 thanks_record
```sql
CREATE TABLE `thanks_record` (
                                 `id` BIGINT NOT NULL AUTO_INCREMENT,
                                 `user_id` BIGINT NOT NULL COMMENT '感谢者ID',
                                 `target_type` VARCHAR(20) NOT NULL COMMENT '目标类型：CARD/REPLY',
                                 `target_id` BIGINT NOT NULL COMMENT '目标ID',
                                 `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
                                 INDEX `idx_target` (`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='感谢记录表（防止重复感谢）';
```

## 10.5 陪伴计划表 companion_plan
```sql
CREATE TABLE `companion_plan` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '计划ID',
                                  `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
                                  `title` VARCHAR(100) NOT NULL COMMENT '计划标题',
                                  `goal_description` TEXT NOT NULL COMMENT '目标描述',
                                  `confusion_tags` JSON NOT NULL COMMENT '关联困惑领域',
    -- 虚拟列
                                  `confusion_tags_text` VARCHAR(500) GENERATED ALWAYS AS (confusion_tags->>'$') VIRTUAL,
                                  `duration_days` INT NOT NULL DEFAULT 21,
                                  `checkin_frequency` VARCHAR(20) NOT NULL,
                                  `companion_style_preferred` VARCHAR(20) DEFAULT NULL,
                                  `status` VARCHAR(20) NOT NULL DEFAULT 'SEEKING',
                                  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`id`),
                                  INDEX `idx_creator` (`creator_id`),
                                  INDEX `idx_status` (`status`),
    -- 索引建在虚拟列上
                                  INDEX `idx_confusion_tags` (`confusion_tags_text`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='陪伴计划表';
```

## 10.6 陪伴关系表 companion_relation
```sql
CREATE TABLE `companion_relation` (
                                      `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关系ID',
                                      `plan_id` BIGINT NOT NULL COMMENT '所属计划ID',
                                      `inviter_id` BIGINT NOT NULL COMMENT '邀请人ID',
                                      `invitee_id` BIGINT NOT NULL COMMENT '被邀请人ID',
                                      `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '关系状态：PENDING=等待确认 ACCEPTED=进行中 COMPLETED=已完成 CANCELLED=已取消 REJECTED=已拒绝',
                                      `started_at` DATETIME DEFAULT NULL COMMENT '开始时间（接受邀请的时间）',
                                      `ended_at` DATETIME DEFAULT NULL COMMENT '结束时间',
                                      `daily_checkin_count` INT NOT NULL DEFAULT 0 COMMENT '双方总打卡次数（后续用）',
                                      `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `uk_plan_inviter_invitee` (`plan_id`, `inviter_id`, `invitee_id`),
                                      INDEX `idx_invitee_status` (`invitee_id`, `status`),
                                      INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='陪伴关系表';
```

## 10.7 私聊消息表 private_message
```sql
CREATE TABLE `private_message` (
                                   `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
                                   `relation_id` BIGINT NOT NULL COMMENT '所属陪伴关系ID',
                                   `sender_id` BIGINT NOT NULL COMMENT '发送者ID',
                                   `receiver_id` BIGINT NOT NULL COMMENT '接收者ID',
                                   `content` TEXT NOT NULL COMMENT '消息内容',
                                   `content_type` VARCHAR(10) NOT NULL DEFAULT 'TEXT' COMMENT '消息类型：TEXT/IMAGE(后续)',
                                   `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读',
                                   `is_recalled` TINYINT NOT NULL DEFAULT 0 COMMENT '是否撤回',
                                   `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`),
                                   INDEX `idx_relation_id` (`relation_id`),
                                   INDEX `idx_sender_receiver` (`sender_id`, `receiver_id`),
                                   INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私聊消息表（仅限陪伴关系内的双方）';
```

### 设计要点：
+ 私聊必须依附于一个已确认的陪伴关系，不存在“无关系的私聊”
+ 这是防止荷尔蒙私聊的架构级约束

## 10.8 举报记录表 report_record
```sql
CREATE TABLE `report_record` (
                                 `id` BIGINT NOT NULL AUTO_INCREMENT,
                                 `reporter_id` BIGINT NOT NULL COMMENT '举报者ID',
                                 `target_type` VARCHAR(20) NOT NULL COMMENT '目标类型：CARD/REPLY/MESSAGE/USER',
                                 `target_id` BIGINT NOT NULL COMMENT '目标ID',
                                 `reason` VARCHAR(50) NOT NULL COMMENT '举报原因：HARASSMENT/FAKE_INFO/HATE_SPEECH/OTHER',
                                 `description` TEXT DEFAULT NULL COMMENT '补充说明',
                                 `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '处理状态：PENDING/RESOLVED/DISMISSED',
                                 `handler_id` BIGINT DEFAULT NULL COMMENT '处理人ID',
                                 `handle_result` VARCHAR(50) DEFAULT NULL COMMENT '处理结果',
                                 `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`),
                                 INDEX `idx_status` (`status`),
                                 INDEX `idx_target` (`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='举报记录表';
```

# 十一、两个核心业务状态机
## 11.1 困惑卡片状态机
```plain
                    ┌──────────┐
       创建并保存     │  DRAFT   │
    ──────────────→  │  草稿    │
                    └────┬─────┘
                         │ 发布
                         ▼
                    ┌──────────┐
        管理员隐藏     │PUBLISHED │ ←──── 用户编辑
      ┌─────────────│ 已发布   │──────────┐
      ▼             └────┬─────┘          │
  ┌────────┐             │                │
  │ HIDDEN │             │ 用户标记        │
  │ 已隐藏  │             │ “我已走出来”    │
  └────────┘             │ + 填写复盘      │
                         ▼                │
                    ┌──────────┐          │
                    │ RESOLVED │          │
                    │ 已解决    │          │
                    └──────────┘          │
                         │                │
                         │ 用户可取消标记  │
                         └────────────────┘
```

### 状态流转规则：
| 当前状态 | 触发事件 | 目标状态 | 前置条件 |
| --- | --- | --- | --- |
| [初始] | 创建并保存 | DRAFT | 无 |
| DRAFT | 发布 | PUBLISHED | 所有必填字段不为空 |
| PUBLISHED | 用户标记“我已走出来” | RESOLVED | resolution_content 不为空 |
| RESOLVED | 用户取消标记 | PUBLISHED | 无 |
| PUBLISHED | 管理员隐藏 | HIDDEN | 管理员权限 |


## 11.2 陪伴关系状态机
```plain
    创建计划
    ┌─────────┐
    │ SEEKING │ ← 计划创建，等待匹配
    │ 寻找中   │
    └────┬────┘
         │ A邀请B
         ▼
    ┌─────────┐
    │ PENDING │ ← 等待B确认
    │ 等待确认  │
    └────┬────┘
         │
    ┌────┼────────────┐
    │    │            │
    ▼    ▼            ▼
B接受  B拒绝      邀请人取消
    │    │            │
    ▼    ▼            ▼
┌──────────┐ ┌──────────┐ ┌──────────┐
│ ACCEPTED │ │ REJECTED │ │CANCELLED │
│  进行中   │ │  已拒绝   │ │  已取消   │
└────┬─────┘ └──────────┘ └──────────┘
     │
     │ 计划到期 / 双方确认完成 / 任一方主动结束
     ▼
┌──────────┐
│COMPLETED │
│  已完成   │
└──────────┘
```

### 状态流转规则：
| 当前状态 | 触发事件 | 目标状态 | 说明 |
| --- | --- | --- | --- |
| [初始] | 创建计划 | SEEKING | 计划创建，可被匹配 |
| SEEKING | 用户A向B发起邀请 | PENDING | 计划状态不变，关系状态为PENDING |
| PENDING | B接受 | ACCEPTED | started_at = now()，计划状态变为 IN_PROGRESS |
| PENDING | B拒绝 | REJECTED | 关系结束，计划回到SEEKING |
| PENDING | A取消邀请 | CANCELLED | 关系结束，计划回到SEEKING |
| ACCEPTED | 任一方主动结束 / 到达计划周期 | COMPLETED | ended_at = now() |


# 十二、模块包结构建议（Spring Boot 项目）  

```plain
com.tondo
├── TondoApplication.java
├── common/                     // 公共类
│   ├── config/                 // Spring 配置
│   ├── exception/              // 全局异常处理
│   ├── response/               // 统一响应体
│   └── utils/                  // 工具类
├── module/
│   ├── user/                   // 用户模块
│   │   ├── controller/
│   │   ├── service/
│   │   ├── mapper/
│   │   └── entity/
│   ├── card/                   // 困惑卡片模块
│   │   ├── controller/
│   │   ├── service/
│   │   ├── mapper/
│   │   └── entity/
│   ├── companion/              // 陪伴模块
│   │   ├── controller/
│   │   ├── service/
│   │   ├── matcher/            // 匹配引擎
│   │   ├── mapper/
│   │   └── entity/
│   ├── message/                // 消息模块
│   │   ├── controller/         // WebSocket 端点
│   │   ├── service/
│   │   ├── mapper/
│   │   └── entity/
│   └── governance/             // 社区治理模块
│       ├── controller/         // 管理后台 API
│       ├── service/
│       ├── mapper/
│       └── entity/
└── infrastructure/             // 基础设施
    ├── security/               // JWT + Spring Security
    ├── websocket/              // WebSocket 配置
    └── cache/                  // Redis 配置
```





