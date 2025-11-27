@echo off
echo ========================================
echo Building Background Music Player
echo ========================================
echo.

cd /d "%~dp0"

echo [1/3] Cleaning previous build...
call gradlew.bat clean
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Clean failed!
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo [2/3] Building Debug APK...
call gradlew.bat assembleDebug --stacktrace
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build failed!
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo [3/3] Checking output...
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo.
    echo ========================================
    echo BUILD SUCCESSFUL!
    echo ========================================
    echo APK Location: app\build\outputs\apk\debug\app-debug.apk
    echo.
    dir "app\build\outputs\apk\debug\app-debug.apk"
) else (
    echo WARNING: APK file not found at expected location
)

echo.
echo Build complete!
pause

