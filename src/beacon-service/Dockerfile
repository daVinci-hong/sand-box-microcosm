#### **1.2 `beacon-service` 的 Dockerfile**

# === 階段一：建造者 (The Builder) ===
FROM maven:3.9-amazoncorretto-21 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
# 關鍵不同點：指向 beacon-service 的 pom.xml
RUN mvn -B -f src/beacon-service/pom.xml clean package -DskipTests

# === 階段二：運行者 (The Runner) ===
FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
# 關鍵不同點：從 beacon-service 的 target 目錄提取 JAR
COPY --from=builder /build/src/beacon-service/target/*.jar app.jar
# 關鍵不同點：暴露 8081 端口
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]