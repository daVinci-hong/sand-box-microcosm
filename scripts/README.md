# Scripts 目錄說明

本目錄包含專案的所有自動化腳本。

## 📁 **目錄結構**

```
scripts/
├── README.md                 # 本說明文件
├── genesis.sh                # 專案初始化腳本
├── switch-compose.bat        # Windows 追蹤系統切換腳本
├── switch-compose.sh         # Linux/WSL 追蹤系統切換腳本
└── load-testing/             # 負載測試腳本目錄
```

## 🚀 **腳本說明**

### genesis.sh
**用途**: 專案初始化和環境搭建
**平台**: Linux/WSL
**使用方法**:
```bash
chmod +x scripts/genesis.sh
./scripts/genesis.sh
```

### switch-compose.bat
**用途**: Windows 平台的追蹤系統切換
**平台**: Windows
**使用方法**:
```cmd
scripts\switch-compose.bat tempo
scripts\switch-compose.bat jaeger
```

### switch-compose.sh
**用途**: Linux/WSL 平台的追蹤系統切換
**平台**: Linux/WSL
**使用方法**:
```bash
chmod +x scripts/switch-compose.sh
./scripts/switch-compose.sh tempo
./scripts/switch-compose.sh jaeger
```

## 🔄 **追蹤系統切換**

### 支援的追蹤系統
- **Tempo**: Grafana 的分散式追蹤後端
- **Jaeger**: Uber 開源的分散式追蹤系統

### 切換功能
- ✅ 自動停止舊服務
- ✅ 清理端口衝突
- ✅ 更新 Grafana 數據源
- ✅ 啟動新的追蹤系統
- ✅ 重啟 Grafana 載入配置

### 使用的技術
- **Docker Compose 覆蓋文件**: 清潔的配置管理
- **自動化腳本**: 減少手動操作錯誤
- **跨平台支援**: Windows 和 Linux/WSL

## 📋 **腳本清理歷史**

### 已移除的腳本 (2025-10-26)
以下腳本因功能重複或過時而被移除：

- ❌ `quick-switch.bat` - 功能與 switch-compose.bat 重複
- ❌ `switch-tracing.bat` - 使用動態修改方案，已棄用
- ❌ `switch-tracing.sh` - 舊版本腳本
- ❌ `switch-tracing-improved.sh` - 功能重複

### 清理原因
1. **減少混淆**: 太多相似功能的腳本會讓用戶困惑
2. **維護簡化**: 減少需要維護的腳本數量
3. **標準化**: 統一使用覆蓋文件方案
4. **最佳實踐**: 遵循 Docker Compose 最佳實踐

## 🎯 **推薦使用方式**

### 新用戶
1. 使用 `genesis.sh` 初始化環境
2. 使用 `switch-compose.*` 腳本切換追蹤系統

### 日常開發
```bash
# 切換到 Tempo 進行開發
./scripts/switch-compose.sh tempo

# 切換到 Jaeger 進行測試
./scripts/switch-compose.sh jaeger
```

### 故障排除
如果腳本執行失敗，請檢查：
1. Docker 是否正在運行
2. 端口是否被其他程序佔用
3. 配置文件是否存在
4. 腳本是否有執行權限 (Linux/WSL)

## 📚 **相關文檔**

- [追蹤系統切換指南](../docs/TRACING_SWITCH_GUIDE.md)
- [Docker Compose 管理指南](../docs/DOCKER_COMPOSE_MANAGEMENT.md)
- [專案主要文檔](../README.md)