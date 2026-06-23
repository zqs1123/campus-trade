```markdown
# 校园二手交易平台（微服务版）

> 基于 Spring Cloud Alibaba 的微服务实战项目，包含完整的服务注册发现、网关路由、JWT 鉴权、
Feign 调用、Redis 缓存、RabbitMQ 延迟队列。

---

## 📦 技术栈

| 组件 | 技术选型 | 版本 |
|------|---------|------|
| 基础框架 | Spring Boot | 2.6.13 |
| 微服务框架 | Spring Cloud | 2021.0.5 |
| 服务注册/配置 | Spring Cloud Alibaba Nacos | 2021.0.5.0 |
| 网关 | Spring Cloud Gateway | 3.1.4 |
| 服务调用 | OpenFeign | 3.1.5 |
| ORM | MyBatis-Plus | 3.5.3.1 |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | latest |
| 消息队列 | RabbitMQ | 3.12-management |
| 容器化 | Docker Compose | 3.8 |

---

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

---

## 🚀 快速启动

### 1. 启动中间件（Docker Compose）

```bash
docker-compose up -d
```

启动后包含：
- MySQL（3307）
- Redis（6379）
- Nacos（8848 + 9848）
- RabbitMQ（5672 + 15672）

### 2. 启动微服务（IDEA 中按顺序执行）

| 顺序 | 模块 | 端口 |
|------|------|------|
| 1 | GatewayApplication | 9000 |
| 2 | UserServiceApplication | 9001 |
| 3 | ProductServiceApplication | 9002 |
| 4 | OrderServiceApplication | 9003 |

### 3. 验证服务注册

访问 Nacos 控制台：`http://localhost:8848/nacos`
- 用户名/密码：`nacos/nacos`
- 服务列表应显示 4 个服务（gateway、user-service、product-service、order-service）

---

## 🔌 接口文档

### 用户模块（路由前缀 `/api/user`）

| 接口 | 方法 | 路径 | Token |
|------|------|------|-------|
| 注册 | POST | `/register` | ❌ |
| 登录 | POST | `/login` | ❌ |
| 获取用户信息 | GET | `/me` | ✅ |

### 商品模块（路由前缀 `/api/product`）

| 接口 | 方法 | 路径 | Token |
|------|------|------|-------|
| 发布商品 | POST | `/` | ✅ |
| 商品列表 | GET | `/list` | ❌ |
| 商品详情 | GET | `/{id}` | ❌ |
| 扣库存 | PUT | `/{id}/stock` | 内部 Feign |

### 订单模块（路由前缀 `/api/order`）

| 接口 | 方法 | 路径 | Token |
|------|------|------|-------|
| 创建订单 | POST | `/create` | ✅ |
| 付款 | PUT | `/{id}/pay` | ✅ |
| 发货 | PUT | `/{id}/ship` | ✅ |
| 确认收货 | PUT | `/{id}/confirm` | ✅ |
| 取消订单 | PUT | `/{id}/cancel` | ✅ |
| 订单列表 | GET | `/list` | ✅ |

---

## 🎯 核心流程

### 订单状态机

```
待付款 (0) → 付款 → 已付款 (1) → 发货 → 已发货 (2) → 确认收货 → 已完成 (3)
       ↓ 30分钟超时
   超时关闭 (5)
```

### 超时取消链路

```
order-service 下单
    ↓ 发送延迟消息
RabbitMQ order.delay.queue (TTL 30分钟)
    ↓ 消息过期
RabbitMQ order.dlx.queue (死信队列)
    ↓ 消费者
OrderTimeoutListener.handleTimeout()
    ↓
取消订单 + 回滚库存（Feign 调用 product-service）
```

---

## 🔧 踩坑记录

### 1. Nacos 端口
Nacos 2.x 需要 **8848 + 9848** 两个端口，9848 用于 gRPC 通信。

### 2. Gateway 与 MVC 冲突
Gateway 基于 WebFlux，不能与 `spring-boot-starter-web` 共存，需要在依赖中排除。

### 3. RabbitMQ 延迟队列
- 使用 `setExpiration()`，**不是** `setDelay()`（后者需要安装插件）
- 测试前先 **Purge** 队列，避免堵塞消息影响

### 4. MySQL 8.0 连接
JDBC URL 需要添加 `&allowPublicKeyRetrieval=true`

### 5. Feign 调用
- 需要添加 `spring-cloud-starter-loadbalancer` 依赖
- 启动类需要 `@EnableFeignClients`

---

## 👤 作者

- 张
- GitHub：[zqs1123/campus-trade](https://github.com/zqs1123/campus-trade)
```