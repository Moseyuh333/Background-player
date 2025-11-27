package com.example.backgroundmusicplayer.model

/**
 * Represents the current playback state of the music player.
 * Using sealed class for type-safe state management.
 */
sealed class PlaybackState {
    /**
     * No track is loaded or playing.
     */
    object Stopped : PlaybackState()

    /**
     * A track is currently playing.
     * @param trackName Name of the currently playing track
     * @param position Current playback position in milliseconds
     * @param duration Total track duration in milliseconds
     * @param trackIndex Current track index in playlist
     */
    data class Playing(
        val trackName: String,
        val position: Long = 0L,
        val duration: Long = 0L,
        val trackIndex: Int = 0
    ) : PlaybackState()

    /**
     * Playback is paused.
     * @param trackName Name of the paused track
     * @param position Current playback position in milliseconds
     * @param duration Total track duration in milliseconds
     * @param trackIndex Current track index in playlist
     */
    data class Paused(
        val trackName: String,
        val position: Long = 0L,
        val duration: Long = 0L,
        val trackIndex: Int = 0
    ) : PlaybackState()

    /**
     * Player is preparing/buffering the track.
     * @param trackName Name of the track being prepared
     */
    data class Preparing(val trackName: String) : PlaybackState()
}

/**
 * Data class representing a music track.
 */
data class Track(
    val id: Int,
    val name: String,
    val artist: String = "Unknown Artist",
    val album: String = "Unknown Album",
    val uri: String // Can be raw resource URI, file path, or content URI
)

