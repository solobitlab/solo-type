@echo off
setlocal

cd /d "%~dp0\..\.."

echo Building SoloType Windows Setup Wizard (.exe and .msi)...
call gradlew.bat packageExe packageMsi --no-daemon
if errorlevel 1 (
    echo Failed to build Windows installer. Make sure JDK 17+ and WiX Toolset 3.x are installed.
    exit /b 1
)

if not exist dist mkdir dist
if exist build\compose\binaries\main\exe\*.exe copy /Y build\compose\binaries\main\exe\*.exe dist\
if exist build\compose\binaries\main\msi\*.msi copy /Y build\compose\binaries\main\msi\*.msi dist\

echo.
echo Windows Setup installers copied to dist\:
dir /B dist\*.exe dist\*.msi 2>nul
endlocal
