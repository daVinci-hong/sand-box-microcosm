# ADR-006: 採用 Prometheus 技術集進行指標監控

**狀態：** 已接納 (Accepted)
**日期：** 【2025-09-09】

---

## 背景 (Context)

隨著「第三幕：全知之眼」的開啟，我們的首要目標是**將我們宇宙的內部狀態，從一個完全的「黑箱」，轉變為一個可被度量的「白箱」**。

當前，我們的服務正在運行，事件在 Kafka 中流動，但我們對以下關鍵問題一無所知：
*   每個服務的健康狀況如何？（CPU、內存使用率）
*   API 的回應時間是多少？請求的吞吐量 (RPS) 有多大？
*   JVM 的內部狀態是否健康？（堆內存、垃圾回收、線程數）
*   Kafka 的消息生產和消費速率是多少？

沒有這些**量化的指標 (Metrics)**，我們就如同在黑暗中航行，無法做出任何基於資料的決策，更無法實現「洞察與掌控」。

## 決策 (Decision)

我們決定採用以 **Prometheus** 為核心的技術集，作為我們宇宙中指標採集、存儲與可視化的標準解決方案。

具體的實施策略如下：

1.  **指標暴露 (Instrumentation):**
    *   我們將利用 Spring Boot Actuator 中內建的 **Micrometer** 庫，為我們的所有微服務（`gateway-service`, `beacon-service`）自動暴露一個兼容 Prometheus 格式的 `/actuator/prometheus` 端點。

2.  **指標採集與存儲 (Collection & Storage):**
    *   我們將在 `docker-compose.yml` 中，部署一個 **Prometheus** 服務。
    *   我們將提供一個 `prometheus.yml` 配置文件，指示 Prometheus 定期地、主動地從我們各個微服務的 `/actuator/prometheus` 端點**拉取 (scrape)** 指標資料，並將其存儲在內置的時序資料庫 (TSDB) 中。

3.  **指標可視化 (Visualization):**
    *   我們將在 `docker-compose.yml` 中，部署一個 **Grafana** 服務。
    *   我們將預先配置 Grafana，使其自動連接到 Prometheus 作為其核心資料源。
    *   我們將在 Grafana 中，創建我們的第一個儀表板 (Dashboard)，用於可視化展示從 Prometheus 查詢到的關鍵性能指標。

## 理由 (Consequences)

### 積極方面：

*   **行業標準與生態系統 (Industry Standard & Ecosystem):**
    *   Prometheus 已成為雲原生領域**事實上的指標監控標準**，擁有無與倫比的社區支持和生態系統。幾乎所有現代基礎設施組件（如 Kafka, Nginx）都提供原生的 Prometheus 指標導出器 (exporter)。
*   **Micrometer 的抽象能力 (Power of Abstraction):**
    *   通過依賴 Micrometer（而非直接依賴 Prometheus 的客戶端庫），我們的應用程序程式碼與底層的監控系統實現了**完美解耦**。未來，如果我們想遷移到另一個監控系統（如 DataDog, InfluxDB），我們**幾乎不需要修改任何業務程式碼**，只需更換依賴和配置即可。這完美地踐行了「無痛換心」的設計哲學。
*   **Grafana 的統一可視化 (Unified Visualization):**
    *   Grafana 是一個極其強大的、與資料源無關的可視化平台。今天我們用它來展示 Prometheus 的指標，明天我們就可以用**同一個 Grafana**，來展示 Loki 的日誌和 Tempo 的追蹤。這使得 Grafana 成為我們構建**「統一儀表板」**、實現「全知之眼」夢想的**唯一、也是最完美的選擇**。
*   **拉取模型的簡潔性 (Simplicity of Pull Model):**
    *   Prometheus 的拉取模型，使得配置和故障排查變得非常簡單。我們只需要在 Prometheus 的配置文件中，告訴它要去哪裡獲取資料即可，而無需在每一個應用程序中配置複雜的推送邏輯。

### 消極方面（權衡）：

*   **資源消耗：** 部署 Prometheus 和 Grafana 將會佔用我們本地環境更多的 CPU 和內存資源。根據我們在「幽靈戰爭」中學到的教訓，我們必須時刻關注 Docker Desktop 的資源分配，確保「中央電網」的能量充足。
*   **資料的短暫性：** Prometheus 被設計為一個用於存儲短期、高頻率指標的監控和告警系統，而非一個長期的資料歸檔解決方案。在我們 POC 的範圍內，這完全足夠。在生產環境中，可能需要考慮與 Thanos 等長期存儲方案的整合。

---
**文件結束**