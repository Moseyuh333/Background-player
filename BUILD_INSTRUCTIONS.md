# Background Music Player - Build & Setup Instructions

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Project Structure](#project-structure)
3. [Adding FLAC Files](#adding-flac-files)
4. [Building the Project](#building-the-project)
5. [Running the App](#running-the-app)
6. [Testing Background Playback](#testing-background-playback)
7. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Required Software
- **Android Studio**: Hedgehog (2023.1.1) or newer
- **JDK**: Version 8 or 11 (included with Android Studio)
- **Android SDK**: API 26+ (Android 8.0) or higher
- **Gradle**: 8.0+ (automatically managed by Android Studio)

### Knowledge Requirements
- Basic understanding of Android development
- Familiarity with Kotlin programming language
- Understanding of Android Services and Notifications

---

## Project Structure

```
Background-player/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/backgroundmusicplayer/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── MainViewModel.kt
│   │   │   │   ├── model/
│   │   │   │   │   └── PlaybackState.kt
│   │   │   │   └── service/
│   │   │   │       ├── MusicPlayerService.kt
│   │   │   │       └── MusicNotificationManager.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   └── activity_main.xml
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── themes.xml
│   │   │   │   │   └── dimens.xml
│   │   │   │   ├── raw/              # FLAC files go here
│   │   │   │   │   ├── sample1.flac
│   │   │   │   │   ├── sample2.flac
│   │   │   │   │   └── sample3.flac
│   │   │   │   └── xml/
│   │   │   │       ├── backup_rules.xml
│   │   │   │       └── data_extraction_rules.xml
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── ARCHITECTURE.md
└── README.md
```

---

## Adding FLAC Files

### Method 1: Using the Existing FLAC File (Quickest)

The project root contains a FLAC file: `1_5131934797106512485.flac`

**On Windows Command Prompt:**
```cmd
cd /d "D:\New folder\Background-player"
mkdir app\src\main\res\raw
copy 1_5131934797106512485.flac app\src\main\res\raw\sample1.flac
copy 1_5131934797106512485.flac app\src\main\res\raw\sample2.flac
copy 1_5131934797106512485.flac app\src\main\res\raw\sample3.flac
```

### Method 2: Using Your Own FLAC Files

1. Locate your FLAC audio files
2. Rename them to: `sample1.flac`, `sample2.flac`, `sample3.flac`
3. Copy to: `app/src/main/res/raw/`
4. File names must be lowercase with no special characters

### Method 3: Converting Audio to FLAC

**Using FFmpeg:**
```cmd
ffmpeg -i input.mp3 -c:a flac app\src\main\res\raw\sample1.flac
```

**Using Online Converters:**
- CloudConvert: https://cloudconvert.com/mp3-to-flac
- FreeConvert: https://www.freeconvert.com/mp3-to-flac

### Method 4: Download Free FLAC Samples

Download high-quality FLAC files from:
- **2L Test Bench**: https://www.2l.no/hires/
- **Internet Archive**: https://archive.org/details/audio
- **Musopen** (Classical music): https://musopen.org/

---

## Building the Project

### Step 1: Open Project in Android Studio

1. Launch **Android Studio**
2. Click **File → Open**
3. Navigate to `D:\New folder\Background-player`
4. Click **OK**
5. Wait for Gradle sync to complete (may take 2-5 minutes first time)

### Step 2: Verify Gradle Sync

Check the **Build** tab at the bottom of Android Studio:
- ✅ "BUILD SUCCESSFUL" = Ready to run
- ❌ Errors? See [Troubleshooting](#troubleshooting)

### Step 3: Ensure FLAC Files Are Present

1. In Android Studio, navigate to: `app → src → main → res → raw`
2. Verify three files exist:
   - `sample1.flac`
   - `sample2.flac`
   - `sample3.flac`
3. If missing, follow [Adding FLAC Files](#adding-flac-files)

---

## Running the App

### On Physical Device (Recommended)

1. **Enable Developer Options** on your Android device:
   - Go to **Settings → About Phone**
   - Tap **Build Number** 7 times
   - Go back to **Settings → Developer Options**
   - Enable **USB Debugging**

2. **Connect Device** via USB cable

3. **Run App in Android Studio**:
   - Click the **Run** button (green play icon) or press **Shift + F10**
   - Select your device from the list
   - Click **OK**

### On Emulator

1. **Create AVD** (Android Virtual Device):
   - Click **Device Manager** in Android Studio
   - Click **Create Device**
   - Select **Phone → Pixel 6**
   - Select **API 33 or 34** (Download if needed)
   - Click **Finish**

2. **Launch Emulator**:
   - Select the AVD in Device Manager
   - Click the **Play** button

3. **Run App**:
   - Click **Run** button in Android Studio
   - Select the emulator

---

## Testing Background Playback

### Test 1: Basic Playback

1. **Launch the app**
2. **Grant permission** when prompted (Read Media Audio)
3. **Select a track** (Demo Track 1, 2, or 3)
4. **Verify playback starts**:
   - Status shows "Playing"
   - Track name is displayed
   - Seek bar moves
   - Notification appears in status bar

### Test 2: App Backgrounded (HOME Button)

1. **Start playing a track**
2. **Press HOME button** on device
3. **Verify music continues playing**
4. **Pull down notification shade**
5. **Verify notification shows** with Play/Pause/Stop controls
6. **Test notification controls**:
   - Tap **Pause** → Music pauses
   - Tap **Play** → Music resumes
   - Tap **Stop** → Music stops and notification disappears

### Test 3: Activity Destroyed (BACK Button)

1. **Start playing a track**
2. **Press BACK button** to close app
3. **Verify music continues playing**
4. **Use notification to control playback**
5. **Reopen app** from launcher
6. **Verify UI reflects current playback state**

### Test 4: Screen Rotation

1. **Start playing a track**
2. **Rotate device** (or press Ctrl+F11 in emulator)
3. **Verify**:
   - Music continues without interruption
   - UI correctly shows current state
   - Seek position is maintained

### Test 5: App Swiped from Recent Apps

⚠️ **Important**: On Android 8.0+, this will typically kill the service.

1. **Start playing a track**
2. **Press Recent Apps button** (square or gesture)
3. **Swipe app away**
4. **Result**: Music stops (expected behavior on modern Android)
5. **Why?**: Android aggressively kills apps to save battery

### Test 6: Play/Pause/Stop Controls

1. **Start playing Track 1**
2. **Press Pause** → Verify music pauses
3. **Press Play** → Verify music resumes
4. **Press Stop** → Verify music stops and service terminates
5. **Select Track 2** → Verify new track starts

### Test 7: Seek Bar Control

1. **Start playing a track**
2. **Drag seek bar** to different position
3. **Verify playback jumps** to selected position
4. **Let track play** and verify seek bar updates automatically

---

## How Music Continues in Background

### Foreground Service Mechanism

The app uses Android's **Foreground Service** pattern:

1. **Service Started**: When user selects a track, `MusicPlayerService` starts
2. **startForeground() Called**: Service promotes itself to foreground with persistent notification
3. **High Priority**: Android treats foreground services as high priority
4. **Survives Activity Death**: Service continues even when `MainActivity` is destroyed
5. **Notification Required**: Persistent notification keeps user informed and allows control

### Lifecycle Flow

```
User selects track
    ↓
MainActivity.startForegroundService()
    ↓
MusicPlayerService.onStartCommand()
    ↓
ExoPlayer initialized and plays track
    ↓
startForeground(notification) called
    ↓
Notification appears
    ↓
User presses HOME/BACK
    ↓
MainActivity destroyed
    ↓
Service continues running
    ↓
Music keeps playing
```

### Why START_STICKY?

```kotlin
return START_STICKY
```

- If Android kills the service due to low memory, it will **restart** the service
- Intent extras will be **null** on restart (service must save state)
- Alternative: `START_NOT_STICKY` = don't restart if killed

### Limitations on Modern Android

**Android 8.0+ (API 26+) Restrictions:**
- App swiped from Recent Apps → Service is killed
- Battery optimization → May kill long-running services
- Doze mode → Service may be paused

**Workarounds:**
- Request "Don't optimize" in battery settings (not recommended)
- Save playback state to SharedPreferences
- Resume from saved state on service restart

---

## Troubleshooting

### Issue: Gradle Sync Failed

**Error**: "Could not resolve dependencies"

**Solution**:
1. Check internet connection
2. In Android Studio: **File → Invalidate Caches → Invalidate and Restart**
3. Delete `.gradle` folder in project root and sync again

### Issue: FLAC Files Not Found

**Error**: "Resource not found: raw/sample1"

**Solution**:
1. Verify files exist in `app/src/main/res/raw/`
2. File names must be exactly: `sample1.flac`, `sample2.flac`, `sample3.flac`
3. File names must be lowercase with no spaces
4. After adding files, do: **Build → Clean Project** then **Build → Rebuild Project**

### Issue: Permission Denied

**Error**: App crashes when selecting track

**Solution**:
1. Ensure you granted permission when prompted
2. Manually grant permission:
   - **Settings → Apps → Background Music Player → Permissions → Files and Media → Allow**
3. For Android 13+: Grant "Music and Audio" permission

### Issue: No Sound

**Problem**: Track appears to play but no audio

**Solution**:
1. Check device volume (media volume, not ringer)
2. Ensure FLAC files are valid audio files
3. Test FLAC files in another player (VLC, etc.)
4. Check Logcat for ExoPlayer errors:
   - **View → Tool Windows → Logcat**
   - Filter by "MusicPlayerService" or "ExoPlayer"

### Issue: Service Stops Immediately

**Problem**: Music stops as soon as app is closed

**Solution**:
1. Check battery optimization settings
2. Ensure `startForeground()` is called
3. Verify notification appears before closing app
4. Check Logcat for service lifecycle logs

### Issue: Notification Not Showing

**Problem**: No notification appears

**Solution**:
1. Check notification permissions (Android 13+ requires permission)
2. Verify notification channel is created (API 26+)
3. Check notification settings: **Settings → Apps → Background Music Player → Notifications → Enable**

### Issue: Build Error - UnstableApi

**Error**: "This declaration needs opt-in"

**Solution**:
Already handled in code with `@UnstableApi` annotation. If issues persist:
```kotlin
// Add to build.gradle.kts
android {
    kotlinOptions {
        freeCompilerArgs += "-opt-in=androidx.media3.common.util.UnstableApi"
    }
}
```

---

## Verifying Background Playback

### Checklist

✅ Track starts playing when selected  
✅ Notification appears with track name and controls  
✅ Status shows "Playing" in app UI  
✅ Music continues when HOME button pressed  
✅ Music continues when BACK button pressed (activity destroyed)  
✅ Notification controls work (Play/Pause/Stop)  
✅ Seek bar works and updates during playback  
✅ Screen rotation doesn't interrupt playback  
✅ Reopening app shows correct playback state  
✅ Stop button stops music and removes notification  

---

## Key Files Reference

### Core Service Logic
- **`MusicPlayerService.kt`**: Foreground service, ExoPlayer management, notification control
- **`MusicNotificationManager.kt`**: Notification channel and notification building

### UI & Activity
- **`MainActivity.kt`**: UI logic, service binding, permission handling
- **`MainViewModel.kt`**: State management across configuration changes
- **`activity_main.xml`**: UI layout

### Models & Data
- **`PlaybackState.kt`**: Sealed class for type-safe state management

### Configuration
- **`AndroidManifest.xml`**: Service declaration, permissions, foreground service type
- **`build.gradle.kts`**: ExoPlayer (Media3) dependencies

---

## Additional Resources

### Documentation
- **ExoPlayer Guide**: https://developer.android.com/guide/topics/media/exoplayer
- **Foreground Services**: https://developer.android.com/guide/components/foreground-services
- **Media Playback**: https://developer.android.com/guide/topics/media/mediaplayer

### Learning Materials
- **Android Services Codelab**: https://developer.android.com/codelabs/android-training-services
- **Media3 Documentation**: https://developer.android.com/jetpack/androidx/releases/media3

---

## Success Criteria

Your app is working correctly if:

1. ✅ Music plays from FLAC files
2. ✅ Foreground notification appears with controls
3. ✅ Music continues when app is backgrounded
4. ✅ Music continues when activity is destroyed (BACK pressed)
5. ✅ Notification controls work (Play/Pause/Stop)
6. ✅ UI correctly reflects playback state
7. ✅ No memory leaks (ExoPlayer properly released)
8. ✅ App survives screen rotation without issues

---

## Next Steps / Enhancements

For production apps, consider adding:

1. **Playlist Support**: Multiple tracks in a queue
2. **Media Session Integration**: Lock screen controls, Android Auto
3. **Progress Persistence**: Save playback position to SharedPreferences
4. **Network Streaming**: Load tracks from URLs
5. **Storage Access Framework**: Let users pick any audio file
6. **MediaStore Integration**: Browse device music library
7. **Equalizer**: Audio effects support
8. **Bluetooth Controls**: AVRCP support
9. **Widget**: Home screen playback control widget
10. **Background Download**: Pre-cache tracks for offline playback

---

## Support

If you encounter issues not covered here:

1. Check **Logcat** for error messages
2. Review **ARCHITECTURE.md** for design details
3. Verify all dependencies are correctly synced
4. Ensure minimum SDK version is met (API 26+)
5. Test on physical device (emulator may have audio issues)

---

**Project Ready!** 🎉

You now have a fully functional Android music player with background playback support using Foreground Service and ExoPlayer for FLAC audio.

