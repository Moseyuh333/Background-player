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
     * @return Notification object ready to be displayed
     */
    fun buildNotification(
        trackName: String,
        artist: String = "Unknown Artist",
        isPlaying: Boolean
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

        // Build the notification
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(trackName)
            .setContentText(artist)
            .setSmallIcon(android.R.drawable.ic_media_play) // Use system icon
            .setContentIntent(contentPendingIntent)
            .setOngoing(true) // Cannot be dismissed by user while playing
            .setOnlyAlertOnce(true) // Only alert once when notification is first shown
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            // Add media style for better media controls
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1) // Show play/pause and stop in compact view
            )
            // Add action buttons
            .addAction(playPauseIcon, playPauseText, playPausePendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    /**
     * Updates an existing notification.
     */
    fun updateNotification(notification: Notification) {
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    /**
     * Cancels the notification.
     */
    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }
}

