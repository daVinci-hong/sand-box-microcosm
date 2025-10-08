# === 階段一：建造者 (The Builder) ===
# 使用包含 Maven 與 JDK 21 的標準鏡像作為我們的「建造車間」。
FROM maven:3.9-amazoncorretto-21 AS builder

# 設定在容器內的工作目錄。
WORKDIR /build

# 為了最大化利用 Docker 的層緩存機制，我們先複製父 POM 文件。
COPY pom.xml .

# 複製所有模塊的源代碼。
COPY src ./src

# 執行 Maven 打包指令，專門針對 gateway-service 模塊。
# -B: Batch mode, -f: 指定 pom 文件路徑, -DskipTests: 跳過測試以加速構建。
RUN mvn -B -f src/gateway-service/pom.xml clean package -DskipTests

# === 階段二：運行者 (The Runner) ===
# 使用您指定的、輕量化的 Alpine JDK 鏡像作為最終的「維生艙」。
FROM amazoncorretto:21-alpine-jdk

# 設定在容器內的工作目錄。
WORKDIR /app

# 從「建造者」階段，精確地提取已編譯好的 JAR 文件，並重命名為 app.jar。
COPY --from=builder /build/src/gateway-service/target/*.jar app.jar

# 向外界聲明此維生艙將暴露 8080 port。
EXPOSE 8080

# 定義容器啟動時執行的命令：運行我們的 Java 應用程式。
ENTRYPOINT ["java", "-jar", "app.jar"]