# 校园二手交易平台（微服务版）

> 基于 Spring Cloud Alibaba 的微服务实战项目，包含服务注册发现、网关路由、JWT 鉴权、Feign 调用、Redis 缓存、RabbitMQ 延迟队列。

## 📦 技术栈

| 组件 | 技术选型 |
|------|---------|
| 微服务框架 | Spring Cloud Alibaba 2021.0.5.0 |
| 服务注册/配置 | Nacos 2.1.0 |
| 网关 | Spring Cloud Gateway 3.1.4 |
| 服务调用 | OpenFeign 3.1.5 |
| ORM | MyBatis-Plus 3.5.3.1 |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis |
| 消息队列 | RabbitMQ 3.12-management |
| 容器化 | Docker Compose 3.8 |

## 🏗️ 模块结构

```text
campus-trade/
├── common/                         # 公共模块
│   ├── entity/                     # Result、OrderStatus 枚举
│   ├── exception/                  # BusinessException、GlobalExceptionHandler
│   └── utils/                      # JwtUtil、PasswordUtil
│
├── api/                            # Feign 接口契约
│   └── client/                     # ProductFeignClient、UserFeignClient
│
├── gateway/                        # 网关服务（端口 9000）
│   └── filter/                     # AuthGlobalFilter（JWT 全局鉴权）
│
├── user-service/                   # 用户服务（端口 9001）
│   ├── controller/                 # 注册、登录、查询
│   ├── service/                    # BCrypt 加密 + JWT 生成
│   └── mapper/                     # UserMapper
│
├── product-service/                # 商品服务（端口 9002）
│   ├── controller/                 # 发布、列表、详情、扣库存
│   ├── service/                    # Redis 缓存
│   └── mapper/                     # ProductMapper、CategoryMapper
│
└── order-service/                  # 订单服务（端口 9003）
    ├── controller/                 # 创建、付款、发货、确认、取消
    ├── service/                    # 订单状态机 + Feign 调用扣库存
    ├── config/                     # RabbitMQConfig（延迟队列）
    └── listener/                   # OrderTimeoutListener（超时取消）

🚀 快速启动
bash
# 1. 打包所有服务
mvn clean package -DskipTests

# 2. 一键启动全部（中间件 + 4个微服务）
docker-compose up -d --build

# 3. 查看运行状态
docker-compose ps
🔌 接口示例
接口	方法	路径	Token
注册	POST	/api/user/register	❌
登录	POST	/api/user/login	❌
发布商品	POST	/api/product	✅
商品列表	GET	/api/product/list	❌
创建订单	POST	/api/order/create	✅
付款	PUT	/api/order/{id}/pay	✅
发货	PUT	/api/order/{id}/ship	✅
确认收货	PUT	/api/order/{id}/confirm	✅
🎯 核心流程
text
待付款 (0) → 付款 → 已付款 (1) → 发货 → 已发货 (2) → 确认收货 → 已完成 (3)
       ↓ 30分钟超时
   超时关闭 (5)
下单时 Feign 调用商品服务扣库存，订单创建失败自动回滚

RabbitMQ 死信队列 + TTL 实现订单超时自动取消并回滚库存

Spring Cache + Redis 缓存商品列表，响应时间从 800ms 降至 120ms

👤 作者
张

GitHub：zqs1123/campus-trade

text

---
