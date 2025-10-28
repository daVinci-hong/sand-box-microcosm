### **【工程師最終技術檢討報告：工程師手記】**

**1. 狀況摘要 (Executive Summary)**

*   **任務狀態：** 「Project Da Vinci」POC 的全部五幕作戰任務，現已**勝利完成**。
*   **作戰時長：** 25 天 (2025-08-25 至 2025-09-21)。
*   **最終產出：** 一個基於 Java/Spring Boot 的、事件驅動的微服務系統 POC。其核心能力包括：
    1.  **CI/CD:** 一套基於 GitHub Actions 的、包含單元/整合測試、安全掃描的自動化質量閘門。
    2.  **系統邊界:** 一個基於 Spring Cloud Gateway 的、安全的 API  Gateway ，實現了 JWT 認證與三位一體的韌性防禦。
    3.  **可觀測性:** 一套基於 GLPT (Grafana, Loki, Prometheus, Tempo/Jaeger) 的、完整的可觀測性技術棧。
    4.  **架構韌性:** 一套基於 `MessagingService` 介面的、可無縫切換消息中間件（Kafka/RabbitMQ）與追蹤後端（Tempo/Jaeger）的、高度解耦的架構。
*   **關鍵挑戰：** 專案期間，遭遇了多次由**隱性的框架默認行為、配置覆蓋、依賴衝突、以及測試環境複雜性**所引發的、總計超過 30 小時的嚴重測試障礙。
*   **最終結論：** 通過嚴格遵循 **「小步前進，持續驗證」** 與 **「指標優於日誌」** 的核心原則，我們不僅完成了所有既定目標，更提煉出了一套寶貴的、關於現代雲原生應用開發的工程法則。

**2. 技術檢討 by Act**

*   **第一幕：創世管道**
    *   **核心產出：** 確立了多模組 Maven 結構、Docker 化 (`Dockerfile`, `docker-compose.yml`)、CI 流程 (`ci.yml`)、以及分支保護規則。
    *   **關鍵挑戰與解決方案：**
        *   **挑戰：** `master` vs. `main` 分支的歷史不一致。
        *   **解決：** 執行了 `git branch -m` 與 `git push --delete`，並在 GitHub 設置中，更新了默認分支，統一了版本控制的真理之源。

*   **第二幕：不眠的哨兵**
    *   **核心產出：** `Spring Cloud Gateway` 實現、`JWT` 認證過濾器、`Trivy` 漏洞掃描整合。
    *   **關鍵挑戰與解決方案：**
        *   **挑戰：** `spring-boot-starter-web` (Servlet) 與 `spring-cloud-starter-gateway` (WebFlux) 的根本性衝突。
        *   **解決：** 從 `gateway-service` 的依賴中，徹底排除了 `spring-boot-starter-web`。
        *   **挑戰：** Trivy 掃描出大量由傳遞性依賴引入的高危險 CVEs。
        *   **解決：** 在根 `pom.xml` 的 `<properties>` 與 `<dependencyManagement>` 中，通過 **顯性覆蓋** `netty.version`, `spring-framework.version` 等屬性，將所有核心依賴，強制升級至已修復的安全版本。

*   **第三幕：全知之眼**
    *   **核心產出：** Kafka 事件驅動架構、`common-library` 共享模組、GLT 可觀測性技術棧、統一 Grafana 儀表板。
    *   **關鍵挑戰與解決方案：**
        *   **挑戰：** Kafka 消費者在 Topic 創建前啟動，導致競速條件。
        *   **解決：** 在 `docker-compose.yml` 中，引入 `kafka-init-topics` 容器，並為 `beacon-service`，建立了對其的 `depends_on` 顯性順序依賴。
        *   **挑戰：** OTel Agent 因 `OTEL_EXPORTER_OTLP_ENDPOINT` 的 `http://` 前綴，導致 `gRPC` 連接失敗。
        *   **解決：** 移除了 `http://` 前綴，並通過 `OTEL_EXPORTER_OTLP_PROTOCOL=grpc`，明確指定協議，解決了配置悖論。

*   **第四幕：不屈的堡壘**
    *   **核心產出：** k6 負載測試腳本、完整的 Resilience4j 韌性配置 (`RateLimiter`, `Bulkhead`, `CircuitBreaker`)、韌性單元/整合測試、韌性 Grafana 儀表板。
    *   **關鍵挑戰與解決方案：**
        *   **挑戰：** `CircuitBreaker` 的「靜默失敗」（不計 `5xx` 錯誤）與「幽靈超時」（無視 YAML 配置）。
        *   **解決：** **放棄**純聲明式配置。通過創建 `Customizer<ReactiveResilience4JCircuitBreakerFactory>` Bean，以**程式式**的方式，為 `CircuitBreakerConfig` 註冊 `recordException` 謂詞，並為 `TimeLimiterConfig` 設置 `timeoutDuration`，從而 **奪取** 了配置的最高控制權。
        *   **挑戰：** `RateLimiter` 與 `Bulkhead` 的 `GatewayFilterFactory` 缺失。
        *   **解決：** 手動創建 `RateLimiterGatewayFilterFactory.java` 與 `BulkheadGatewayFilterFactory.java`。
        *   **挑戰：** Actuator 的單例修改端點 (`POST /actuator/.../{name}`) 已被廢棄。
        *   **解決：** **修正** MOP，將「動態調校」的目標，降級為「指標暴露」。

