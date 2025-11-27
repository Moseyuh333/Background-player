@echo off
echo ============================================
echo Background Music Player - Build and Install
echo ============================================
echo.

cd /d "D:\New folder\Background-player"

echo Checking Android SDK...
if not defined ANDROID_HOME (
    echo Warning: ANDROID_HOME not set. Trying to use local.properties...
)

echo.
echo [Step 1/4] Cleaning previous builds...
call gradlew.bat clean
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Clean failed!
    pause
    exit /b 1
)
echo ✅ Clean successful!

echo.
echo [Step 2/4] Building debug APK...
echo This may take 2-5 minutes on first build...
call gradlew.bat assembleDebug
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ BUILD FAILED!
    echo.
    echo Common issues:
    echo 1. Check internet connection (needed for downloading dependencies)
    echo 2. Verify Android SDK is installed
    echo 3. Update local.properties with SDK path
    echo.
    pause
    exit /b 1
)
echo ✅ Build successful!

echo.
echo [Step 3/4] Checking connected devices...
adb devices
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ⚠️ ADB not found in PATH
    echo Make sure Android SDK platform-tools are in your PATH
    echo.
    pause
    exit /b 1
)

echo.
echo [Step 4/4] Installing on device...
call gradlew.bat installDebug
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ INSTALLATION FAILED!
    echo.
    echo Make sure:
    echo 1. Device is connected via USB
    echo 2. USB Debugging is enabled
    echo 3. "Allow USB debugging" popup is accepted on device
    echo.
    echo You can manually install:
    echo adb install app\build\outputs\apk\debug\app-debug.apk
    echo.
    pause
    exit /b 1
)

echo.
echo ============================================
echo ✅ BUILD AND INSTALLATION SUCCESSFUL!
echo ============================================
echo.
echo APK Location: app\build\outputs\apk\debug\app-debug.apk
echo Package: com.example.backgroundmusicplayer
echo.
echo The app is now installed on your device!
echo Look for "Background Music Player" in the app drawer.
echo.
echo To launch from command line:
echo adb shell am start -n com.example.backgroundmusicplayer/.MainActivity
echo.
pause

