# 《/docs 轄區組織綱領 v1.0》
**文件編號：** DOC-INFRA-v1.0
**制定單位：** 【建築師的聖殿】
**授權指令：**
**生效日期：** 【2025-08-21】
**目的：**
---

## 第一章：總體設計哲學

本文件定義了 `/docs` 目錄的內部組織結構。其設計遵循以下核心原則：

1.  **職責單一 (Single Responsibility):** 每個子目錄都有一個清晰、獨立的用途。
2.  **易於發現 (Discoverability):** 結構需直觀，讓任何成員都能快速找到所需信息。
3.  **面向未來 (Future-Proof):** 結構需具備擴展性，以容納專案未來可能產生的新型文檔。

## 第二章：子目錄結構與職責

### 📁 `/adr` - 架構決策記錄 (Architecture Decision Records)

*   **用途：** 存放所有對專案產生重大影響的架構決策。每一個 ADR 文件都將記錄一個決策的背景、備選方案、最終決定及其理由。這是我們「過程即展現」哲學的核心體現。
*   **文件格式：** `NNN-[decision-title].md` (例如: `001-use-github-flow.md`)

### 📁 `/diagrams` - 技術架構圖 (Technical Architecture Diagrams)

*   **用途：** 存放所有視覺化作戰資產，包括但不限於 C4 模型圖、時序圖、網絡拓撲圖、CI/CD 流程圖等。源文件與導出文件將一併存放。
*   **文件格式：**
    *   源文件: `[diagram-id]_[description].puml` (PlantUML)
    *   導出文件: `[diagram-id]_[description].png` / `.svg`
    *   示例: `arc_c2_container_diagram.puml`

### 📁 `/guides` - 開發與運維指南 (Developer & Operations Guides)

*   **用途：** 提供詳細的、面向任務的「如何做 (How-To)」文檔。這將是新成員的 onboarding 手冊，也是日常開發的參考書。
*   **文件示例：**
    *   `how-to-add-a-new-service.md`
    *   `local-debugging-with-kafka.md`
    *   `testing-strategy-in-practice.md`

### 📁 `/api` - API 規格文檔 (API Specifications)

*   **用途：** 存放所有微服務對外暴露的 API 的規格定義文件。我們將採用 OpenAPI 3.0 標準。
*   **文件示例：**
    *   `gateway-api-v1.yml`
    *   `beacon-api-v1.yml`

### 📁 `/runbooks` - 運維手冊 (Operational Runbooks)

*   **用途：** 提供在特定場景下（尤其是告警觸發時）的標準化操作程序 (SOP)。這是我們「潮汐掌控者」劇本的配套文檔，體現了運維的專業性。
*   **文件示例：**
    *   `handling-highload-alert.md`
    *   `manual-failover-procedure-for-redis.md`

### 📁 `/concepts` - 核心概念闡述 (Core Concept Explanations)

*   **用途：** 對專案中一些核心的、跨領域的設計思想或概念進行深入的、原理性的闡述。這些文件解釋「是什麼 (What)」和「為什麼 (Why)」，而非「如何做 (How)」。
*   **文件示例：**
    *   `our-observability-philosophy.md`
    *   `service-mesh-metaphor-implementation.md`

## 第三章：根目錄核心文件

### 📄 `SETUP.md` - 環境搭建指南

*   **用途：** 專案的**首要入口**。提供從零開始，在一個全新的開發環境中，搭建、配置並成功運行「沙盤微縮宇宙」所需的所有步驟。

### 📄 `_DIRECTORY_STRUCTURE.md` - (本文件)

*   **用途：** 作為本目錄的「地圖」和「索引」，解釋 `/docs` 目錄自身的組織結構。

---
**文件結束**