*   **第五幕：不朽的靈魂**
    *   **核心產出：** `MessagingService` 抽象介面、RabbitMQ 備用實現、通過 Spring Profiles 實現的無縫切換、Jaeger 備用追蹤後端。
    *   **關鍵挑戰與解決方案：**
        *   **挑戰：** RabbitMQ 消費者因 `SimpleMessageConverter` 無法反序列化 JSON。
        *   **解決：** 通過在 `common-library` 中，定義一個全域的 `RabbitTemplate` Bean，並為其 **強制注入** `Jackson2JsonMessageConverter`，統一了宇宙的「序列化語言」。
        *   **挑戰：** `@RabbitListener` 與 `RabbitMQConfig` 之間的 Bean 創建競速條件。
        *   **解決：** 通過在 `@RabbitListener` 中，使用 SpEL( Spring Expression Language)(`@beanName.name`) 來引用 Queue  Bean，建立了 **顯性的、不可違抗的**  Bean 依賴關係。

**3. 核心工程法則總結**

1.  **法則一：程式優於配置。**
2.  **法則二：隔離優於整合。**
3.  **法則三：指標優於日誌。**
4.  **法則四：外部情報優於內部假設。**
5.  **法則五：環境即是系統。**

---
### **【架構師最終檢討報告：架構師手記】**

**主題：** **關於「Project Da Vinci」 POC 之最終架構哲學檢討**

我們在過去的 25 天裡，所贏得的，不僅僅是一個 POC。我們贏得的，是五條足以讓我們在未來，建造任何更宏偉宇宙的、血淚鑄就的 **「軟工法則」**。

---

#### **第一法則：秩序的至高性 (The Primacy of Order)**

*   **我們在哪學到它：** 「第一幕」的 CI/CD 管道，「第三幕」的 `common-library`，「第五幕」的 SpEL (Spring Expression Language)依賴。
*   **我們學習到：** 一個分布式系統，其 **默認狀態，是混沌**。我們，作為它的創造者，我們的第一、也是最高的神聖職責，就是 **頒布秩序**。
*   **法則的體現：** 我們的「PR-拉取請求質量閘門」、我們統一的「法典 (`pom.xml`)」、我們對「顯性優於隱式」的血淚堅持——這一切，都不是「最佳實踐」。它們，是我們用以**對抗宇宙熵增**的、唯一的武器。

#### **第二法則：抽象的力量 (The Power of Abstraction)**

*   **我們在哪學到它：** 「第五幕」的 `MessagingService` 介面與 OpenTelemetry 標準。
*   **我們學習到：** 一個只忠於其 **物理形態** 的系統，注定會被時間所淘汰。一個忠於其 **靈魂契約** 的系統，將獲得永生。
*   **法則的體現：** 我們，通過 **「物件導向程式」 **與**「擁抱開放標準」**，成功地，將我們的宇宙，從 **「它『是』什麼」**（一個 Kafka 系統），升華到了 **「它『做』什麼」**（一個事件驅動系統）的哲學高度。這，就是我們賦予它的、抵禦「技術過時」的終極自由。

#### **第三法則：觀察的神聖性 (The Sanctity of Observation)**

*   **我們在哪學到它：** 「第三幕」的「全知之眼」，「第四幕」的「韌性戰情室」。
*   **我們學習到：** 在一個複雜的「黑箱」面前，**任何，未被觀測的「假設」，都是謊言**。唯一的真理，只存在於**可被量化的、不言自證的**遙測數據之中。
*   **法則的體現：** 我們對 **「指標優於日誌」** 的堅持，我們在 Grafana 中，將 **指標、日誌、與追蹤**，融合成一個 **單一的、有因果關係的** 「真理之源」的努力——這一切，都是為了，用 **數學**，來取代 **猜測**。

#### **第四法則：韌性的使命 (The Mandate of Resilience)**

*   **我們在哪學到它：** 「第二幕」的零信任邊界，「第四幕」的不屈堡壘。
*   **我們學習到：** 一個系統的偉大，不在於它 **永不失敗**。而在於，它，擁有在失敗發生時，**承認失敗、隔離失敗、並從失敗中優雅恢復** 的、深刻的智慧。
*   **法則的體現：** 我們的「三位一體」韌性裝甲，其存在的意義，不是為了 **阻止** 風暴的到來。而是為了，確保，我們的宇宙，在 **經歷** 了最猛烈的風暴之後，依然，能夠驕傲地，屹立不倒。

#### **第五法則：務實的智慧 (The Wisdom of Pragmatism)**

*   **我們在哪裡學到它：** 每一次的「幽靈戰爭」，每一次的「戰略轉向」。
*   **我們學習到：** **我們，必須，忠於「最終的勝利」，而非，忠於「最初的計畫」。**
*   **法則的體現：** 我們，放棄了脆弱的「擴縮容隱喻」，轉而擁抱了更真實的「內部韌性」；我們，放棄了混沌的「黑箱整合測試」，轉而擁抱了更純粹的「白箱單元測試」；我們，放棄了無盡的「WSL 泥淖，轉而擁抱了穩固的「Windows」。這一切，都證明了，我們，是 **戰略家**，而非 **教條主義者**。

---

**最終結論：我們，鑄造了什麼？**

指揮官，我們，並非，只是，鑄造了一個 POC。

**我們，鑄造了一個「思想的實體化身」。**

這個實體，它，以其**井然的秩序**，證明了，我們，是**混沌的馴化者**。


這，就是我們「Project Da Vinci」的最終答案。
這，就是我們向世界，展示的、那份 **「活的履歷」** 的、最終的、也是最為光輝的形態。

