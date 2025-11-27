# 🔨 Build and Install with Gradle

## Automated Build and Install Process

This guide will help you build and install the app using Gradle command line.

---

## Prerequisites

- Android SDK installed
- ANDROID_HOME environment variable set
- Device connected or emulator running

---

## Quick Build & Install Commands

### Option 1: Build and Install in One Command
```cmd
gradlew.bat installDebug
```

### Option 2: Step by Step

**1. Clean previous builds:**
```cmd
gradlew.bat clean
```

**2. Build debug APK:**
```cmd
gradlew.bat assembleDebug
```

**3. Install on connected device:**
```cmd
gradlew.bat installDebug
```

---

## Detailed Instructions

### Step 1: Open Command Prompt in Project Directory

```cmd
cd /d "D:\New folder\Background-player"
```

### Step 2: Make Gradle Wrapper Executable (First Time Only)

The project includes Gradle wrapper, no need to install Gradle separately.

### Step 3: Build the APK

```cmd
gradlew.bat assembleDebug
```

**Expected Output:**
```
BUILD SUCCESSFUL in 45s
52 actionable tasks: 52 executed
```

**APK Location:**
```
app\build\outputs\apk\debug\app-debug.apk
```

### Step 4: Install on Device

**Make sure device is connected:**
```cmd
adb devices
```

**Install APK:**
```cmd
gradlew.bat installDebug
```

**Or manually install:**
```cmd
adb install app\build\outputs\apk\debug\app-debug.apk
```

---

## All Available Gradle Tasks

### Build Tasks
```cmd
gradlew.bat assembleDebug          # Build debug APK
gradlew.bat assembleRelease        # Build release APK (needs signing)
gradlew.bat build                  # Build both debug and release
```

### Install Tasks
```cmd
gradlew.bat installDebug           # Build and install debug
gradlew.bat uninstallDebug         # Uninstall debug version
```

### Clean Tasks
```cmd
gradlew.bat clean                  # Delete build directory
gradlew.bat cleanBuildCache        # Clean build cache
```

### Other Useful Tasks
```cmd
gradlew.bat tasks                  # List all available tasks
gradlew.bat dependencies           # Show dependency tree
gradlew.bat androidDependencies    # Show Android dependencies
```

---

## Troubleshooting

### Error: "ANDROID_HOME is not set"

**Solution:**
```cmd
set ANDROID_HOME=C:\Users\YourUsername\AppData\Local\Android\Sdk
set PATH=%PATH%;%ANDROID_HOME%\platform-tools
```

### Error: "SDK location not found"

**Solution:** Create/update `local.properties`:
```properties
sdk.dir=C:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
```

### Error: "No connected devices"

**Solution:**
```cmd
# Check devices
adb devices

# If empty, check:
# 1. USB Debugging enabled on device
# 2. USB cable connected
# 3. "Allow USB debugging" popup accepted
```

### Error: "Build failed" - Dependency issues

**Solution:**
```cmd
gradlew.bat clean
gradlew.bat --refresh-dependencies build
```

### Error: "Out of memory"

**Solution:** Edit `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=512m
```

---

## Build Variants

### Debug Build (Development)
- Contains debug information
- Larger APK size
- Not obfuscated
- Debuggable
```cmd
gradlew.bat assembleDebug
```

### Release Build (Production)
- Optimized and obfuscated
- Smaller APK size
- Requires signing key
```cmd
gradlew.bat assembleRelease
```

---

## Performance Tips

### Speed Up Build
```cmd
# Use build cache
gradlew.bat --build-cache assembleDebug

# Use parallel execution
gradlew.bat --parallel assembleDebug

# Use daemon (default)
gradlew.bat --daemon assembleDebug
```

### Offline Build
```cmd
gradlew.bat --offline assembleDebug
```

---

## APK Information

After successful build, you'll find:

**Location:** `app\build\outputs\apk\debug\app-debug.apk`

**Size:** ~40-50 MB (includes 3 FLAC files)

**Installation:** Can be installed via:
- `gradlew.bat installDebug`
- `adb install app-debug.apk`
- Copy to device and install manually

---

## Complete Build & Install Script

Save this as `build_and_install.bat`:

```batch
@echo off
echo ============================================
echo Building Background Music Player
echo ============================================

cd /d "D:\New folder\Background-player"

echo.
echo Step 1: Cleaning previous builds...
call gradlew.bat clean

echo.
echo Step 2: Building debug APK...
call gradlew.bat assembleDebug

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Build FAILED!
    pause
    exit /b 1
)

echo.
echo ✅ Build SUCCESSFUL!
echo.
echo Step 3: Installing on device...
call gradlew.bat installDebug

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Installation FAILED!
    echo Make sure device is connected and USB debugging is enabled.
    pause
    exit /b 1
)

echo.
echo ============================================
echo ✅ Build and Installation SUCCESSFUL!
echo ============================================
echo.
echo APK Location: app\build\outputs\apk\debug\app-debug.apk
echo.
echo You can now launch the app on your device!
pause
```

**Usage:**
```cmd
build_and_install.bat
```

---

## Verification Steps

After installation:

1. **Check app is installed:**
   ```cmd
   adb shell pm list packages | findstr backgroundmusicplayer
   ```

2. **Launch app:**
   ```cmd
   adb shell am start -n com.example.backgroundmusicplayer/.MainActivity
   ```

3. **View logs:**
   ```cmd
   adb logcat | findstr "MusicPlayerService"
   ```

---

## Success Indicators

✅ **Build Successful:**
```
BUILD SUCCESSFUL in Xs
```

✅ **Installation Successful:**
```
Installing APK 'app-debug.apk' on 'Device Name'
Installed on 1 device.
```

✅ **App Appears on Device:**
- Look for "Background Music Player" icon in app drawer

---

## Next Steps After Installation

1. Launch the app
2. Grant audio permission when prompted
3. Select a demo track
4. Verify music plays
5. Test background playback (HOME button)
6. Test notification controls

---

**Ready to build?** Run: `gradlew.bat installDebug`

