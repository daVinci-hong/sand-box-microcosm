# ADR-002: 採用 Spring Cloud Gateway 處理 API 邊界，並引入 Resilience4j 實現容錯

**狀態：** 提議中 (Proposed)
**日期：** 【2025-09-04】

---

## 背景 (Context)

隨著「沙盤微縮宇宙」從「第一幕」的基礎管道建設，進入「第二幕」的內部強化階段，我們面臨兩個新的、緊迫的架構挑戰：

1.  **邊界模糊問題：** 當前，我們的所有服務（`gateway-service`, `beacon-service`）都可以被外部直接訪問。這缺乏一個統一的入口來管理路由、執行安全策略、以及監控流量。
2.  **級聯故障風險 (Risk of Cascading Failures)：** 在微服務架構中，一個下游服務的延遲或失敗，極有可能通過同步調用鏈，引發上游服務的資源耗盡，最終導致整個系統的雪崩式崩潰。

我們必須引入相應的技術組件，來建立清晰的**「系統邊界」**，並植入**「容錯機制」**。

## 決策 (Decision)

我們決定採取以下組合方案，來應對上述挑戰：

1.  **API 網關層：** 我們將正式採用 **Spring Cloud Gateway**，將 `gateway-service` 從一個普通的 Spring Boot 應用，升級為一個功能完備的 API 網關。
2.  **容錯機制層：** 我們將引入輕量級的 **Resilience4j** 庫，作為我們實現服務間調用容錯（特別是熔斷機制）的標準工具。

## 理由 (Consequences)

### 積極方面：

*   **統一的流量入口 (Single Point of Entry):**
    *   Spring Cloud Gateway 為我們提供了一個統一的門面。所有外部請求都將先經過網關，這使得我們可以集中實現**路由 (Routing)、請求過濾 (Filtering)、限流 (Rate Limiting)、認證/授權 (Authentication/Authorization)**等橫切關注點。這完美地契合了「零信任堡壘」的設計哲學。

*   **與技術棧無縫集成 (Seamless Integration):**
    *   Spring Cloud Gateway 是 Spring Cloud 生態系統的原生成員，與 Spring Boot 的集成極其順暢。它基於響應式編程模型 (Project Reactor)，性能卓越，非常適合處理高併發的 I/O 密集型場景。

*   **輕量且強大的容錯 (Lightweight & Powerful Fault Tolerance):**
    *   Resilience4j 是一個純粹的 Java 庫，沒有任何外部依賴，這使得它非常輕量。與 Hystrix 等舊方案不同，它採用了更現代的函數式編程風格，易於與 CompletableFuture 或響應式流結合。
    *   它不僅提供**熔斷器 (Circuit Breaker)**，還提供了**限流器 (Rate Limiter)、重試 (Retry)、艙壁隔離 (Bulkhead)**等多種韌性模式，為我們後續的韌性設計提供了豐富的「武器庫」。

*   **聲明式配置 (Declarative Configuration):**
    *   Spring Cloud Gateway 和 Resilience4j 的大部分核心功能，都可以通過 `application.yml` 進行聲明式配置。這使得我們的韌性策略和路由規則，變得**高度可讀、易於管理，並能納入版本控制**，完全符合「基礎設施即代碼」的原則。

### 消極方面（權衡）：

*   **引入了新的複雜性：** 引入 API 網關，意味著我們的請求鏈路上增加了一個新的節點。這會輕微增加系統的延遲，並在排查問題時，需要多考慮一個環節。我們認為，為了獲得統一邊界管理所帶來的巨大好處，這一點複雜性的增加是完全值得的。
*   **響應式編程的學習曲線：** Spring Cloud Gateway 基於響應式模型，這對於習慣了傳統 Servlet 模型的開發者來說，有一定的學習曲線。考慮到本專案的探索性質，我們將此視為一次寶貴的學習與實踐機會。

---
**文件結束**