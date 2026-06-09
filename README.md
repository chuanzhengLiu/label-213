# 景区票务管理系统

## 项目简介

景区票务管理系统是一个前后端分离的票务管理Demo系统，实现了基础的票务买卖和管理功能。系统采用SpringBoot 2.7作为后端框架，Vue 3作为前端框架，使用MySQL 8.0.36作为数据库，Redis 6.0用于存储JWT token。支持Docker Compose一键启动所有服务。

## 技术栈

### 后端
- **框架**: SpringBoot 2.7.18
- **ORM**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0.36
- **缓存**: Redis 6-alpine
- **认证**: JWT (jjwt 0.12.3)
- **密码加密**: BCrypt
- **API文档**: Swagger/Knife4j 4.3.0
- **JDK**: Amazon Corretto 17-alpine

### 前端
- **框架**: Vue 3.4.21
- **构建工具**: Vite 5.1.4
- **UI组件库**: Element Plus 2.6.0
- **路由**: Vue Router 4.3.0
- **状态管理**: Pinia 2.1.7
- **HTTP客户端**: Axios 1.6.7
- **Web服务器**: Nginx alpine
- **Node版本**: 20

### 运维
- **容器化**: Docker & Docker Compose
- **CI/CD**: GitHub Actions

## 快速启动（Docker Compose一键启动）

### 前置要求

- Docker 20.10+
- Docker Compose 2.0+
- Git

### 启动步骤

```bash
# 1. 克隆项目
git clone <repository-url>
cd scenic-manager

# 2. 一键启动所有服务
docker-compose up -d

# 3. 查看服务状态
docker-compose ps

# 4. 查看日志
docker-compose logs -f
```

启动成功后访问：
- **前端应用**: http://localhost:3000
- **后端API**: http://localhost:8088
- **Swagger文档**: http://localhost:3000/doc.html 或 http://localhost:8088/doc.html

### 停止服务

```bash
# 停止所有服务
docker-compose down

# 停止并删除数据卷（慎用，会删除所有数据）
docker-compose down -v
```

## 测试账号

系统预置了两个测试账号：

| 用户名 | 密码 | 角色 | 权限说明 |
|--------|------|------|----------|
| admin | 123456 | 超级管理员 | 可操作所有功能，包括删除景区/票种 |
| user | 123456 | 普通管理员 | 不可删除景区/票种，其他功能正常 |

## 功能介绍

### 管理员功能

1. **用户登录**: JWT认证，支持记住密码，Token存储在Redis和Cookie中
2. **景区管理**: 
   - 景区信息的增删改查
   - 支持按景区名称模糊搜索
   - 分页展示
3. **票种管理**: 
   - 票种的增删改查
   - 支持按景区和票种名称筛选
   - 库存管理（乐观锁防超卖）
   - 有效期管理
4. **订单管理**: 
   - 订单查询（多条件筛选：订单号、手机号、时间范围、订单状态）
   - 订单核销（状态1→2）
   - 订单取消（状态1→3，自动恢复库存）
5. **订单统计**: 
   - 总销售额统计
   - 总销量统计
   - 票种销量排行（TOP10）

### 游客功能

1. **浏览景区**: 查看所有景区信息（名称、介绍、所在地区）
2. **查看票种**: 查看各景区的票种信息（价格、库存、有效期）
3. **在线购票**: 
   - 选择票种和数量
   - 填写购票人姓名和手机号
   - 提交订单并获取订单编号

### 核心特性

- ✅ **乐观锁防超卖**: 使用MyBatis-Plus的version字段防止并发超卖
- ✅ **订单编号自动生成**: yyyyMMddHHmmss + 6位随机数（保证唯一性）
- ✅ **角色权限控制**: 超级管理员/普通管理员权限区分
- ✅ **统一异常处理**: 全局异常处理器统一返回格式
- ✅ **响应式布局**: 支持PC和移动端自适应
- ✅ **Token管理**: Redis + Cookie双重存储，ThreadLocal封装当前用户信息
- ✅ **中文支持**: 完整的UTF-8字符集支持，数据库和接口支持中文

## 项目结构

```
scenic-manager/
├── scenic-manager-backend/          # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/scenic/
│   │   │   │   ├── common/          # 通用类（Result、异常处理、ThreadLocal）
│   │   │   │   ├── config/          # 配置类（Security、JWT、Redis、MyBatis-Plus）
│   │   │   │   ├── controller/      # 控制器层
│   │   │   │   ├── dto/             # 数据传输对象
│   │   │   │   ├── entity/          # 实体类
│   │   │   │   ├── filter/          # 过滤器（JWT认证）
│   │   │   │   ├── mapper/          # Mapper接口
│   │   │   │   ├── service/         # 服务层
│   │   │   │   └── util/            # 工具类（JWT工具）
│   │   │   └── resources/
│   │   │       ├── mapper/          # MyBatis XML映射文件
│   │   │       └── application.yml  # 配置文件
│   │   └── pom.xml
│   └── Dockerfile                   # 后端Docker镜像构建文件
├── scenic-manager-frontend/         # 前端项目
│   ├── src/
│   │   ├── api/                     # API接口封装
│   │   ├── components/              # 组件
│   │   ├── layouts/                 # 布局组件
│   │   ├── router/                  # 路由配置
│   │   ├── store/                   # Pinia状态管理
│   │   ├── utils/                   # 工具函数
│   │   ├── views/                   # 页面组件
│   │   │   ├── admin/               # 管理端页面
│   │   │   └── Visitor.vue          # 游客端页面
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   ├── vite.config.js
│   ├── Dockerfile                   # 前端Docker镜像构建文件
│   ├── nginx.conf                   # Nginx主配置文件
│   └── default.conf                 # Nginx站点配置
├── sql/                             # 数据库脚本
│   ├── init.sql                     # 建表SQL和测试数据
│   └── my.cnf                       # MySQL字符集配置
├── .github/
│   └── workflows/
│       └── ci.yml                   # GitHub Actions CI/CD配置
├── docker-compose.yml               # Docker Compose编排文件
└── README.md                        # 项目说明文档
```

