# === 階段一：建造者 (The Builder) ===
FROM maven:3.9-amazoncorretto-21 AS builder
WORKDIR /build

COPY pom.xml .
COPY src ./src

# =================================================================
# ==           CRITICAL FIX: BUILD THE ENTIRE UNIVERSE           ==
# =================================================================
RUN mvn -B clean install -DskipTests

# === 階段二：運行者 (The Runner) ===
FROM amazoncorretto:21-alpine-jdk
WORKDIR /app

COPY --from=builder /build/src/gateway-service/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]