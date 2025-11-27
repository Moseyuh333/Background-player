# 🎵 Background Music Player

<div align="center">

![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

*A professional Android music player with foreground service support for uninterrupted playback*

[Features](#-features) • [Screenshots](#-screenshots) • [Installation](#-installation) • [Architecture](#-architecture) • [Building](#-building) • [License](#-license)

</div>

---

## 📱 Overview

Background Music Player is a modern Android application that provides seamless music playback with a persistent notification interface. Built with Kotlin and leveraging Android's Foreground Service, the app ensures your music continues playing even when the app is in the background or the screen is locked.

### ✨ Key Highlights

- 🎼 **High-Quality Audio**: Supports FLAC format for lossless audio playback
- 🔔 **Media Controls**: Rich notification with play/pause/skip controls
- 🔄 **Persistent Playback**: Continues playing when app is backgrounded or screen is locked
- 🔁 **Auto-Play & Loop**: Automatically plays next track and loops playlist up to 2 times
- 📱 **Modern UI**: Scrollable RecyclerView with Material Design 3 cards
- 🎯 **Lightweight**: Optimized for performance and battery efficiency
- 🌐 **Japanese & Chinese Support**: Full Unicode support for international track names

---

## 🎯 Features

### Core Functionality

- **Scrollable Track List**
  - RecyclerView with smooth scrolling
  - Material Design 3 cards for each track
  - Shows track name, artist, and album
  - Tap any track to play instantly

- **Foreground Service Playback**
  - Music continues playing when app is minimized
  - Survives screen rotation and configuration changes
  - Maintains playback state across app lifecycle

- **Rich Notification Interface**
  - Always-visible playback controls
  - Track name and artist display
  - Progress indicator
  - Previous, Play/Pause, Next, and Stop buttons

- **Full Playback Control**
  - Play, Pause, Stop functionality
  - Previous/Next track navigation
  - Seek bar for position control
  - Real-time progress updates

- **Smart Playlist Management**
  - Automatic track progression
  - Single-track loop (repeat current track 1x or 2x)
  - Toggle loop mode with one button
  - Seamless transition between tracks
  - Loop counter resets on manual track selection

- **Media Format Support**
  - FLAC (Free Lossless Audio Codec)
  - High-quality audio playback via ExoPlayer (Media3)
  - Efficient buffering and streaming

### Auto-Play & Loop Behavior

The app includes intelligent single-track loop management:

1. **Automatic Track Progression**
   - When a track finishes, the next track automatically starts
   - No manual intervention needed for continuous playback

2. **Single-Track Loop System**
   - **🔁 OFF**: Normal playback, auto-advance to next track
   - **🔁 1x**: Current track repeats 1 time before moving to next
   - **🔁 2x**: Current track repeats 2 times before moving to next
   - Tap loop button to cycle: OFF → 1x → 2x → OFF

3. **Loop Button Behavior**
   - Press once: Enable loop 1x (track plays 2 times total)
   - Press twice: Enable loop 2x (track plays 3 times total)
   - Press third time: Disable loop (back to normal)
   - Visual indicators: Different border colors for each mode

4. **Smart Reset Logic**
   - Loop counter resets when manually selecting a new track
   - Loop counter resets when using Previous/Next buttons
   - Loop mode stays active across track changes

5. **Seamless Experience**
   - No gaps between track loops
   - Smooth transitions
   - State preserved across app lifecycle

**Example Flow with Loop 2x:**
```
Track 1 (play 1) → Track 1 (loop 1) → Track 1 (loop 2) → Track 2 →  ...
```

### Technical Features

- **Modern Android Architecture**
  - MVVM pattern with ViewModel
  - Kotlin Coroutines for async operations
  - StateFlow for reactive state management
  - ViewBinding for type-safe view access

- **Android Jetpack Components**
  - Lifecycle-aware components
  - ViewModel for configuration survival
  - Material Design 3 components

- **Robust Service Management**
  - Bound and Started service pattern
  - Proper lifecycle handling
  - Battery optimization compatible
  - Audio focus management

---

## 📸 Screenshots

| Main Screen | Now Playing | Notification |
|------------|-------------|--------------|
| *Track selection interface with scrollable playlist* | *Active playback with progress indicator* | *Persistent notification with media controls* |

---

## 🚀 Installation

### Prerequisites

- Android device or emulator running **Android 8.0 (API 26)** or higher
- For building: Android Studio Hedgehog or newer
- Gradle 8.2+
- JDK 11 or higher

### Quick Install

1. **Download APK**
   ```bash
   # Clone the repository
   git clone https://github.com/yourusername/background-music-player.git
   cd background-music-player
   ```

2. **Build & Install**
   ```bash
   # Windows
   gradlew.bat assembleDebug installDebug
   
   # macOS/Linux
   ./gradlew assembleDebug installDebug
   ```

3. **Grant Permissions**
   - Allow notification permissions (Android 13+)
   - Allow audio file access
   - Disable battery optimization for best performance

---

## 🏗️ Architecture

### Project Structure

```
app/
├── src/main/
│   ├── java/com/example/backgroundmusicplayer/
│   │   ├── MainActivity.kt              # Main UI controller
│   │   ├── MainViewModel.kt             # UI state management
│   │   ├── model/
│   │   │   └── PlaybackState.kt         # State definitions
│   │   └── service/
│   │       ├── MusicPlayerService.kt    # Background service
│   │       └── MusicNotificationManager.kt  # Notification handler
│   ├── res/
│   │   ├── layout/
│   │   │   └── activity_main.xml        # Main UI layout
│   │   ├── raw/                         # Audio files (FLAC)
│   │   └── values/                      # Strings, themes, colors
│   └── AndroidManifest.xml              # App configuration
```

### Design Pattern: MVVM

```
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│  MainActivity│ ◄─────► │ MainViewModel│         │   Service   │
│   (View)    │         │  (ViewModel) │         │             │
└─────────────┘         └──────────────┘         └─────────────┘
       │                       │                         │
       │                       │                         │
       ▼                       ▼                         ▼
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│ ViewBinding │         │  StateFlow   │         │  ExoPlayer  │
└─────────────┘         └──────────────┘         └─────────────┘
```

### Key Components

#### 1. **MainActivity**
- Handles UI interactions
- Binds to MusicPlayerService
- Observes playback state via StateFlow
- Manages permissions

#### 2. **MainViewModel**
- Maintains track list
- Preserves UI state across configuration changes
- Provides StateFlow for reactive updates

#### 3. **MusicPlayerService**
- Foreground service for background playback
- Manages ExoPlayer instance
- Handles notification updates
- Exposes playback state via StateFlow

#### 4. **MusicNotificationManager**
- Creates and updates media-style notifications
- Handles notification action clicks
- Manages notification channel

---

## 🛠️ Building

### Development Setup

1. **Clone Repository**
   ```bash
   git clone https://github.com/yourusername/background-music-player.git
   cd background-music-player
   ```

2. **Open in Android Studio**
   - File → Open → Select project folder
   - Wait for Gradle sync to complete

3. **Add Music Files**
   - Place FLAC files in `app/src/main/res/raw/`
   - File names must be lowercase with underscores only
   - Update `MainViewModel.kt` with track information

4. **Build Variants**
   ```bash
   # Debug build
   ./gradlew assembleDebug
   
   # Release build (requires signing config)
   ./gradlew assembleRelease
   ```

### Build Configuration

**Minimum Requirements:**
- `minSdk`: 26 (Android 8.0)
- `targetSdk`: 34 (Android 14)
- `compileSdk`: 34

**Key Dependencies:**
```gradle
// Media3 (ExoPlayer)
androidx.media3:media3-exoplayer:1.2.1
androidx.media3:media3-ui:1.2.1
androidx.media3:media3-session:1.2.1

// Kotlin Coroutines
kotlinx-coroutines-android:1.7.3

// AndroidX Lifecycle
lifecycle-viewmodel-ktx:2.7.0
lifecycle-runtime-ktx:2.7.0
```

---

## 🎼 Adding Your Own Music

### Step 1: Prepare Audio Files

Convert your audio to FLAC format (optional but recommended for quality):
```bash
ffmpeg -i input.mp3 -c:a flac output.flac
```

### Step 2: Add to Resources

1. Copy FLAC files to `app/src/main/res/raw/`
2. Rename files using only lowercase letters, numbers, and underscores:
   - ✅ `my_favorite_song.flac`
   - ❌ `My Favorite Song.flac`
   - ❌ `song-name.flac`

### Step 3: Update Track List

Edit `MainViewModel.kt`:

```kotlin
_tracks.value = listOf(
    Track(
        id = 1,
        name = "Your Song Title",  // Display name (can use any characters)
        artist = "Artist Name",
        album = "Album Name",
        uri = "android.resource://com.example.backgroundmusicplayer/${R.raw.your_file_name}"
    ),
    // Add more tracks...
)
```

### Step 4: Rebuild & Install

```bash
./gradlew clean assembleDebug installDebug
```

---

## 🔧 Configuration

### Permissions

The app requires the following permissions:

```xml
<!-- Audio file access (Android 6-12) -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

<!-- Audio file access (Android 13+) -->
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />

<!-- Notification display (Android 13+) -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<!-- Foreground service -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />

<!-- Prevent device sleep during playback -->
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

### Customization

**Colors** - Edit `res/values/colors.xml`:
```xml
<color name="primary">#6200EE</color>
<color name="primary_variant">#3700B3</color>
```

**App Name** - Edit `res/values/strings.xml`:
```xml
<string name="app_name">Your App Name</string>
```

**Notification Channel** - Edit `MusicNotificationManager.kt`:
```kotlin
const val CHANNEL_NAME = "Your Channel Name"
```

---

## 🐛 Troubleshooting

### No Sound During Playback

1. **Check device volume**: Use hardware volume buttons to increase media volume
2. **Grant permissions**: Ensure notification and audio permissions are granted
3. **Disable battery optimization**: Settings → Battery → Unrestricted
4. **Check logs**: Run `adb logcat | grep MusicPlayerService` to see errors

### App Crashes on Launch

1. **Clean build**: `./gradlew clean`
2. **Rebuild**: `./gradlew assembleDebug`
3. **Check API level**: Device must be Android 8.0 (API 26) or higher

### Music Stops When Screen Locks

1. **Check battery settings**: Disable battery optimization for the app
2. **Check Do Not Disturb**: Ensure app is allowed during DND mode

### Notification Not Showing

1. **Grant permission**: Android 13+ requires POST_NOTIFICATIONS permission
2. **Check notification settings**: Settings → Apps → Background Music Player → Notifications

---

## 🧪 Testing

### Manual Testing Checklist

- [ ] App launches without crash
- [ ] Permissions are requested on first launch
- [ ] Track list displays all 6 songs
- [ ] Selecting a track starts playback
- [ ] Notification appears with controls
- [ ] Play/Pause buttons work
- [ ] Previous/Next buttons navigate tracks
- [ ] Seek bar updates during playback
- [ ] Music continues when app is minimized
- [ ] Music continues when screen is locked
- [ ] Stop button terminates playback and service
- [ ] Track automatically advances to next song when finished
- [ ] Loop button cycles through OFF → 1x → 2x → OFF
- [ ] Loop 1x: Current track plays 2 times total before next
- [ ] Loop 2x: Current track plays 3 times total before next
- [ ] Manual track selection resets loop counter
- [ ] Loop mode persists across different tracks

### Logcat Monitoring

```bash
# View all service logs
adb logcat -s MusicPlayerService

# View with errors
adb logcat *:E

# Clear logs and monitor
adb logcat -c && adb logcat -s MusicPlayerService
```

---

## 📊 Technical Specifications

| Component | Technology |
|-----------|------------|
| **Language** | Kotlin 1.9.20 |
| **UI Framework** | Android View System with ViewBinding + RecyclerView |
| **Architecture** | MVVM (Model-View-ViewModel) |
| **Audio Engine** | ExoPlayer (Media3) 1.2.1 |
| **Concurrency** | Kotlin Coroutines & Flow |
| **Dependency Injection** | Manual (no DI framework) |
| **Min SDK** | 26 (Android 8.0 Oreo) |
| **Target SDK** | 34 (Android 14) |
| **Build System** | Gradle 8.2 with Kotlin DSL |

---

## 📈 Performance

- **APK Size**: ~300 MB (includes 6 FLAC audio files)
- **RAM Usage**: ~50-80 MB during playback
- **Battery Impact**: Minimal (uses optimized foreground service)
- **Audio Latency**: <100ms playback start time

---

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Keep functions small and focused

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2025 Background Music Player

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 👨‍💻 Authors

- **Your Name** - *Initial work* - [YourGitHub](https://github.com/yourusername)

---

## 🙏 Acknowledgments

- [ExoPlayer](https://exoplayer.dev/) - Powerful media player for Android
- [Material Design](https://m3.material.io/) - UI design guidelines
- [Kotlin](https://kotlinlang.org/) - Modern programming language for Android
- Android Open Source Project - Platform and tools

---

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/yourusername/background-music-player/issues)
- **Email**: your.email@example.com
- **Documentation**: [Wiki](https://github.com/yourusername/background-music-player/wiki)

---

## 🗺️ Roadmap

### Version 2.0 (Planned)

- [x] **Auto-play next track** ✅ (Completed)
- [x] **Single-track loop (1x/2x)** ✅ (Completed)
- [x] **Toggle loop button with visual feedback** ✅ (Completed)
- [ ] Playlist management with save/load
- [ ] Shuffle mode
- [ ] Custom repeat modes (single track, infinite loop)
- [ ] Equalizer with presets
- [ ] Sleep timer
- [ ] Widget support
- [ ] Android Auto integration
- [ ] Folder browsing
- [ ] Album art display
- [ ] Queue management
- [ ] Favorite tracks

### Version 3.0 (Future)

- [ ] Online streaming support
- [ ] Lyrics display
- [ ] Cloud sync
- [ ] Chromecast support
- [ ] Podcast support

---

<div align="center">

**⭐ Star this repository if you find it helpful! ⭐**

Made with ❤️ using Kotlin and Android

[Back to Top](#-background-music-player)

</div>

