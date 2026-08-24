@echo off
echo Building Nova Assistant...
cd /d "%~dp0"
call gradlew.bat assembleDebug
if %errorlevel% equ 0 (
    echo Build successful!
    echo APK located at: app\build\outputs\apk\debug\app-debug.apk
) else (
    echo Build failed!
)
pause
