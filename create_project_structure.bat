@echo off
setlocal
chcp 65001 > nul

:: =================================================================
:: Project Da Vinci - Structure Initialization Script v2.0 (Corrected)
:: File: create_project_structure_v2.bat
:: Purpose: Generates the directory structure STRICTLY ADHERING to
::          General Order 007. All source code is now correctly
::          placed under the top-level /src directory.
:: =================================================================

set "ROOT_DIR=sand-box-microcosm"

:: --- 4. Engineer's Forge (Source Code under /src) ---
echo [ENG]   Igniting the Engineer's Forge within the /src jurisdiction...
md src

:: Module: beacon-service
echo [ENG]   ...Forging 'beacon-service' module inside /src...
md src\beacon-service\src\main\java\com\projectdavinci\beaconservice
md src\beacon-service\src\main\resources
md src\beacon-service\src\test\java\com\projectdavinci\beaconservice
md src\beacon-service\src\test\resources
type nul > src\beacon-service\pom.xml
type nul > src\beacon-service\src\main\java\com\projectdavinci\beaconservice\BeaconServiceApplication.java
type nul > src\beacon-service\src\main\resources\application.properties

:: Module: gateway-service
echo [ENG]   ...Forging 'gateway-service' module inside /src...
md src\gateway-service\src\main\java\com\projectdavinci\gatewayservice
md src\gateway-service\src\main\resources
md src\gateway-service\src\test\java\com\projectdavinci\gatewayservice
md src\gateway-service\src\test\resources
type nul > src\gateway-service\pom.xml
type nul > src\gateway-service\src\main\java\com\projectdavinci\gatewayservice\GatewayServiceApplication.java
type nul > src\gateway-service\src\main\resources\application.properties


echo.
echo [SUCCESS] The "sand-box-microcosm" has been successfully materialized.
echo [INFO]    All directory structures and placeholder files are in place.

endlocal
goto :eof