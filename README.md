# Local Board Server

A Ktor-based bulletin board service with JWT authentication and MySQL storage.

## Features
- 🛡️ JWT Authentication
- 📝 Bulletin CRUD Operations
- 📌 Pin/Unpin Management
- 📊 Pagination Support
- 📦 Docker Deployment

## Tech Stack
- **Backend**: Ktor 3.1.3
- **Database**: MySQL 8.0 + Exposed ORM
- **Auth**: JWT + BCrypt
- **Build**: Gradle 8.5
- **Container**: Docker + Docker Compose

## API Endpoints

### Authentication
| Method | Path       | Description                   |
|--------|------------|-------------------------------|
| POST   | /register  | User registration            |
| POST   | /login     | User login to obtain JWT token |

### Bulletin Operations
| Method | Path                 | Description                      |
|--------|----------------------|----------------------------------|
| GET    | /bulletins/list      | Paginated bulletin list retrieval |
| POST   | /bulletins/add       | Create new bulletin             |
| GET    | /bulletins/{id}      | Get bulletin details            |
| DELETE | /bulletins/{id}      | Delete bulletin                 |
| POST   | /bulletins/pin/{id}  | Pin bulletin to top              |
| POST   | /bulletins/unpin/{id}| Unpin bulletin                   |

## Deployment with Docker Compose

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

### Deployment Steps
1. Build image:
```bash
./gradlew build && docker build -t opoojkk/local-board-server .
```

2. Start services:
```bash
docker-compose up -d
```

3. Access API endpoint:
```
http://localhost:8227/bulletins/list
```

## Environment Variables
| Variable              | Description                      |
|-----------------------|----------------------------------|
| DB_JDBC_URL           | MySQL JDBC connection string    |
| DB_USERNAME           | Database username               |
| DB_PASSWORD           | Database password               |
| MYSQL_ROOT_PASSWORD   | MySQL root password             |
```