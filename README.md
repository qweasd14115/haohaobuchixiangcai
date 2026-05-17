# 顺顺银行 - 在线账户管理系统

基于 Spring Boot + Vue 3 的银行账户管理系统，支持注册登录、账户管理、转账汇款、存取款、交易记录查询。

## 技术栈

| 模块 | 技术 |
|---|---|
| 后端 | Spring Boot 4 + JPA + H2 |
| 前端 | Vue 3 + Vite + Vue Router + Tailwind CSS |
| 数据库 | H2（默认）/ MySQL |
| 图标 | lucide-vue-next |

## 项目结构

```
bank-system/
├── bank-backend/   # Spring Boot 后端
├── frontend/       # Vue 3 前端
└── database/       # 数据库建表脚本
    └── schema.sql
```

## 快速开始

### 环境要求

- Java 17+
- Node.js 18+

### 1. 启动后端

```bash
cd bank-backend
.\mvnw spring-boot:run
```

后端运行在 `http://localhost:8080`

### 2. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端运行在 `http://localhost:5173`

### 3. 访问系统

浏览器打开 `http://localhost:5173`，注册新账户或使用测试账号：

- 用户名：`chenshun`
- 密码：`123456`

## 功能列表

- 用户注册 / 登录
- 创建多张银行卡
- 账户余额实时概览
- 转账汇款（支持跨用户）
- 存款充值 / 取款提现
- 交易记录查询
- 退出登录 / 路由守卫

## API 接口

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/register` | 用户注册 |
| POST | `/api/login` | 用户登录 |
| GET | `/api/accounts/my` | 查询我的账户 |
| POST | `/api/accounts/create` | 创建新银行卡 |
| POST | `/api/accounts/transfer` | 转账 |
| POST | `/api/deposit` | 存款 |
| POST | `/api/withdraw` | 取款 |
| GET | `/api/transactions/{userId}` | 交易记录 |

## 数据库说明

默认使用 H2 文件数据库，数据持久化在 `bank-backend/data/` 目录。

如需切换 MySQL：修改 `bank-backend/src/main/resources/application.yml`，将 datasource 改为 MySQL 连接信息，并执行 `database/schema.sql` 建表。
