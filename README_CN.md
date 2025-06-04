# Local Board Server / 本地公告板服务

A Ktor-based bulletin board service with JWT authentication and MySQL storage.  
基于 Ktor 框架的公告板服务，支持 JWT 认证和 MySQL 存储。

## Features / 功能特性
- 🛡️ JWT Authentication (JWT 认证)
- 📝 Bulletin CRUD Operations (公告增删改查)
- 📌 Pin/Unpin Bulletins (公告置顶管理)
- 📊 Pagination Support (分页查询)
- 📦 Docker Deployment (Docker 部署)

## Tech Stack / 技术栈
- **Backend**: Ktor 3.1.3
- **Database**: MySQL 8.0 + Exposed ORM
- **Auth**: JWT + BCrypt
- **Build**: Gradle 8.5
- **Container**: Docker + Docker Compose

## API Endpoints / 接口列表

### Authentication
| Method | Path       | Description          |
|--------|------------|----------------------|
| POST   | /register  | 用户注册             |
| POST   | /login     | 用户登录获取Token    |

### Bulletin Operations
| Method | Path                 | Description          |
|--------|----------------------|----------------------|
| GET    | /bulletins/list      | 分页获取公告列表     |
| POST   | /bulletins/add       | 创建新公告           |
| GET    | /bulletins/{id}      | 获取单个公告详情     |
| DELETE | /bulletins/{id}      | 删除公告             |
| POST   | /bulletins/pin/{id}  | 置顶公告             |
| POST   | /bulletins/unpin/{id}| 取消置顶             |

## Deployment with Docker Compose / Docker Compose 部署

```yaml:docker-compose.yaml
services:
  app:
    container_name: app
    image: opoojkk/local-board-server
    ports:
      - "8227:8080"
    environment:
      - DB_HOST=mysql
      - DB_JDBC_URL=jdbc:mysql://mysql:3306/local_board_db?createDatabaseIfNotExist=TRUE&allowPublicKeyRetrieval=TRUE&useSSL=FALSE&serverTimezone=UTC
      - DB_USERNAME=ktor_user
      - DB_PASSWORD=ktor_password
    depends_on:
      mysql:
        condition: service_healthy
    restart: unless-stopped

  mysql:
    container_name: mysql
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: local_board_db
      MYSQL_USER: ktor_user
      MYSQL_PASSWORD: ktor_password
      MYSQL_ROOT_PASSWORD: root_password
    volumes:
      - mysql_data:/var/lib/mysql
    ports:
      - "3306:3306"
    healthcheck:
      test: [ "CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-proot_password" ]
      interval: 5s
      timeout: 10s
      retries: 10
    restart: unless-stopped

volumes:
  mysql_data:
```

### Deployment Steps / 部署步骤
1. 构建镜像：
```bash
./gradlew build && docker build -t opoojkk/local-board-server .
```

2. 启动服务：
```bash
docker-compose up -d
```

3. 访问接口：
```
http://localhost:8227/bulletins/list
```

## Environment Variables / 环境变量
| 变量名               | 说明                         |
|----------------------|------------------------------|
| DB_JDBC_URL          | MySQL JDBC连接字符串         |
| DB_USERNAME          | 数据库用户名                 |
| DB_PASSWORD          | 数据库密码                   |
| MYSQL_ROOT_PASSWORD  | MySQL root密码               |