## Docker服务说明

### 服务架构

系统使用Docker Compose编排以下服务：

1. **MySQL 8.0.36**: 数据持久化存储
   - 端口: 3306
   - 数据卷: mysql_data
   - 自动初始化数据库和表结构

2. **Redis 6-alpine**: JWT Token缓存
   - 端口: 6379
   - 数据卷: redis_data
   - 启用AOF持久化

3. **Backend (SpringBoot)**: 后端API服务
   - 端口: 8088
   - 基于Amazon Corretto 17-alpine
   - 依赖MySQL和Redis健康检查

4. **Frontend (Nginx)**: 前端Web服务
   - 端口: 3000（对外暴露）
   - 基于Nginx alpine
   - 代理/api/请求到后端
   - 服务静态文件

### 网络配置

所有服务在同一个Docker网络（scenic-network）中，通过服务名互相访问：
- 前端访问后端: `http://backend:8088`
- 后端访问MySQL: `mysql:3306`
- 后端访问Redis: `redis:6379`

## 核心业务逻辑

### 1. 订单创建流程

```
1. 校验票种库存是否充足
   ↓
2. 使用乐观锁扣减库存（version字段自增）
   ↓
3. 生成订单编号（yyyyMMddHHmmss + 6位随机数）
   ↓
4. 计算订单总金额（价格 × 数量）
   ↓
5. 创建订单，状态默认为1（已下单）
```

### 2. 订单状态管理

- **状态1（已下单）**: 可以核销或取消
- **状态2（已核销）**: 不可修改
- **状态3（已取消）**: 不可修改，且会恢复票种库存

### 3. 权限控制

- **超级管理员（role=1）**: 可操作所有功能，包括删除景区/票种
- **普通管理员（role=2）**: 不可删除景区/票种，其他功能正常

### 4. 防超卖机制

使用MyBatis-Plus的乐观锁功能，通过version字段防止并发情况下的超卖问题。当并发下单时，只有一个请求能成功更新version，其他请求会失败并提示重试。

## API接口文档

启动服务后访问Swagger文档：http://localhost:3000/doc.html

### 主要接口

#### 用户模块
- `POST /api/user/login` - 用户登录
- `GET /api/user/current` - 获取当前用户信息
- `POST /api/user/logout` - 用户登出

#### 景区模块
- `GET /api/scenic/list` - 分页查询景区
- `POST /api/scenic/add` - 新增景区
- `PUT /api/scenic/update` - 修改景区
- `DELETE /api/scenic/delete/{id}` - 删除景区

#### 票种模块
- `GET /api/ticket/list` - 分页查询票种
- `POST /api/ticket/add` - 新增票种
- `PUT /api/ticket/update` - 修改票种
- `DELETE /api/ticket/delete/{id}` - 删除票种
- `GET /api/ticket/stock/{id}` - 获取票种库存

#### 订单模块
- `POST /api/order/create` - 创建订单
- `GET /api/order/list` - 分页查询订单
- `PUT /api/order/updateStatus` - 修改订单状态
- `GET /api/order/statistics` - 订单统计

### 统一返回格式

```json
{
  "code": 200,        // 200成功，401未授权，500失败
  "msg": "操作成功",   // 提示信息
  "data": {}          // 数据对象/数组
}
```

## 常见问题

### 1. 服务启动失败

**问题**: MySQL连接失败
- 检查MySQL容器是否正常运行: `docker-compose ps`
- 查看MySQL日志: `docker-compose logs mysql`
- 等待MySQL完全启动（约30秒）后再启动后端

**问题**: 端口被占用
- 检查端口占用: `lsof -i:3000` 或 `lsof -i:8088`
- 修改docker-compose.yml中的端口映射

### 2. 中文乱码

系统已完整配置UTF-8支持：
- MySQL字符集: utf8mb4
- 数据库连接URL包含字符集参数
- Nginx字符集配置为utf-8

如仍有问题，检查：
- MySQL容器的字符集配置: `docker-compose exec mysql mysql -uroot -proot123456 -e "SHOW VARIABLES LIKE 'character%'"`
- 数据库表的字符集: `SHOW CREATE TABLE scenic_spot;`

### 3. Token失效

- Token有效期为2小时
- Token存储在Redis中，过期后需重新登录
- 前端会自动处理401错误，跳转登录页

### 4. 库存扣减失败

- 使用乐观锁机制，并发时可能失败
- 失败时提示"库存扣减失败，请重试"
- 建议前端实现重试机制

### 5. 权限不足

普通管理员尝试删除操作时，返回错误提示"无权限删除"。

## 开发说明

### 本地开发（不使用Docker）

#### 后端开发

```bash
cd scenic-manager-backend

# 修改application.yml中的数据库和Redis连接为localhost

# 启动应用
mvn spring-boot:run
```

#### 前端开发

```bash
cd scenic-manager-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 代码规范

- 后端: Java命名规范，统一使用Result<T>封装返回
- 前端: ESLint规范，使用Composition API
- 所有代码添加中文注释

## CI/CD

项目使用GitHub Actions实现持续集成：

- **触发条件**: push到main/master/develop分支或创建PR
- **构建任务**: 
  - 后端Maven构建和测试
  - 前端npm构建
  - Docker镜像构建验证

查看CI状态：项目首页显示GitHub Actions状态

## 许可证

MIT License

## 联系方式

如有问题或建议，欢迎提交Issue。
