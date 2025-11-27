package com.example.backgroundmusicplayer.service

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.backgroundmusicplayer.model.PlaybackState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

/**
 * Foreground Service for managing music playback.
 *
 * This service:
 * - Runs as a foreground service with a persistent notification
 * - Uses ExoPlayer for reliable FLAC audio playback
 * - Implements both Started and Bound service patterns
 * - Survives Activity destruction and app backgrounding
 * - Handles notification action clicks (Play/Pause/Stop)
 *
 * Lifecycle:
 * - Started via startService() from MainActivity
 * - Bound via bindService() for UI communication
 * - Calls startForeground() to become foreground service
 * - Continues playing even when Activity is destroyed
 * - Stops when user presses Stop or service is explicitly stopped
 */
@UnstableApi
class MusicPlayerService : Service() {

    companion object {
        private const val TAG = "MusicPlayerService"

        // Intent extras
        const val EXTRA_TRACK_URI = "track_uri"
        const val EXTRA_TRACK_NAME = "track_name"
        const val EXTRA_ARTIST = "artist"
    }

    // ExoPlayer instance for audio playback
    private var exoPlayer: ExoPlayer? = null

    // Notification manager
    private lateinit var notificationManager: MusicNotificationManager

    // Current track information
    private var currentTrackName: String = "No Track"
    private var currentArtist: String = "Unknown Artist"

    // Playlist management
    private val playlist = mutableListOf<TrackInfo>()
    private var currentTrackIndex = 0

    // Loop management - Loop single track (0=off, 1=loop once, 2=loop twice)
    private var loopMode = 0 // 0=off, 1=loop 1x, 2=loop 2x
    private var currentTrackLoopCount = 0 // How many times current track has looped

