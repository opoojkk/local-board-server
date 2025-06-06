FROM --platform=$BUILDPLATFORM eclipse-temurin:17-jdk-jammy as builder

WORKDIR /app

# 先复制gradle wrapper文件
COPY gradlew .
COPY gradle gradle
COPY gradle.properties .
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src

# 设置gradlew可执行权限
RUN chmod +x gradlew

# 构建应用
RUN ./gradlew build -x test --no-daemon

FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
