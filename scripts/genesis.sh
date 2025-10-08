#!/bin/bash

# =================================================================
#               《 建築師的饋贈 (The Architect's Gift) 》
#
# 文件代號：genesis.sh (創世腳本)
# 版本：1.0
#
# 核心意圖：
#   本腳本並非僅為工具，而是一場精心設計的「創世儀式」。
#   它將一個複雜、易錯的本地環境搭建過程，轉化為一個標準化、
#   具備自我診斷能力的引導式體驗。
#   其存在的目的，是為了根除環境不一致所引發的混亂，並將我們
#   「系統性賦能」的哲學，贈予每一位進入此宇宙的探索者。
#
# =================================================================

# --- 序章：定義儀式的視覺語言 ---
# 為儀式的反饋，定義清晰的顏色代碼，用以傳達成功、警告與失敗。
C_RED='\033[0;31m'
C_GREEN='\033[0;32m'
C_YELLOW='\033[0;33m'
C_NC='\033[0m' # No Color

# 設立一個標記，用以記錄儀式前置檢查是否通過。
CHECKS_FAILED=0

echo -e "${C_GREEN}===================================================${C_NC}"
echo -e "${C_GREEN}    歡迎，探索者。建築師的饋贈，創世儀式，啟動。    ${C_NC}"
echo -e "${C_GREEN}===================================================${C_NC}"
echo ""

# --- 第一幕：守護者的詰問 (環境自檢) ---
# 在對宇宙進行任何構建之前，守護者將首先檢驗您的基石是否穩固。
echo "第一幕：檢驗基石 —— 驗證本地基礎設施..."

# 詰問一：Java 的心臟是否跳動？(JDK 17+)
echo -n "  - 檢測 Java 引擎 (JDK 17+)... "
if command -v java &> /dev/null; then
    JAVA_VERSION_STRING=$(java -version 2>&1)
    if [[ "$JAVA_VERSION_STRING" =~ \"([0-9]+)\. ]]; then
        JAVA_MAJOR_VERSION=${BASH_REMATCH[1]}
        if [ "$JAVA_MAJOR_VERSION" -ge 17 ]; then
            echo -e "${C_GREEN}合格 (版本 ${JAVA_MAJOR_VERSION})${C_NC}"
        else
            echo -e "${C_RED}不合格 (發現版本 ${JAVA_MAJOR_VERSION}，但需要 >= 17)${C_NC}"
            CHECKS_FAILED=1
        fi
    else
        echo -e "${C_RED}不合格 (無法解析 Java 版本)${C_NC}"
        CHECKS_FAILED=1
    fi
else
    echo -e "${C_RED}不合格 (未找到 Java 指令)${C_NC}"
    CHECKS_FAILED=1
fi

# 詰問二：Maven 的熔爐是否備妥？(3.8+)
echo -n "  - 檢測 Maven 熔爐 (3.8+)... "
if command -v mvn &> /dev/null; then
    MVN_VERSION_STRING=$(mvn -version)
    if [[ "$MVN_VERSION_STRING" =~ Apache\ Maven\ ([0-9]+)\.([0-9]+) ]]; then
        MVN_MAJOR=${BASH_REMATCH[1]}
        MVN_MINOR=${BASH_REMATCH[2]}
        if [ "$MVN_MAJOR" -gt 3 ] || { [ "$MVN_MAJOR" -eq 3 ] && [ "$MVN_MINOR" -ge 8 ]; }; then
            echo -e "${C_GREEN}合格 (版本 ${MVN_MAJOR}.${MVN_MINOR})${C_NC}"
        else
            echo -e "${C_RED}不合格 (發現版本 ${MVN_MAJOR}.${MVN_MINOR}，但需要 >= 3.8)${C_NC}"
            CHECKS_FAILED=1
        fi
    else
        echo -e "${C_RED}不合格 (無法解析 Maven 版本)${C_NC}"
        CHECKS_FAILED=1
    fi
else
    echo -e "${C_RED}不合格 (未找到 Maven 指令)${C_NC}"
    CHECKS_FAILED=1
fi

# 詰問三：Docker 的港灣是否開放？
echo -n "  - 檢測 Docker 港灣... "
if command -v docker &> /dev/null && docker --version &> /dev/null; then
    echo -e "${C_GREEN}合格 (Docker 指令已就緒)${C_NC}"
else
    echo -e "${C_RED}不合格 (未找到 Docker 指令或其未在運行)${C_NC}"
    CHECKS_FAILED=1
fi

# --- 第二幕：建築師的指引 (反饋與授權) ---
# 若基石不穩，建築師將給予指引；若一切就緒，則請求您的授權。
echo ""
if [ "$CHECKS_FAILED" -ne 0 ]; then
    echo -e "${C_RED}--------------------[ 建築師的建議 ]--------------------${C_NC}"
    echo -e "${C_RED}您的基石尚需加固。請依循以下指引，完成準備後，${C_NC}"
    echo -e "${C_RED}再回來接受儀式的洗禮。${C_NC}"
    echo -e "${C_YELLOW}  - Java 引擎: 請確保 JDK 17 或更高版本已安裝，並配置於系統路徑中。${C_NC}"
    echo -e "${C_YELLOW}  - Maven 熔爐: https://maven.apache.org/install.html${C_NC}"
    echo -e "${C_YELLOW}  - Docker 港灣: https://www.docker.com/products/docker-desktop/${C_NC}"
    echo -e "${C_RED}-------------------------------------------------------${C_NC}"
    exit 1
fi

echo "第二幕：基石穩固。準備執行核心建造。"
echo ""
echo -e "${C_YELLOW}接下來，儀式將為您執行以下核心操作：${C_NC}"
echo -e "${C_YELLOW}  1. 鑄造『軟件之魂』：運行 'mvn clean install'，編譯並測試所有微服務。${C_NC}"
echo -e "${C_YELLOW}  2. 構築『設施之軀』：運行 'docker-compose build'，預先構建服務的容器鏡像。${C_NC}"
echo ""

read -p "您是否授權儀式繼續？(y/n): " consent

# --- 第三幕：宇宙的鑄成 (核心構建) ---
# 在您的授權下，開始執行真正的創世操作。
if [[ "$consent" =~ ^[Yy]$ ]]; then
    echo ""
    echo "第三幕：儀式核心啟動，宇宙正在鑄成..."
    cd "$(dirname "$0")/.."
    echo -e "${C_GREEN}--- 正在鑄造『軟件之魂』(mvn clean install) ---${C_NC}"
    mvn clean install
    
    if [ $? -ne 0 ]; then
        echo ""
        echo -e "${C_RED}鑄造失敗：Maven 熔爐回報錯誤。請檢閱上方日誌。${C_NC}"
        exit 1
    fi

    echo ""
    echo -e "${C_GREEN}--- 正在構築『設施之軀』(docker-compose build) ---${C_NC}"
    docker-compose build

    if [ $? -ne 0 ]; then
        echo ""
        echo -e "${C_RED}構築失敗：Docker 港灣回報錯誤。請檢閱上方日誌。${C_NC}"
        exit 1
    fi

    echo ""
    echo -e "${C_GREEN}===================================================${C_NC}"
    echo -e "${C_GREEN}      創世儀式完成。歡迎來到這個為您而設的宇宙。      ${C_NC}"
    echo -e "${C_GREEN}  您現在可以通過 'docker-compose up' 來喚醒所有服務。 ${C_NC}"
    echo -e "${C_GREEN}===================================================${C_NC}"
else
    echo ""
    echo "儀式已由您中止。期待您的再次到來。"
    exit 0
fi

exit 0