    // Playback state exposed via StateFlow for reactive UI updates
    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Stopped)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    // Data class for track information
    data class TrackInfo(
        val uri: String,
        val name: String,
        val artist: String
    )

    // Binder for Activity to communicate with Service
    private val binder = MusicPlayerBinder()

    // Coroutine scope for background tasks
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var notificationUpdateJob: Job? = null

    /**
     * Binder class that provides access to the service instance.
     * Activities can use this to control playback directly.
     */
    inner class MusicPlayerBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    /**
     * Called when the service is created.
     * Initialize ExoPlayer and notification manager here.
     */
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate()")

        // Initialize ExoPlayer
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            // Set audio attributes for music playback
            val audioAttributes = androidx.media3.common.AudioAttributes.Builder()
                .setContentType(androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(androidx.media3.common.C.USAGE_MEDIA)
                .build()
            setAudioAttributes(audioAttributes, true) // true = handle audio focus automatically

            // Ensure volume is set to maximum (1.0f)
            volume = 1.0f

            // Add listener for playback state changes
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            Log.d(TAG, "ExoPlayer STATE_READY")
                            updatePlaybackState()
                        }
                        Player.STATE_BUFFERING -> {
                            Log.d(TAG, "ExoPlayer STATE_BUFFERING")
                            _playbackState.value = PlaybackState.Preparing(currentTrackName)
                        }
                        Player.STATE_ENDED -> {
                            Log.d(TAG, "ExoPlayer STATE_ENDED - Auto-playing next track")
                            handleTrackEnded()
                        }
                        Player.STATE_IDLE -> {
                            Log.d(TAG, "ExoPlayer STATE_IDLE")
                        }
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    Log.d(TAG, "ExoPlayer onIsPlayingChanged: $isPlaying")
                    updatePlaybackState()
                    updateNotification()

                    // Start or stop periodic notification updates
                    if (isPlaying) {
                        startNotificationUpdates()
                    } else {
                        stopNotificationUpdates()
                    }
                }

                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    Log.e(TAG, "ExoPlayer error: ${error.errorCodeName} - ${error.message}", error)
                    _playbackState.value = PlaybackState.Stopped
                }
            })
        }

        // Initialize notification manager
        notificationManager = MusicNotificationManager(this)
    }

    /**
     * Start periodic notification updates to show progress.
     */
    private fun startNotificationUpdates() {
        stopNotificationUpdates() // Cancel any existing job
        notificationUpdateJob = serviceScope.launch {
            while (true) {
                delay(1000) // Update every second
                exoPlayer?.let { player ->
                    if (player.isPlaying) {
                        updateNotification()
                        updatePlaybackState()
                    }
                }
            }
        }
    }

    /**
     * Stop periodic notification updates.
     */
    private fun stopNotificationUpdates() {
        notificationUpdateJob?.cancel()
        notificationUpdateJob = null
    }

    /**
     * Called when the service is started via startService().
     *
     * @param intent Intent containing track information
     * @param flags Additional data about start request
     * @param startId Unique ID for this start request
     * @return START_STICKY to have service restarted if killed by system
     *
     * START_STICKY vs START_NOT_STICKY:
     * - START_STICKY: Service is restarted if killed, but Intent is null on restart.
     *   Good for services that should continue running (like music playback).
     *   The service can resume last played track from SharedPreferences.
     * - START_NOT_STICKY: Service is not restarted if killed.
     *   Better for one-off tasks.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand() - Action: ${intent?.action}")

        when (intent?.action) {
            MusicNotificationManager.ACTION_PLAY_PAUSE -> {
                handlePlayPause()
            }
            MusicNotificationManager.ACTION_STOP -> {
                handleStop()
            }
            MusicNotificationManager.ACTION_PREVIOUS -> {
                handlePrevious()
            }
            MusicNotificationManager.ACTION_NEXT -> {
                handleNext()
            }
            else -> {
                // Start/resume playback with new track
                val trackUri = intent?.getStringExtra(EXTRA_TRACK_URI)
                val trackName = intent?.getStringExtra(EXTRA_TRACK_NAME) ?: "Unknown Track"
                val artist = intent?.getStringExtra(EXTRA_ARTIST) ?: "Unknown Artist"

                if (trackUri != null) {
                    currentTrackName = trackName
                    currentArtist = artist
                    prepareAndPlay(trackUri)
                }

                // Start foreground service with notification
                startForegroundService()
            }
        }

        return START_STICKY
    }

    /**
     * Called when a client binds to the service.
     * @return Binder object for client to interact with service
     */
    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "onBind()")
        return binder
    }

    /**
     * Called when all clients have unbound from the service.
     * We return false to indicate we don't want onRebind() to be called.
     */
    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind()")
        return false // Don't want onRebind() to be called
    }

    /**
     * Called when the service is destroyed.
     * Release all resources here to prevent memory leaks.
     */
    override fun onDestroy() {
        Log.d(TAG, "Service onDestroy()")

        // Stop notification updates
        stopNotificationUpdates()

        // Cancel coroutine scope
        serviceScope.cancel()

        // Release ExoPlayer resources
        exoPlayer?.release()
        exoPlayer = null

        // Cancel notification
        notificationManager.cancelNotification()

        super.onDestroy()
    }

    /**
     * Start the service as a foreground service with notification.
     * This is required to keep the service running when the app is backgrounded.
     */
    private fun startForegroundService() {
        val player = exoPlayer ?: return
        val notification = notificationManager.buildNotification(
            trackName = currentTrackName,
            artist = currentArtist,
            isPlaying = player.isPlaying,
            position = player.currentPosition,
            duration = if (player.duration > 0) player.duration else 0L
        )

        // Start foreground service
        // This makes the service high priority and shows a persistent notification
        startForeground(MusicNotificationManager.NOTIFICATION_ID, notification)
    }

    /**
     * Prepare and play a track from URI.
     * @param uriString URI of the track (can be raw resource, file, or content URI)
     */
    private fun prepareAndPlay(uriString: String) {
        try {
            Log.d(TAG, "Preparing track: $uriString")
            val mediaItem = MediaItem.fromUri(Uri.parse(uriString))
            exoPlayer?.apply {
                // Stop current playback
                stop()
                // Clear previous media items
                clearMediaItems()
                // Set new media item
                setMediaItem(mediaItem)
                // Prepare the player
                prepare()
                // Ensure volume is set
                volume = 1.0f
                // Start playback
                playWhenReady = true
                play()
            }
            Log.d(TAG, "Track prepared and playing. Volume: ${exoPlayer?.volume}")
        } catch (e: Exception) {
            Log.e(TAG, "Error preparing track: ${e.message}", e)
        }
    }

    /**
     * Update the notification based on current playback state.
     */
    private fun updateNotification() {
        val player = exoPlayer ?: return
        val notification = notificationManager.buildNotification(
            trackName = currentTrackName,
            artist = currentArtist,
            isPlaying = player.isPlaying,
            position = player.currentPosition,
            duration = if (player.duration > 0) player.duration else 0L
        )
        notificationManager.updateNotification(notification)
    }

    /**
     * Update playback state based on ExoPlayer state.
     */
    private fun updatePlaybackState() {
        exoPlayer?.let { player ->
            val position = player.currentPosition
            val duration = player.duration.takeIf { it > 0 } ?: 0L

            _playbackState.value = if (player.isPlaying) {
                PlaybackState.Playing(currentTrackName, position, duration, currentTrackIndex)
            } else if (player.playbackState == Player.STATE_READY) {
                PlaybackState.Paused(currentTrackName, position, duration, currentTrackIndex)
            } else {
                PlaybackState.Stopped
            }
        }
    }

    /**
     * Handle play/pause action from notification.
     */
    private fun handlePlayPause() {
        exoPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }
    }

    /**
     * Handle stop action from notification.
     */
    private fun handleStop() {
        exoPlayer?.stop()
        _playbackState.value = PlaybackState.Stopped
        currentTrackLoopCount = 0 // Reset loop count when stopping
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    /**
     * Handle track ended - Loop current track or play next based on loop mode
     */
    private fun handleTrackEnded() {
        if (playlist.isEmpty()) {
            Log.d(TAG, "Playlist empty, stopping playback")
            handleStop()
            return
        }

        // Check if current track should loop
        if (loopMode > 0 && currentTrackLoopCount < loopMode) {
            // Loop current track
            currentTrackLoopCount++
            Log.d(TAG, "Looping current track (${currentTrackLoopCount}/${loopMode})")
            playTrackAtIndex(currentTrackIndex, resetLoopCount = false)
        } else {
            // Reset loop counter and play next track
            currentTrackLoopCount = 0

            if (currentTrackIndex >= playlist.size - 1) {
                // Reached end of playlist, stop
                Log.d(TAG, "Playlist ended, stopping playback")
                _playbackState.value = PlaybackState.Stopped
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            } else {
                // Play next track
                currentTrackIndex++
                Log.d(TAG, "Auto-playing next track: ${currentTrackIndex + 1}/${playlist.size}")
                playTrackAtIndex(currentTrackIndex)
            }
        }
    }

    /**
     * Handle previous track action.
     */
    private fun handlePrevious() {
        if (playlist.isEmpty()) return

        // Manual previous action doesn't affect loop count
        currentTrackIndex = if (currentTrackIndex > 0) {
            currentTrackIndex - 1
        } else {
            playlist.size - 1 // Loop to last track
        }

        playTrackAtIndex(currentTrackIndex, isManualAction = true)
    }

    /**
     * Handle next track action.
     */
    private fun handleNext() {
        if (playlist.isEmpty()) return

        // Manual next action doesn't affect loop count
        currentTrackIndex = if (currentTrackIndex < playlist.size - 1) {
            currentTrackIndex + 1
        } else {
            0 // Loop to first track
        }

        playTrackAtIndex(currentTrackIndex, isManualAction = true)
    }

    /**
     * Play track at specific index.
     * @param index Track index in playlist
     * @param isManualAction True if triggered by user action (resets loop count)
     * @param resetLoopCount Whether to reset the current track loop counter
     */
    private fun playTrackAtIndex(index: Int, isManualAction: Boolean = false, resetLoopCount: Boolean = true) {
        if (index < 0 || index >= playlist.size) return

        val track = playlist[index]
        currentTrackName = track.name
        currentArtist = track.artist
        currentTrackIndex = index

        // Reset loop count on manual track selection or when moving to new track
        if (isManualAction || resetLoopCount) {
            currentTrackLoopCount = 0
            Log.d(TAG, "Track loop count reset")
        }

        prepareAndPlay(track.uri)
        updateNotification()
    }

    // Public methods for Activity to control playback via Binder

    /**
     * Play or resume playback.
     */
    fun play() {
        exoPlayer?.play()
    }

    /**
     * Pause playback.
     */
    fun pause() {
        exoPlayer?.pause()
    }

    /**
     * Stop playback and stop service.
     */
    fun stop() {
        handleStop()
    }

    /**
     * Check if currently playing.
     */
    fun isPlaying(): Boolean = exoPlayer?.isPlaying ?: false

    /**
     * Get current playback position in milliseconds.
     */
    fun getCurrentPosition(): Long = exoPlayer?.currentPosition ?: 0L

    /**
     * Get track duration in milliseconds.
     */
    fun getDuration(): Long = exoPlayer?.duration?.takeIf { it > 0 } ?: 0L

    /**
     * Seek to a specific position.
     * @param positionMs Position in milliseconds
     */
    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
    }

    /**
     * Set the playlist for the service.
     */
    fun setPlaylist(tracks: List<TrackInfo>, startIndex: Int = 0) {
        playlist.clear()
        playlist.addAll(tracks)
        currentTrackIndex = startIndex.coerceIn(0, tracks.size - 1)
        currentTrackLoopCount = 0 // Reset loop count when setting new playlist
        Log.d(TAG, "Playlist set with ${tracks.size} tracks, starting at index $startIndex")
    }

    /**
     * Play previous track in playlist.
     */
    fun previous() {
        handlePrevious()
    }

    /**
     * Play next track in playlist.
     */
    fun next() {
        handleNext()
    }

    /**
     * Get current track index.
     */
    fun getCurrentTrackIndex(): Int = currentTrackIndex

    /**
     * Cycle loop mode: OFF -> 1x -> 2x -> OFF
     * @return New loop mode (0, 1, or 2)
     */
    fun cycleLoopMode(): Int {
        loopMode = when (loopMode) {
            0 -> 1
            1 -> 2
            else -> 0
        }
        currentTrackLoopCount = 0 // Reset counter when changing mode

        val modeText = when (loopMode) {
            1 -> "Loop 1x"
            2 -> "Loop 2x"
            else -> "Loop OFF"
        }
        Log.d(TAG, "Loop mode changed to: $modeText")
        return loopMode
    }

    /**
     * Get current loop mode.
     */
    fun getLoopMode(): Int = loopMode
}

