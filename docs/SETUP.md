# 《朝聖者的指引：本地作戰平台搭建指南 (SETUP.md)》
# 版本：1.0

歡迎，探索者。

本指南是您踏入「Project Da Vinci」宇宙的第一步，也是最重要的一步。它的使命，是引導您從一個空白的 Windows 環境開始，親手搭建起一個標準化的、與我們雲端工廠完全一致的本地作戰平台。

請將這份指南，視為 `genesis.sh` (創世儀式) 的**前置任務**。只有在完成了這份指引中的所有準備後，您才能順利地執行那場儀式，並喚醒整個宇宙。

---

### **第一步：奠定基石 —— 安裝 WSL2 與 Ubuntu**

我們的整個宇宙，運行在 Windows Subsystem for Linux (WSL2) 這個堅實的基石之上。

1.  **以系統管理員身分**打開您的 PowerShell 終端。
2.  執行以下指令，這將自動為您安裝 WSL 核心以及我們推薦的 Ubuntu 發行版：
    ```powershell
    wsl --install
    ```
3.  **重啟您的電腦**以完成安裝。
4.  重啟後，系統會提示您為新的 Ubuntu 環境創建一個用戶名和密碼。請務必記住它們。

---

### **第二步：裝備熔爐 —— 核心開發工具鏈 (SDKMAN!)**

我們將使用 SDKMAN! 這柄「瑞士軍刀」來安裝和管理我們的開發工具。這是一種「賦能」的選擇，它能讓您在未來輕鬆地切換不同版本的工具，而無需手動配置複雜的環境變量。

**後續所有命令，都應在您的 Ubuntu (WSL) 終端中執行。**

1.  **安裝 SDKMAN!**
    ```bash
    curl -s "https://get.sdkman.io" | bash
    source "$HOME/.sdkman/bin/sdkman-init.sh"
    ```

2.  **安裝 Java 引擎 (Amazon Corretto 21)**
    *   我們選用 Amazon Corretto 21 作為我們宇宙的核心動力源。
    ```bash
    # 安裝 JDK 21
    sdk install java 21.0.4-amzn
    ```

3.  **安裝 Maven 熔爐 (最新版)**
    ```bash
    sdk install maven
    ```

---

### **第三步：連接港灣 —— 容器化引擎 (Docker Desktop)**

Docker Desktop 是我們連接 Windows 主機與 WSL 宇宙的關鍵港灣。

1.  前往官方網站下載並安裝 [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop/)。
2.  在安裝過程中，請確保**已勾選** `Use WSL 2 instead of Hyper-V (recommended)` 選項。
3.  安裝完成後，啟動 Docker Desktop。
4.  **關鍵配置：**
    *   進入 Docker Desktop 的 `Settings` -> `Resources` -> `WSL Integration`。
    *   請確保您剛剛安裝的 `Ubuntu` 發行版，其右側的開關處於**啟用狀態**。這一步至關重要，它允許您在 WSL 終端中直接調用 `docker` 命令。

---

### **第四步：獲取許可 —— 版本控制 (Git)**

1.  Ubuntu 通常自帶 Git，您可以通過 `git --version` 檢查。
2.  如果沒有，或版本過低，請執行以下命令進行安裝：
    ```bash
    sudo apt update
    sudo apt install git
    ```

---

### **最終驗證：確認您的作戰平台已就緒**

在繼續下一步之前，請在您的 WSL 終端中，逐一執行以下命令，以確認所有基礎設施均已正確安裝。

| 指令 | 預期輸出 (示例) |
| :--- | :--- |
| `wsl -l -v` | 應顯示 Ubuntu 正在運行，且版本為 2 |
| `java --version` | 應顯示 OpenJDK version "21.0.4" 或更高 |
| `mvn -v` | 應顯示 Apache Maven 3.8.x 或更高 |
| `docker --version` | 應顯示 Docker version, e.g., "26.1.1" |
| `git --version` | 應顯示 git version, e.g., "2.34.1" |

---

### **朝聖之旅的終點：執行「創世儀式」**

至此，您的作戰平台已萬事俱備。

1.  **克隆專案藍圖：**
    ```bash
    git clone https://github.com/daVinci-hong/sand-box-microcosm.git
    cd sand-box-microcosm
    ```
2.  **執行儀式：**
    ```bash
    # 導航至腳本所在目錄
    cd scripts
    # 賦予執行權限 (僅需一次)
    chmod +x genesis.sh
    # 啟動創世儀式
    ./genesis.sh
    ```

遵循腳本的指引完成最後的構建。儀式結束後，您的宇宙便已準備好，可以通過 `docker-compose up` 來喚醒。

**搭建完成。歡迎加入 Project Da Vinci。**