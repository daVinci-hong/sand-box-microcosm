@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM =================================================================
REM           Docker Compose 覆蓋文件切換器
REM
REM 用法: switch-compose.bat [tempo|jaeger]
REM 使用 Docker Compose 覆蓋文件進行切換，更清潔的方案
REM =================================================================

if "%1"=="" (
    echo Usage: switch-compose.bat [tempo^|jaeger]
    echo Example: switch-compose.bat tempo
    exit /b 1
)

set target_system=%1
cd /d "%~dp0\.."

echo Switching to %target_system% using Docker Compose override files...

REM 步驟 1: 停止服務
echo 1. Stopping current services...
docker-compose down >nul 2>&1

REM 步驟 2: 清理舊容器
echo 2. Cleaning old containers...
if "%target_system%"=="tempo" (
    docker stop jaeger-server >nul 2>&1
    docker rm jaeger-server >nul 2>&1
) else (
    docker stop tempo-server >nul 2>&1
    docker rm tempo-server >nul 2>&1
)

REM 步驟 3: 更新數據源配置
echo 3. Updating Grafana datasource configuration...
del config\grafana\provisioning\datasources\datasource.yml >nul 2>&1
copy config\grafana\templates\datasource-%target_system%.yml config\grafana\provisioning\datasources\datasource.yml >nul

REM 步驟 4: 使用覆蓋文件啟動服務
echo 4. Starting %target_system% configuration...
docker-compose -f docker-compose.base.yml -f docker-compose.%target_system%.yml up -d >nul

REM 步驟 5: 等待服務就緒並驗證
echo 5. Waiting for services to be ready...
timeout /t 30 /nobreak >nul

REM 步驟 6: 驗證追蹤系統啟動狀態
echo 6. Verifying %target_system% service status...
if "%target_system%"=="tempo" (
    REM Tempo 需要更長時間初始化 ingester
    echo    Waiting for Tempo ingester to be ready...
    :tempo_wait_loop
    curl -s http://localhost:3200/ready >nul 2>&1
    if errorlevel 1 (
        timeout /t 10 /nobreak >nul
        goto tempo_wait_loop
    )
    echo    ^✓ Tempo is ready
) else (
    REM Jaeger 啟動較快
    timeout /t 10 /nobreak >nul
    echo    ^✓ Jaeger is ready
)

REM 步驟 7: 重啟 Grafana 載入新數據源
echo 7. Restarting Grafana to load new datasource...
docker restart grafana-dashboard >nul
timeout /t 20 /nobreak >nul

echo.
echo ^✅ Successfully switched to %target_system%!
echo.
echo Configuration files used:
echo   - docker-compose.base.yml ^(base configuration^)
echo   - docker-compose.%target_system%.yml ^(tracing system configuration^)
echo.
if "%target_system%"=="tempo" (
    echo Tempo UI: http://localhost:3200
) else (
    echo Jaeger UI: http://localhost:16686
)
echo Grafana: http://localhost:3000
echo.
echo Tip: Next time you can directly use this command:
echo docker-compose -f docker-compose.base.yml -f docker-compose.%target_system%.yml up -d