# Android Background Music Player

A fully functional Android music player app that continues playing FLAC audio files even after the user leaves or closes the app, using a **Foreground Service** pattern with **ExoPlayer (Media3)**.

![Android](https://img.shields.io/badge/Android-8.0%2B-green.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)
![ExoPlayer](https://img.shields.io/badge/ExoPlayer-Media3%201.2.1-orange.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

## 🎵 Features

- ✅ **Background Playback**: Music continues when app is closed or backgrounded
- ✅ **Foreground Service**: Persistent notification with media controls
- ✅ **FLAC Support**: High-quality lossless audio playback via ExoPlayer
- ✅ **Media Controls**: Play, Pause, Stop, and Seek functionality
- ✅ **Notification Controls**: Control playback from notification shade
- ✅ **State Preservation**: Survives screen rotation and activity lifecycle changes
- ✅ **Modern Architecture**: ViewModel, LiveData/StateFlow, Service Binding
- ✅ **Runtime Permissions**: Proper handling for Android 6.0 - 13+
- ✅ **Material Design**: Clean, modern UI with Material Components

## 📱 Screenshots

The app provides:
- **Track Selection**: Choose from 3 demo FLAC tracks
- **Playback Controls**: Play/Pause/Stop buttons
- **Progress Tracking**: Seek bar with time display
- **Status Display**: Real-time playback state (Playing/Paused/Stopped)
- **Persistent Notification**: Always visible when music is playing

## 🏗️ Architecture

This app follows Android best practices with a clean architecture:

```
MainActivity (UI Layer)
    ↕ (Binding)
MusicPlayerService (Service Layer)
    ↕ (Controls)
ExoPlayer (Media Layer)
    ↓
FLAC Audio Files
```

### Key Components

1. **MusicPlayerService**: Foreground service managing ExoPlayer and playback lifecycle
2. **MusicNotificationManager**: Handles notification channel and media controls
3. **MainActivity**: UI with service binding for real-time state updates
4. **MainViewModel**: State management across configuration changes
5. **PlaybackState**: Sealed class for type-safe state representation

## 🚀 Quick Start

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK** 8 or 11
- **Android SDK** API 26+ (Android 8.0 Oreo)
- **Physical device or emulator** for testing

### Installation

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd Background-player
   ```

2. **Open in Android Studio**:
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the project folder
   - Wait for Gradle sync to complete

3. **Verify FLAC files** (already included):
   - Check `app/src/main/res/raw/` directory
   - Should contain: `sample1.flac`, `sample2.flac`, `sample3.flac`

4. **Build and Run**:
   - Connect your Android device or start an emulator
   - Click the **Run** button (or press `Shift + F10`)
   - Grant permission when prompted
   - Select a track and enjoy!

## 📖 Documentation

- **[BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md)**: Detailed build and testing guide
- **[ARCHITECTURE.md](ARCHITECTURE.md)**: In-depth architecture and design decisions

## 🎯 How It Works

### Foreground Service Pattern

The app uses Android's **Foreground Service** to ensure music playback continues:

1. User selects a track
2. `MainActivity` starts `MusicPlayerService` via `startForegroundService()`
3. Service calls `startForeground()` with a persistent notification
4. ExoPlayer loads and plays the FLAC file
5. User can close the app - service continues running
6. Notification provides Play/Pause/Stop controls
7. Service stops when user presses Stop or music ends

### Why ExoPlayer?

**ExoPlayer** (now AndroidX Media3) is chosen over MediaPlayer because:

- ✅ **Native FLAC Support**: Built-in decoder, no device dependency
- ✅ **Better Error Handling**: Detailed callbacks and recovery
- ✅ **Extensible**: Modular architecture for custom features
- ✅ **Active Development**: Google's official media library
- ✅ **Advanced Features**: Gapless playback, adaptive streaming

## 🧪 Testing Background Playback

### Test Scenario 1: Home Button
1. Start playing a track
2. Press **HOME** button
3. ✅ Music continues playing
4. ✅ Notification remains visible
5. Pull down notification shade and test controls

### Test Scenario 2: Back Button
1. Start playing a track
2. Press **BACK** button (destroys activity)
3. ✅ Music continues playing
4. Reopen app from launcher
5. ✅ UI reflects current playback state

### Test Scenario 3: Screen Rotation
1. Start playing a track
2. Rotate device (or press `Ctrl+F11` in emulator)
3. ✅ Music continues without interruption
4. ✅ UI state is preserved

### Test Scenario 4: Notification Controls
1. Start playing a track
2. Pull down notification shade
3. Test **Pause** button - music pauses
4. Test **Play** button - music resumes
5. Test **Stop** button - music stops and notification disappears

## 📁 Project Structure

```
app/src/main/
├── java/com/example/backgroundmusicplayer/
│   ├── MainActivity.kt                    # Main UI Activity
│   ├── MainViewModel.kt                   # ViewModel for state management
│   ├── model/
│   │   └── PlaybackState.kt              # Sealed class for playback states
│   └── service/
│       ├── MusicPlayerService.kt         # Foreground service with ExoPlayer
│       └── MusicNotificationManager.kt   # Notification management
├── res/
│   ├── layout/
│   │   └── activity_main.xml             # Main UI layout
│   ├── values/
│   │   ├── strings.xml                   # String resources
│   │   ├── colors.xml                    # Color palette
│   │   ├── themes.xml                    # Material theme
│   │   └── dimens.xml                    # Dimensions
│   └── raw/
│       ├── sample1.flac                  # Demo track 1
│       ├── sample2.flac                  # Demo track 2
│       └── sample3.flac                  # Demo track 3
└── AndroidManifest.xml                   # App manifest with permissions
```

## 🔧 Key Technologies

- **Language**: Kotlin
- **Min SDK**: API 26 (Android 8.0 Oreo)
- **Target SDK**: API 34 (Android 14)
- **Media Player**: AndroidX Media3 (ExoPlayer) 1.2.1
- **Architecture**: MVVM with ViewModel + LiveData/StateFlow
- **UI**: Material Components, ViewBinding
- **Concurrency**: Kotlin Coroutines
- **Service Type**: Foreground + Bound Service

## 📋 Dependencies

```kotlin
// ExoPlayer (Media3)
implementation("androidx.media3:media3-exoplayer:1.2.1")
implementation("androidx.media3:media3-ui:1.2.1")
implementation("androidx.media3:media3-session:1.2.1")
implementation("androidx.media3:media3-exoplayer-flac:1.2.1")

// Lifecycle components
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
implementation("androidx.lifecycle:lifecycle-service:2.7.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

## 🔐 Permissions

The app requests the following permissions:

- `READ_EXTERNAL_STORAGE` (Android 6-12) - Access audio files
- `READ_MEDIA_AUDIO` (Android 13+) - Granular audio access
- `FOREGROUND_SERVICE` - Run foreground service
- `FOREGROUND_SERVICE_MEDIA_PLAYBACK` (Android 10+) - Media playback service type
- `WAKE_LOCK` - Prevent device sleep during playback

All dangerous permissions are requested at runtime with proper user prompts.

## ⚠️ Known Limitations

### Android 8.0+ Behavior
When the app is **swiped away from Recent Apps**:
- ❌ Service is killed by Android (expected behavior)
- This is Android's battery optimization policy
- Workaround: Save playback state to SharedPreferences for resume

### Battery Optimization
- Some manufacturers aggressively kill background services
- Users can whitelist the app in battery settings
- Not recommended to request this automatically

## 🎓 Learning Resources

This project demonstrates:

- ✅ Foreground Service implementation
- ✅ ExoPlayer integration for FLAC playback
- ✅ Service binding for Activity-Service communication
- ✅ Notification with PendingIntents for actions
- ✅ StateFlow for reactive state management
- ✅ ViewModel to survive configuration changes
- ✅ Runtime permission handling
- ✅ Proper resource management (no memory leaks)

## 🚧 Future Enhancements

Potential features for production apps:

- [ ] **Playlist Support**: Queue multiple tracks
- [ ] **Media Session**: Lock screen controls, Android Auto support
- [ ] **Progress Persistence**: Save/restore playback position
- [ ] **Storage Access Framework**: Browse and pick any audio file
- [ ] **MediaStore Integration**: Access device music library
- [ ] **Equalizer**: Audio effects and bass boost
- [ ] **Bluetooth Controls**: AVRCP support for headsets
- [ ] **Home Screen Widget**: Quick playback controls
- [ ] **Streaming Support**: Play from network URLs
- [ ] **Gapless Playback**: Seamless track transitions

## 🐛 Troubleshooting

### Build fails with "Could not resolve dependencies"
- Check internet connection
- File → Invalidate Caches → Invalidate and Restart
- Sync Gradle again

### FLAC files not found
- Verify files in `app/src/main/res/raw/`
- File names must be lowercase: `sample1.flac`, `sample2.flac`, `sample3.flac`
- Clean and rebuild project

### No sound during playback
- Check device volume (media volume, not ringer)
- Verify FLAC files are valid
- Check Logcat for ExoPlayer errors

### Music stops when app is closed
- Verify notification appears before closing app
- Check battery optimization settings
- Review Logcat for service lifecycle logs

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

Created as a demonstration of Android Foreground Service and ExoPlayer integration for background music playback.

## 🙏 Acknowledgments

- **AndroidX Media3 Team**: For the excellent ExoPlayer library
- **Google Android Developers**: For comprehensive documentation
- **FLAC Format**: For lossless audio compression

## 📞 Support

For issues, questions, or contributions:

1. Check [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md) for setup help
2. Review [ARCHITECTURE.md](ARCHITECTURE.md) for design details
3. Check Logcat for runtime errors
4. Ensure minimum SDK requirements are met

---

**Ready to build?** Follow the [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md) guide!

**Want to understand the architecture?** Read [ARCHITECTURE.md](ARCHITECTURE.md)!

🎵 **Happy Coding!** 🎵

