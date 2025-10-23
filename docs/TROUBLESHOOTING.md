# 《戰場工程師筆記：常見問題與解決方案 (TROUBLESHOOTING.md)》
# 版本：1.1

本筆記是《SETUP.md》的姊妹篇。它記錄了在搭建與初始化過程中，可能遇到的各種「地形障礙」及其標準排除協議。

當您嚴格遵循 `SETUP.md` 但仍遭遇意外時，請首先查閱本筆記。

---

### **1. WSL 環境問題**

#### **問題：在 PowerShell 中，`wsl` 命令把我帶到了一個奇怪的 `-sh:` 環境。**
*   **原因：** 您的默認 WSL 發行版被設置為了 Docker 的內部後勤系統。
*   **解決方案：**
    1.  在 PowerShell 中，執行 `wsl -l -v` 來確認。
    2.  執行 `wsl --setdefault Ubuntu`，將我們的主作戰基地設置為預設。

#### **問題：在一個全新的 Ubuntu 中，缺少 `unzip` 或 `zip` 命令。**
*   **原因：** 全新的 WSL/Ubuntu 是一個極簡系統，未預裝這些基礎工具。
*   **解決方案：**
    ```bash
    # 首先更新軟體包列表
    sudo apt update
    # 然後安裝所需的工具
    sudo apt install unzip zip
    ```

### **2. Docker 權限問題**

#### **問題：`genesis.sh` 在執行 `docker-compose build` 時，報告 `permission denied`。**
*   **原因：** 您目前前的普通用戶，尚未被加入到 `docker` 這個特殊管理員用戶群組中。
*   **解決方案：**
    1.  執行用戶群組添加指令：`sudo usermod -aG docker ${USER}`
    2.  **關鍵一步：** 關閉所有 Ubuntu 終端，然後在 PowerShell 中執行 `wsl --shutdown`，最後再重新打開一個新的 Ubuntu 終端，以使權限完全生效。


---