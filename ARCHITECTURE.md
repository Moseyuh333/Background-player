# Android Music Player with Foreground Service - Architecture Overview

## High-Level Architecture

This app implements a robust background music player using Android's Foreground Service pattern combined with ExoPlayer for reliable FLAC audio playback. The architecture follows the Service-Activity separation pattern where:

**MusicPlayerService** runs as a foreground service that manages the ExoPlayer instance, handles media playback lifecycle, and displays a persistent media notification. The service uses a hybrid approach - it's both a Started Service (for background operation) and a Bound Service (for UI communication). When started via `startService()`, it calls `startForeground()` with a notification containing media controls, ensuring the service survives even when the Activity is destroyed. The service uses `START_STICKY` return type so Android attempts to restart it if killed due to low memory.

**MainActivity** provides the UI for track selection and playback controls. It binds to MusicPlayerService to get direct access to the service's Binder interface, allowing real-time playback control and state observation through LiveData/StateFlow. The Activity requests runtime permissions for audio file access (READ_MEDIA_AUDIO on Android 13+, READ_EXTERNAL_STORAGE on older versions) and can load FLAC files from raw resources or external storage. A ViewModel maintains UI state across configuration changes like screen rotation.

**Communication Flow**: Activity → starts Service via Intent → Service initializes ExoPlayer → Service calls startForeground() with Notification → Activity binds to Service → Activity gets Binder → Activity controls playback through Binder interface → Service updates Notification based on playback state → User can close Activity and music continues playing → Notification actions (Play/Pause/Stop) send PendingIntents back to Service → Service handles actions and updates state → When Stop is pressed, Service calls stopForeground(true) and stopSelf().

## Why ExoPlayer over MediaPlayer for FLAC?

- **Native FLAC Support**: ExoPlayer has built-in FLAC decoder while MediaPlayer's FLAC support is device-dependent and inconsistent across Android versions
- **Better Error Handling**: Provides detailed error callbacks and recovery mechanisms
- **Extensibility**: Modular architecture allows custom renderers, data sources, and track selectors
- **Active Development**: Google actively maintains ExoPlayer (now AndroidX Media3)
- **Advanced Features**: Built-in support for gapless playback, adaptive streaming, and multiple audio formats

## Component Breakdown

### 1. MusicPlayerService.kt
- **Type**: Foreground + Bound Service
- **Lifecycle**: onCreate() → initializes ExoPlayer and notification channel
- **onStartCommand()**: Receives track URI/name via Intent, prepares media, starts playback, calls startForeground()
- **onBind()**: Returns Binder for Activity to control playback
- **Responsibilities**: 
  - Manage ExoPlayer instance
  - Build and update media notification
  - Handle notification action intents (PLAY/PAUSE/STOP)
  - Expose playback controls via Binder
  - Broadcast playback state via LiveData/StateFlow
  - Clean up resources in onDestroy()

### 2. MainActivity.kt
- **Type**: Single Activity UI
- **Responsibilities**:
  - Request runtime permissions
  - Display track list/buttons (3 demo FLAC files)
  - Bind to MusicPlayerService
  - Control playback through service Binder
  - Observe and display playback state (Playing/Paused/Stopped)
  - Handle track selection and service start

### 3. MusicNotificationManager.kt
- **Helper class** for notification creation and updates
- Creates notification channel (required for API 26+)
- Builds MediaStyle notification with:
  - Song title and artist/album info
  - Play/Pause action button
  - Stop/Close action button
  - Large icon for media
  - PendingIntents for user actions

### 4. PlaybackState Data Class
- Sealed class or data class representing: Playing(trackName), Paused(trackName), Stopped
- Shared via StateFlow/LiveData from Service to Activity

## Lifecycle & Edge Cases Handling

### Screen Rotation
- Service continues running (unaffected)
- Activity is recreated
- ViewModel retains state
- Activity re-binds to service in onStart()
- Playback state is restored via StateFlow observation

### User Presses HOME or BACK
- Activity goes to background/destroyed
- Service continues running as foreground service
- Notification remains visible
- Music keeps playing
- User can control via notification

### App Swiped from Recent Apps
- On Android 8.0+: Service is killed by system (Android policy)
- Service can survive temporarily if playing media
- START_STICKY ensures restart attempt (without Intent extras)
- Best practice: Save state in SharedPreferences for resume
- Limitation: Android may kill foreground services aggressively to save battery

### Notification Swiped Away or Stop Pressed
- Stop action: Service receives STOP intent → player.stop() → stopForeground(true) → stopSelf()
- Swipe away: Only possible if notification is not ongoing (we make it ongoing for media playback)
- Service properly releases ExoPlayer to prevent memory leaks

### Service Stopped
1. `stopForeground(true)` - removes notification
2. `player.release()` - releases ExoPlayer resources
3. `stopSelf()` - stops service
4. Service unbinds from Activity
5. Activity updates UI to show Stopped state

## Permissions & Android Versions

- **Android 6.0 - 12 (API 23-32)**: READ_EXTERNAL_STORAGE (dangerous permission - runtime request)
- **Android 13+ (API 33+)**: READ_MEDIA_AUDIO (granular media permission)
- **All versions**: FOREGROUND_SERVICE permission (normal permission - manifest only)
- **Android 10+ (API 29+)**: FOREGROUND_SERVICE_MEDIA_PLAYBACK (if targeting API 34+)

## File Access Strategy

1. **Raw Resources** (res/raw/): Simple, no permissions needed, included in APK
2. **External Storage**: Requires runtime permissions
3. **Storage Access Framework (SAF)**: Modern approach, user grants access per file
4. **MediaStore API**: Query system media library (requires READ_MEDIA_AUDIO)

This app demonstrates both raw resources (easy demo) and SAF-based file picking (production approach).

