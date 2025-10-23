# === 階段一：建造者 (The Builder) ===
FROM maven:3.9-amazoncorretto-21 AS builder
WORKDIR /build

# =================================================================
# == 優化 1：先複製 POM 文件，利用 Docker 層緩存下載依賴 ==
# =================================================================
# 複製根 POM 和所有模塊的 POM 文件
COPY pom.xml .
COPY src/common-library/pom.xml ./src/common-library/
COPY src/gateway-service/pom.xml ./src/gateway-service/
COPY src/beacon-service/pom.xml ./src/beacon-service/

# 下載所有依賴（這一層會被緩存，除非 POM 文件改變）
RUN mvn -B dependency:go-offline -DskipTests

# =================================================================
# == 優化 2：然後複製源代碼 ==
# =================================================================
COPY src ./src

# =================================================================
# == 優化 3：構建整個項目 ==
# =================================================================
RUN mvn -B clean install -DskipTests

# === 階段二：運行者 (The Runner) ===
FROM amazoncorretto:21-alpine-jdk
WORKDIR /app

COPY --from=builder /build/src/gateway-service/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]