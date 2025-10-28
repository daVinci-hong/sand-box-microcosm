# 追蹤系統完整指南 (Complete Tracing System Guide)

本指南提供 Tempo 和 Jaeger 追蹤系統的完整管理方案，包括切換、配置和故障排除。

## 🚀 **快速開始 (Quick Start)**

### 預設配置 (Default Setup)
專案預設使用 **Tempo** 作為追蹤系統，clone 後可直接啟動：

```bash
# 直接啟動 (預設 Tempo)
docker compose up --build

# 驗證 Tempo 運行
curl http://localhost:3200/ready
```

### 切換到 Jaeger
```bash
# Windows
scripts\switch-compose.bat jaeger

# Linux/WSL  
./scripts/switch-compose.sh jaeger
```

### 切換回 Tempo
```bash
# Windows
scripts\switch-compose.bat tempo

# Linux/WSL
./scripts/switch-compose.sh tempo
```

## 🏗️ **架構設計**

### Docker Compose 文件結構
```
專案根目錄/
├── docker-compose.yml           # 主配置文件 (預設 Tempo)
├── docker-compose.base.yml      # 基礎配置 (共同服務)
├── docker-compose.tempo.yml     # Tempo 覆蓋配置
├── docker-compose.jaeger.yml    # Jaeger 覆蓋配置
└── scripts/
    ├── switch-compose.bat        # Windows 切換腳本
    └── switch-compose.sh         # Linux/WSL 切換腳本
```

### Grafana 配置結構
```
config/grafana/
├── templates/                    # 模板目錄 (Grafana 不會讀取)
│   ├── datasource-tempo.yml     # Tempo 數據源模板
│   └── datasource-jaeger.yml    # Jaeger 數據源模板
└── provisioning/
    └── datasources/
        └── datasource.yml       # 活動配置文件 (唯一)
```

## 🔄 **切換方案**

### 方案 1: 自動化腳本 (推薦)
```bash
# Windows
scripts\switch-compose.bat [tempo|jaeger]

# Linux/WSL
./scripts/switch-compose.sh [tempo|jaeger]
```

**腳本執行步驟**：
1. 停止當前服務
2. 清理舊容器 (避免端口衝突)
3. 更新 Grafana 數據源配置
4. 使用覆蓋文件啟動新配置
5. 重啟 Grafana 載入新數據源

### 方案 2: 手動覆蓋文件
```bash
# 停止服務
docker-compose down

# 清理舊容器
docker stop tempo-server jaeger-server 2>/dev/null || true
docker rm tempo-server jaeger-server 2>/dev/null || true

# 啟動 Tempo
docker-compose -f docker-compose.base.yml -f docker-compose.tempo.yml up -d

# 或啟動 Jaeger
docker-compose -f docker-compose.base.yml -f docker-compose.jaeger.yml up -d

# 更新數據源並重啟 Grafana
copy config\grafana\templates\datasource-tempo.yml config\grafana\provisioning\datasources\datasource.yml
docker restart grafana-dashboard
```

## 🧪 **測試驗證**

### 完整測試流程
1. **預設啟動測試**: `docker compose up --build` → Tempo 正常
2. **切換到 Jaeger**: `scripts\switch-compose.bat jaeger` → Jaeger 正常
3. **切換回 Tempo**: `scripts\switch-compose.bat tempo` → Tempo 正常  
4. **再次預設啟動**: `docker compose up --build` → Tempo 正常

### 驗證命令

#### Tempo 驗證
```bash
# 檢查 Tempo UI
curl http://localhost:3200/ready

# 檢查服務狀態
docker ps | grep tempo

# 檢查 Grafana 數據源
curl -u admin:admin http://localhost:3000/api/datasources
```

#### Jaeger 驗證
```bash
# 檢查 Jaeger API
curl http://localhost:16686/api/services

# 檢查服務狀態
docker ps | grep jaeger
```

