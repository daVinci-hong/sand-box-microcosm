# 《工程啟動協議 v1.0》
# (Engineering Kick-off Protocol - EKP)

**文件編號：** EKP-v1.0
**頒布單位：** 【第一作戰司令部】
**生效日期：** 【此處填寫當前日期】

---

## **第一章：協議目的 (Protocol Objective)**

本協議旨在為「沙盤微縮宇宙」的所有工程活動，建立一套統一的、不可違背的**「工程憲法」**。它定義了我們的質量標準、協作流程與工作紀律，是確保我們所有創造活動，都能在**秩序與和諧**中進行的最高保障。

本協議，是我們**「橫貫任務 C.1：建立並完善自動化測試體系」**的最高**法理依據**。

## **第二章：工程團隊內部憲法 (The Engineering Constitution)**

### **2.1 程式碼風格與品質 (Code Style & Quality)**
*   **規範：** 所有 Java 程式碼必須嚴格遵循 **Google Java Style Guide**。
*   **執行：** 通過在 CI 流程中集成**靜態分析工具 (Checkstyle)** 來自動化強制執行。任何不符合風格規範的代碼，都將導致「質量閘門」關閉。

### **2.2 版本控制策略 (Version Control Strategy)**
*   **規範：** 我們嚴格採用**「受保護主幹的拉取請求工作流 (Pull Request Workflow with Protected Main Branch)」**。
    *   `main` 分支是唯一的主分支，受分支保護規則捍衛，永遠處於可部署狀態。
    *   **嚴禁**任何形式的直接推送 (`push`) 到 `main` 分支。
    *   所有變更，必須通過從 `main` 創建特性分支 (`feature/...`)，並在完成後提交拉取請求 (PR) 的方式進行。
    *   PR **必須**成功通過所有 CI 檢查（編譯、測試、安全掃描），才能獲得被合併的資格。

### **2.3 提交訊息規範 (Commit Message Convention)**
*   **規範：** 所有 Git 提交訊息必須嚴格遵循 **Conventional Commits v1.0.0** 規範。
*   **價值：** 此舉旨在確保提交歷史的絕對清晰性，並為未來的自動化版本管理與變更日誌生成，奠定堅實基礎。

### **2.4 測試哲學 (Testing Philosophy)**
*   **規範：** 我們莊嚴地承諾，遵循**測試金字塔**原則，來構建我們的「質量與信心的安全網」。
    *   **單元測試 (Unit Tests):** **(基石)** 使用 **JUnit 5 + Mockito**，專注於單個組件的邏輯正確性。目標覆蓋率 > 80%。
    *   **整合測試 (Integration Tests):** **(中堅)** 使用 **Testcontainers**，驗證我們的服務與真實外部依賴（如 Kafka, MongoDB）的交互。
    *   **端到端測試 (E2E Tests):** **(尖頂)** 使用 **k6** 或 **Postman/Newman**，從最終用戶視角，對完整的業務流程進行黑箱驗證。

### **2.5 文檔化紀律 (Documentation Discipline)**
*   **規範：** 我們相信，文檔與代碼同等重要。
    *   **架構決策：** 所有重大的架構決策，**必須**以 **ADR (Architecture Decision Record)** 的形式，記錄在 `/docs/adr` 目錄下。
    *   **API 契約：** 所有對外暴露的 API，**必須**使用 **OpenAPI 3.0 (Swagger)** 規範進行定義，並存放於 `/docs/api` 目錄下。

---
**文件結束**