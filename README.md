# 校园二手交易平台

> 基于 Spring Boot + MyBatis-Plus + MySQL + Redis 的校园二手交易平台后端。

## 技术栈
- Spring Boot 2.6.13
- MyBatis-Plus 3.5.3.1
- MySQL 8.0
- Redis
- JWT
- Docker / Docker Compose

## 核心功能
- 用户注册 / 登录 / JWT 认证
- 商品发布 / 分页查询 / 分类筛选
- 订单管理（下单 → 付款 → 发货 → 确认收货 → 取消）
- Redis 缓存商品列表
- Docker Compose 一键部署

## 快速启动

### 1. 启动依赖服务（MySQL + Redis）
```bash
docker-compose up -d