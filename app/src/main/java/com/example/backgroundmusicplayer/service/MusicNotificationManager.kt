package com.example.backgroundmusicplayer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import com.example.backgroundmusicplayer.MainActivity
import com.example.backgroundmusicplayer.R

/**
 * Helper class to manage music player notifications.
 * Handles notification channel creation and notification building.
 */
@UnstableApi
class MusicNotificationManager(private val context: Context) {

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "music_playback_channel"
        const val CHANNEL_NAME = "Music Playback"

        // Notification action constants
        const val ACTION_PLAY_PAUSE = "com.example.backgroundmusicplayer.ACTION_PLAY_PAUSE"
        const val ACTION_STOP = "com.example.backgroundmusicplayer.ACTION_STOP"
        const val ACTION_PREVIOUS = "com.example.backgroundmusicplayer.ACTION_PREVIOUS"
        const val ACTION_NEXT = "com.example.backgroundmusicplayer.ACTION_NEXT"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    /**
     * Creates notification channel for Android O+ (API 26+).
     * Notification channels are required for showing notifications on Android 8.0+.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW // LOW to avoid sound/vibration
            ).apply {
                description = "Shows currently playing music and playback controls"
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(false)
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Builds a notification for the music player service.
     *
     * @param trackName Name of the currently playing track
     * @param artist Artist name
     * @param isPlaying Whether the track is currently playing
     * @param position Current playback position in milliseconds
     * @param duration Total track duration in milliseconds
     * @return Notification object ready to be displayed
     */
    fun buildNotification(
        trackName: String,
        artist: String = "Unknown Artist",
        isPlaying: Boolean,
        position: Long = 0L,
        duration: Long = 0L
    ): Notification {
        // Intent to open the app when notification is clicked
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Previous action
        val previousIntent = Intent(context, MusicPlayerService::class.java).apply {
            action = ACTION_PREVIOUS
        }
        val previousPendingIntent = PendingIntent.getService(
            context,
            2,
            previousIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Play/Pause action
        val playPauseIntent = Intent(context, MusicPlayerService::class.java).apply {
            action = ACTION_PLAY_PAUSE
        }
        val playPausePendingIntent = PendingIntent.getService(
            context,
            0,
            playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Next action
        val nextIntent = Intent(context, MusicPlayerService::class.java).apply {
            action = ACTION_NEXT
        }
        val nextPendingIntent = PendingIntent.getService(
            context,
            3,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Stop action
        val stopIntent = Intent(context, MusicPlayerService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            context,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Determine play/pause icon and text
        val playPauseIcon = if (isPlaying) {
            android.R.drawable.ic_media_pause
        } else {
            android.R.drawable.ic_media_play
        }
        val playPauseText = if (isPlaying) "Pause" else "Play"

        // Format time for subtext
        val positionText = formatTime(position)
        val durationText = formatTime(duration)
        val timeText = if (duration > 0) "$positionText / $durationText" else artist

        // Build the notification
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(trackName)
            .setContentText(timeText)
            .setSubText(artist)
            .setSmallIcon(android.R.drawable.ic_media_play) // Use system icon
            .setContentIntent(contentPendingIntent)
            .setOngoing(isPlaying) // Only ongoing when playing
            .setOnlyAlertOnce(true) // Only alert once when notification is first shown
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            // Add progress bar
            .setProgress(
                if (duration > 0) duration.toInt() else 100,
                if (duration > 0) position.toInt() else 0,
                duration == 0L // Indeterminate if no duration
            )
            // Add media style for better media controls
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2) // Show previous, play/pause, next in compact view
            )
            // Add action buttons
            .addAction(android.R.drawable.ic_media_previous, "Previous", previousPendingIntent)
            .addAction(playPauseIcon, playPauseText, playPausePendingIntent)
            .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    /**
     * Formats milliseconds to MM:SS format.
     */
    private fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    /**
     * Updates an existing notification.
     */
    fun updateNotification(notification: Notification) {
        // Check permission for Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(NOTIFICATION_ID, notification)
            }
        } else {
            // No permission check needed for Android 12 and below
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    /**
     * Cancels the notification.
     */
    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }
}