#### 追蹤數據驗證
```bash
# 生成測試 token
TOKEN=$(curl -s http://localhost:8080/generate-token/subject/testuser/roles/admin,user)

# 觸發追蹤事件
curl -X POST -H "Authorization: Bearer $TOKEN" http://localhost:8080/trigger-beacon-event

# 檢查追蹤 ID
docker logs gateway-service --tail 5 | grep trace_id
```

## 🚨 **已知問題與解決方案**

### 問題 1: 端口衝突 (4317)
**原因**: Tempo 和 Jaeger 都使用 4317 端口
**解決**: 切換前必須完全停止並移除舊容器

```bash
# 查找佔用端口的容器
docker ps | grep 4317

# 強制清理
docker stop $(docker ps -q --filter "publish=4317") 2>/dev/null || true
docker rm $(docker ps -aq --filter "publish=4317") 2>/dev/null || true
```

### 問題 2: Grafana 數據源緩存
**原因**: Grafana 不會自動重載數據源配置
**解決**: 切換後必須重啟 Grafana 容器

```bash
docker restart grafana-dashboard
```

### 問題 3: 中文亂碼 (Windows)
**原因**: Windows 命令列編碼問題
**解決**: 腳本已加入 `chcp 65001` 設置 UTF-8 編碼

### 問題 4: 配置文件衝突
**原因**: Grafana 會讀取 `datasources/` 目錄下所有配置文件
**解決**: 模板存放在 `templates/` 目錄，只保留一個活動配置

## 🎯 **最佳實踐**

1. **預設使用 Tempo**: 新用戶 clone 後直接 `docker compose up --build`
2. **切換前停止服務**: 避免端口衝突
3. **使用自動化腳本**: 減少手動錯誤
4. **驗證切換結果**: 確認追蹤數據正常收集
5. **定期清理容器**: 避免資源浪費

## 🔧 **故障排除**

### 配置文件語法檢查
```bash
# 驗證 Docker Compose 配置
docker-compose config

# 驗證覆蓋文件配置
docker-compose -f docker-compose.base.yml -f docker-compose.tempo.yml config
```

### 服務健康檢查
```bash
# 檢查所有服務狀態
docker-compose ps

# 檢查特定服務日誌
docker logs [service-name] --tail 20
```

### 網路連接測試
```bash
# 測試服務間連接
docker exec gateway-service curl -f http://tempo:3200/ready
docker exec gateway-service curl -f http://jaeger:16686/api/services
```

## 📝 **配置文件說明**

### docker-compose.yml (主配置)
- 包含完整的 Tempo 配置
- 新用戶的預設啟動文件
- 與 `docker-compose.base.yml + docker-compose.tempo.yml` 功能相同

### docker-compose.base.yml (基礎配置)
- 包含所有共同服務
- 不包含追蹤系統特定配置
- 與覆蓋文件配合使用

### 覆蓋文件
- `docker-compose.tempo.yml`: Tempo 特定配置
- `docker-compose.jaeger.yml`: Jaeger 特定配置
- 定義 OTEL 端點和追蹤服務

## ⚠️ **重要注意事項**

1. **Docker Desktop 必須運行**: 所有操作前確認 Docker 服務正常
2. **端口不衝突**: 確保 3200 (Tempo) 和 16686 (Jaeger) 端口可用
3. **等待服務就緒**: 給服務足夠時間啟動 (通常 30-60 秒)
4. **Grafana 重啟**: 數據源更改後必須重啟 Grafana
5. **備份配置**: 重要更改前備份當前配置

## 🌟 **使用場景**

### 開發者 (首次使用)
```bash
git clone [repository]
cd [project]
docker compose up --build  # 預設 Tempo，無需額外配置
```

### 開發者 (需要切換)
```bash
scripts\switch-compose.bat jaeger  # 切換到 Jaeger
scripts\switch-compose.bat tempo   # 切換回 Tempo
```

### 運維人員 (生產環境)
```bash
# 使用特定配置部署
docker-compose -f docker-compose.base.yml -f docker-compose.tempo.yml up -d
```