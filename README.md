# 企快办 - 企业代办小程序

一套完整的企业工商代办服务平台，包含微信小程序端和后台管理系统，提供工商年报、异常解除、注册注销等代办服务。

## 项目简介

本项目为中小企业和个体工商户提供便捷的工商事务代办服务，用户可通过小程序在线下单、支付并实时跟踪办理进度，后台管理系统支持订单全生命周期管理、内容配置、财务对账等功能。

## 技术栈

### 后端 (admin3-server)
- Java 21
- Spring Boot 3.2.3
- Spring Data JPA
- MySQL
- JWT 认证
- 微信支付 API
- 阿里云企业信息查询 API

### 管理后台 (admin3-ui)
- Vue 3
- TypeScript
- Element Plus
- Vite
- Pinia
- ECharts

### 小程序端 (minapp_enterprise_agency)
- uni-app (Vue 3)
- 微信小程序

## 项目结构

```
├── admin3-server/          # 后端服务
│   ├── src/main/java/
│   │   └── tech/wetech/admin3/
│   │       ├── controller/     # 后台管理接口
│   │       ├── miniapp/        # 小程序接口
│   │       ├── sys/            # 系统模块(模型/服务/仓库)
│   │       └── wechat/         # 微信相关服务
│   └── src/main/resources/
│       ├── application.yml     # 配置文件
│       ├── schema.sql          # 数据库结构
│       └── data.sql            # 初始化数据
├── admin3-ui/              # 管理后台前端
│   └── src/
│       ├── api/                # API 接口
│       ├── views/              # 页面组件
│       └── router/             # 路由配置
├── minapp_enterprise_agency/   # 微信小程序
│   ├── api/                    # API 接口
│   ├── pages/                  # 页面
│   └── utils/                  # 工具函数
└── docs/                   # 文档
```

## 功能模块

### 小程序端
- 首页轮播图、公告、产品展示
- 企业信息模糊查询（自动填充）
- 在线下单、微信支付
- 订单管理与进度跟踪
- 优惠券领取与使用
- 发票申请
- 用户反馈
- 个人中心

### 管理后台
- 仪表盘数据统计
- 产品管理（服务项目、定价、表单配置）
- 订单管理（审核、派单、进度更新）
- 退款管理
- 用户管理（后台用户、小程序用户）
- 内容管理（轮播图、公告、文章、FAQ）
- 财务管理（交易记录、发票管理）
- 营销管理（优惠券）
- 系统配置（微信配置、协议设置）
- RBAC 权限管理

## 快速开始

### 环境要求
- JDK 21+
- Node.js 16+
- MySQL 8.0+
- Maven 3.8+

### 后端启动

1. 创建数据库
```sql
CREATE DATABASE enterprise_agency DEFAULT CHARACTER SET utf8mb4;
```

2. 修改配置文件 `admin3-server/src/main/resources/application.yml`
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/enterprise_agency
    username: your_username
    password: your_password
```

3. 启动服务
```bash
cd admin3-server
mvn spring-boot:run
```

服务启动后访问：http://localhost:8080/api

### 管理后台启动

```bash
cd admin3-ui
npm install
npm run dev
```

访问：http://localhost:3000

默认账号：admin / 123456

### 小程序开发

1. 使用 HBuilderX 打开 `minapp_enterprise_agency` 目录
2. 修改 `utils/request.js` 中的 `BASE_URL` 为后端地址
3. 运行到微信开发者工具

## 配置说明

### 微信小程序配置
在后台「小程序管理 > 微信配置」中配置：
- 小程序 AppID
- 小程序 AppSecret

### 微信支付配置
- 商户号
- API 密钥
- 证书文件

### 阿里云企业查询
在 `application.yml` 中配置：
```yaml
aliyun:
  enterprise:
    appcode: your_appcode
```

## API 文档

启动后端服务后访问 Swagger 文档：
http://localhost:8080/api/swagger-ui.html

## 角色权限

| 角色 | 权限说明 |
|------|----------|
| 超级管理员 | 系统全部权限 |
| 运营人员 | 订单审核、派单、内容管理、营销管理 |
| 办理人员 | 订单查看、进度更新 |
| 财务人员 | 退款处理、财务数据、发票管理 |

## License

MIT License
