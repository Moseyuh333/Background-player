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

    // Playback state exposed via StateFlow for reactive UI updates
    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Stopped)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    // Binder for Activity to communicate with Service
    private val binder = MusicPlayerBinder()

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
                            Log.d(TAG, "ExoPlayer STATE_ENDED")
                            _playbackState.value = PlaybackState.Stopped
                            stopForeground(STOP_FOREGROUND_REMOVE)
                            stopSelf()
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
                }
            })
        }

        // Initialize notification manager
        notificationManager = MusicNotificationManager(this)
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
        val notification = notificationManager.buildNotification(
            trackName = currentTrackName,
            artist = currentArtist,
            isPlaying = exoPlayer?.isPlaying ?: false
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
            val mediaItem = MediaItem.fromUri(Uri.parse(uriString))
            exoPlayer?.apply {
                setMediaItem(mediaItem)
                prepare()
                play()
            }
            Log.d(TAG, "Preparing and playing: $uriString")
        } catch (e: Exception) {
            Log.e(TAG, "Error preparing track", e)
        }
    }

    /**
     * Update the notification based on current playback state.
     */
    private fun updateNotification() {
        val notification = notificationManager.buildNotification(
            trackName = currentTrackName,
            artist = currentArtist,
            isPlaying = exoPlayer?.isPlaying ?: false
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
                PlaybackState.Playing(currentTrackName, position, duration)
            } else if (player.playbackState == Player.STATE_READY) {
                PlaybackState.Paused(currentTrackName, position, duration)
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
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
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
}

