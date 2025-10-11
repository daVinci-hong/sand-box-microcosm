# === 階段一：建造者 (The Builder) ===
FROM maven:3.9-amazoncorretto-21 AS builder
WORKDIR /build

# 複製父 POM，以利用 Docker 緩存
COPY pom.xml .
# 複製所有源代碼
COPY src ./src

# =================================================================
# ==           CRITICAL FIX: BUILD THE ENTIRE UNIVERSE           ==
# =================================================================
# 我們不再只建造單一模塊。我們從根目錄，執行 'mvn install'。
# Maven 將會智能地，按照正確的順序（common-library -> beacon-service），
# 建造所有它需要的東西。
RUN mvn -B clean install -DskipTests

# === 階段二：運行者 (The Runner) ===
FROM amazoncorretto:21-alpine-jdk
WORKDIR /app

# 從「建造者」階段，精確地提取已編譯好的、正確的 JAR 文件。
COPY --from=builder /build/src/beacon-service/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]