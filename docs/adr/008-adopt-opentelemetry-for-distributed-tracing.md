# ADR-008: 採用 OpenTelemetry 實現分布式追蹤

**狀態：** 已接納 (Accepted)
**日期：** 【2025-09-10】

---

## 背景 (Context)

我們已成功部署了指標（Prometheus）和日誌（Loki）系統。我們現在能夠回答「發生了什麼 (What)」和「為什麼會發生 (Why)」。然而，在一個由多個非同步服務（通過 Kafka 連接）構成的分布式系統中，我們還面臨一個更為致命的挑戰：**我們無法回答「一個完整的業務流程，其端到端的路徑是怎樣的 (Where & When)」**。

例如，當一個請求進入 `gateway-service`，觸發一個 Kafka 事件，並最終被 `beacon-service` 消費時，我們如何將這三個孤立的動作，關聯成一個**單一的、有因果關係的「故事」**？

沒有一個統一的上下文標識符（即 Trace ID）來串聯這一切，我們的日誌和指標，就只是一堆散落的、無法形成完整畫面的拼圖碎片。

## 決策 (Decision)

我們決定採用 **OpenTelemetry (OTel)** 作為我們宇宙中，實現分布式追蹤的**唯一、也是最終的標準**。

具體的實施策略如下：

1.  **自動化檢測 (Auto-Instrumentation):**
    *   我們將採用 OpenTelemetry 提供的 **Java Agent** (`opentelemetry-javaagent.jar`)。我們將通過修改 `docker-compose.yml`，在啟動每一個 Java 服務時，自動掛載並啟用這個 Agent。
    *   這個 Agent 將**無需任何程式碼修改**，就能自動地為我們檢測主流的框架和庫（如 Spring Boot, Kafka Clients, Netty），並為它們生成和傳播追蹤資料（Spans）。

2.  **上下文傳播 (Context Propagation):**
    *   OTel Agent 將自動處理跨 process 的上下文傳播。例如，當 `gateway-service` 生產一個 Kafka 消息時，Agent 會自動將當前的 Trace Context（Trace ID, Span ID）注入到 Kafka 消息的頭部。當 `beacon-service` 消費這個消息時，Agent 會自動從頭部提取這個上下文，並將其作為新創建的 Span 的父級，從而**無縫地將兩個服務的追蹤鏈路串聯起來**。

3.  **資料導出 (Exporting):**
    *   我們將配置 OTel Agent，使其將所有採集到的追蹤資料，以 **OTLP (OpenTelemetry Protocol)** 格式，導出到一個集中的追蹤後端。

4.  **追蹤後端 (Tracing Backend):**
    *   我們將在 `docker-compose.yml` 中，部署 **Grafana Tempo** 作為我們的追蹤資料存儲與查詢後端。Tempo 是一個專為大規模分布式追蹤設計的、高性價比的存儲系統。

## 理由 (Consequences)

### 積極方面：

*   **行業標準與供應商無關 (Industry Standard & Vendor-Neutral):**
    *   OpenTelemetry 是由 CNCF（雲原生計算基金會）支持的、繼 Kubernetes 和 Prometheus 之後的**下一個世界級標準**。通過全面擁抱 OTel，我們確保了我們的檢測層，與任何特定的後端供應商（如 Jaeger, Zipkin, DataDog）**完全解耦**。這完美地踐行了「無痛換心」的設計哲學。
*   **零程式碼修改 (Zero Code Change):**
    *   採用 Java Agent 的自動化檢測方案，其最大的魅力在於，我們**幾乎不需要修改任何一行現有的業務程式碼**，就能為我們的整個宇宙，植入強大的分布式追蹤能力。這極大地降低了我們的實施成本和對現有程式碼的侵入性。
*   **與 Grafana 生態的完美閉環 (Closing the Loop with Grafana):**
    *   Tempo 作為 Grafana 家族的一員，與 Grafana、Loki、Prometheus 之間存在著**無與倫比的、原生的整合能力**。我們將能夠在 Grafana 中，實現從一個指標圖表（Prometheus），直接跳轉到觸發該指標的相關追蹤（Tempo），再從一個追蹤的 Span，直接跳轉到與該 Span 相關的具體日誌（Loki）。**這，就是「全知之眼」的終極形態——指標、日誌、追蹤的完美融合。**
*   **統一的未來 (A Unified Future):**
    *   OpenTelemetry 的願景，是最終統一 Metrics, Logs, Traces 的採集標準。今天我們用它來實現追蹤，明天我們就可以用**同一個 OTel Collector**，來統一處理我們的所有遙測資料。投資 OTel，就是投資一個更簡單、更統一的未來。

### 消極方面（權衡）：

*   **Agent 的「黑箱」效應 (The "Black Box" Effect):**
    *   自動化檢測的便利性，也帶來了一定的「黑箱」效應。如果出現問題，排查 Agent 的內部工作原理，可能會比排查手動檢測的程式碼更具挑戰性。
*   **資源開銷 (Resource Overhead):**
    *   運行 Java Agent 會給每個服務帶來一定的啟動延遲和運行時內存開銷。部署 Tempo 也會進一步增加我們本地環境的資源壓力。我們必須確保「中央電網」的能量充足。

---
**文件結束**