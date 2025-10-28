#!/bin/bash

# =================================================================
#           Docker Compose 覆蓋文件切換器 (Linux/WSL 版本)
#
# 用法: ./switch-compose.sh [tempo|jaeger]
# 使用 Docker Compose 覆蓋文件進行切換，更清潔的方案
# =================================================================

# 顏色定義
C_RED='\033[0;31m'
C_GREEN='\033[0;32m'
C_YELLOW='\033[0;33m'
C_BLUE='\033[0;34m'
C_NC='\033[0m' # No Color

if [ "$1" = "" ]; then
    echo -e "${C_RED}用法: ./switch-compose.sh [tempo|jaeger]${C_NC}"
    echo -e "${C_YELLOW}範例: ./switch-compose.sh tempo${C_NC}"
    exit 1
fi

target_system=$1
cd "$(dirname "$0")/.."

echo -e "${C_BLUE}使用 Docker Compose 覆蓋文件切換到 $target_system...${C_NC}"

# 步驟 1: 停止服務
echo -e "${C_YELLOW}1. 停止當前服務...${C_NC}"
docker-compose down >/dev/null 2>&1

# 步驟 2: 清理舊容器
echo -e "${C_YELLOW}2. 清理舊容器...${C_NC}"
if [ "$target_system" = "tempo" ]; then
    docker stop jaeger-server >/dev/null 2>&1
    docker rm jaeger-server >/dev/null 2>&1
else
    docker stop tempo-server >/dev/null 2>&1
    docker rm tempo-server >/dev/null 2>&1
fi

# 步驟 3: 更新數據源配置
echo -e "${C_YELLOW}3. 更新 Grafana 數據源配置...${C_NC}"
rm -f config/grafana/provisioning/datasources/datasource.yml
cp config/grafana/templates/datasource-$target_system.yml config/grafana/provisioning/datasources/datasource.yml

# 步驟 4: 使用覆蓋文件啟動服務
echo -e "${C_YELLOW}4. 使用 $target_system 配置啟動服務...${C_NC}"
docker-compose -f docker-compose.base.yml -f docker-compose.$target_system.yml up -d >/dev/null

# 步驟 5: 等待並重啟 Grafana
echo -e "${C_YELLOW}5. 等待服務就緒...${C_NC}"
sleep 30
echo -e "${C_YELLOW}6. 重啟 Grafana 載入新數據源...${C_NC}"
docker restart grafana-dashboard >/dev/null
sleep 20

echo ""
echo -e "${C_GREEN}✅ 成功切換到 $target_system！${C_NC}"
echo ""
echo -e "${C_BLUE}使用的配置文件:${C_NC}"
echo -e "  - docker-compose.base.yml (基礎配置)"
echo -e "  - docker-compose.$target_system.yml (追蹤系統配置)"
echo ""
if [ "$target_system" = "tempo" ]; then
    echo -e "${C_GREEN}Tempo UI: http://localhost:3200${C_NC}"
else
    echo -e "${C_GREEN}Jaeger UI: http://localhost:16686${C_NC}"
fi
echo -e "${C_GREEN}Grafana: http://localhost:3000${C_NC}"
echo ""
echo -e "${C_YELLOW}提示: 下次可以直接使用以下命令啟動:${C_NC}"
echo -e "docker-compose -f docker-compose.base.yml -f docker-compose.$target_system.yml up -d"