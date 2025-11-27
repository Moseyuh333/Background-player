# 🎵 Background Music Player
/**Made by Khôi Nguyễn**/
/**https://github.com/Moseyuh333**/
/**https://github.com/Moseyuh333/Background-player**/ 
A professional Android music player application with background playback support, built using modern Android development practices and Media3 ExoPlayer.

## ✨ Features

### Core Functionality
- **Background Playback** - Continue listening to music even when the app is minimized or the screen is off
- **Foreground Service** - Persistent music playback with notification controls
- **Media Controls** - Play, Pause, Stop, Previous, Next track controls
- **Loop Mode** - Cycle through loop options: OFF → Loop 1x → Loop 2x
- **Seek Control** - Interactive seek bar for precise playback position control
- **Playlist Support** - Play through a queue of multiple tracks

### User Experience
- **Material Design UI** - Clean, modern interface with Material Components
- **Notification Controls** - Control playback directly from the notification shade
- **Real-time Updates** - Live playback position and duration updates
- **Track Information** - Display track name, artist, and playback status
- **Permission Handling** - Smart runtime permission requests for audio access

### Technical Features
- **Media3 ExoPlayer** - Industry-standard media playback engine
- **MVVM Architecture** - Separation of concerns with ViewModel pattern
- **Kotlin Coroutines** - Asynchronous programming with StateFlow
- **Service Binding** - Efficient communication between Activity and Service
- **FLAC Support** - High-quality lossless audio playback

## 🏗️ Architecture

### Project Structure
```
app/
├── src/main/
│   ├── java/com/example/backgroundmusicplayer/
│   │   ├── MainActivity.kt              # Main UI controller
│   │   ├── MainViewModel.kt             # UI state management
│   │   ├── model/
│   │   │   └── PlaybackState.kt         # Playback state definitions
│   │   └── service/
│   │       ├── MusicPlayerService.kt    # Background playback service
│   │       └── MusicNotificationManager.kt  # Notification controls
│   ├── res/
│   │   ├── layout/                      # UI layouts
│   │   ├── raw/                         # Audio files (FLAC)
│   │   └── values/                      # Resources
│   └── AndroidManifest.xml
└── build.gradle.kts
```

### Components

#### MainActivity
- Handles UI interactions and displays track list
- Binds to `MusicPlayerService` for playback control
- Manages runtime permissions (READ_MEDIA_AUDIO, POST_NOTIFICATIONS)
- Observes playback state and updates UI accordingly
- Lifecycle-aware: properly binds/unbinds service

#### MainViewModel
- Manages track list and selected track state
- Provides UI state using Kotlin StateFlow
- Survives configuration changes (screen rotation)

#### MusicPlayerService
- Foreground service for background playback
- Implements ExoPlayer for media playback
- Manages playlist and track navigation
- Handles playback events and state updates
- Supports loop modes (1x, 2x)

#### MusicNotificationManager
- Creates and manages media-style notifications
- Provides notification controls (play, pause, next, previous)
- Updates notification with current track information

#### PlaybackState
Sealed class hierarchy representing different playback states:
- `Playing` - Active playback with position/duration
- `Paused` - Playback paused at position
- `Stopped` - No active playback
- `Preparing` - Loading/buffering track

## 🛠️ Technical Stack

### Dependencies
- **AndroidX Core** - Modern Android development libraries
- **Material Components** - Material Design UI components
- **Media3 ExoPlayer 1.2.1** - Advanced media playback
  - media3-exoplayer
  - media3-ui
  - media3-session
  - media3-common
  - media3-decoder
  - media3-extractor
- **Kotlin Coroutines** - Asynchronous programming
- **ViewBinding** - Type-safe view access

### Requirements
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Compile SDK**: API 34
- **Kotlin**: 1.9.0
- **Gradle**: 8.1.1

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Android SDK with API 34

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Background-player
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the project directory

3. **Sync Gradle**
   - Wait for Gradle sync to complete
   - Ensure all dependencies are downloaded

4. **Build and Run**
   - Connect an Android device or start an emulator
   - Click "Run" (Shift+F10) to build and install

### Build Scripts

The project includes convenient build scripts for Windows:

- **`build_project.bat`** - Build the APK
- **`build_and_install.bat`** - Build and install to connected device
- **`reinstall.bat`** - Reinstall the app

## 📱 Permissions

The app requests the following permissions:

### Android 13+ (API 33+)
- `READ_MEDIA_AUDIO` - Access audio files
- `POST_NOTIFICATIONS` - Show playback notifications
- `FOREGROUND_SERVICE` - Background playback
- `FOREGROUND_SERVICE_MEDIA_PLAYBACK` - Media playback service

### Android 6.0 - 12 (API 23-32)
- `READ_EXTERNAL_STORAGE` - Access audio files
- `FOREGROUND_SERVICE` - Background playback

## 🎮 Usage

### Playing Music
1. Launch the app
2. Grant required permissions when prompted
3. Tap on any track from the list to start playback
4. The music will continue playing in the background

### Playback Controls
- **Play/Pause** - Control playback state
- **Previous/Next** - Navigate through playlist
- **Stop** - Stop playback and clear notification
- **Loop** - Cycle through loop modes (OFF/1x/2x)
- **Seek Bar** - Drag to change playback position

### Notification Controls
When music is playing, a persistent notification appears with:
- Current track information
- Play/Pause button
- Previous/Next track buttons
- Tap to return to the app

## 🔧 Configuration

### Adding Custom Tracks
Place your audio files (FLAC format) in `app/src/main/res/raw/` directory. The app automatically discovers and lists all audio files.

### Supported Audio Formats
- FLAC (Free Lossless Audio Codec) - Primary support
- MP3, M4A, WAV - Supported through ExoPlayer

## 📝 Code Highlights

### Service Lifecycle
The app properly manages service lifecycle:
- Service starts when user plays a track
- Service becomes foreground with notification
- Service continues playing when app is backgrounded
- Service stops when user clicks Stop or clears notification

### State Management
Uses modern Kotlin Flow for reactive state updates:
```kotlin
// Service emits playback state
val playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Stopped)

// Activity observes and updates UI
musicService?.playbackState?.collect { state ->
    updateUI(state)
}
```

### Permission Handling
Smart permission requests based on Android version:
- Android 13+: Requests `READ_MEDIA_AUDIO` and `POST_NOTIFICATIONS`
- Android 6-12: Requests `READ_EXTERNAL_STORAGE`

## 🐛 Troubleshooting

### Music doesn't play
- Ensure audio files are in `res/raw/` directory
- Check that permissions are granted
- Verify audio file format is supported

### Notification doesn't show
- Grant `POST_NOTIFICATIONS` permission (Android 13+)
- Check that service is running in foreground

### App crashes on startup
- Check logcat for error messages
- Verify all dependencies are properly installed
- Ensure target device meets minimum SDK requirements

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

### Development Guidelines
- Follow Kotlin coding conventions
- Use Material Design principles
- Add comments for complex logic
- Test on multiple Android versions
- Handle edge cases gracefully

## 📧 Contact

For questions or support, please open an issue in the repository.

---

**Built with ❤️ using Kotlin and Android Jetpack**

