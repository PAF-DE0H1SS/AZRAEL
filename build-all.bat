@echo off
rem ============================================================
rem AZRAEL: автосборка под Windows 10/11 (MSI-инсталлятор).
rem
rem  Запуск:  build-all.bat [clean]
rem    (опциональный аргумент clean - очистка build перед сборкой)
rem
rem  Требования:
rem   - JDK 17+ (JAVA_HOME) или Android Studio
rem   - facewo: при первом запуске Compose Desktop скачает WiX сам
rem ============================================================
setlocal
cd /d "%~dp0"

if "%JAVA_HOME%"=="" (
    echo [AZRAEL] JAVA_HOME не задан. Укажите путь к JDK 17+:
    echo          set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
    exit /b 1
)

if not exist gradlew.bat (
    echo [AZRAEL] gradlew.bat не найден в %cd%
    exit /b 1
)

echo [AZRAEL] === Сборка Windows MSI %date% %time% ===

if "%1"=="clean" (
    call gradlew.bat clean || exit /b 1
)

call gradlew.bat :desktopApp:packageMsi || exit /b 1

set DIST=build\dist
if not exist %DIST% mkdir %DIST%
for /r desktopApp\build\compose\binaries\main\msi %%f in (*.msi) do (
    copy /Y "%%f" "%DIST%\AZRAEL-win10-11.msi"
)

echo [AZRAEL] Windows MSI собран. Артефакт:
dir /b %DIST%
endlocal