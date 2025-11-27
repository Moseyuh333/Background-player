package com.example.backgroundmusicplayer

import androidx.lifecycle.ViewModel
import com.example.backgroundmusicplayer.model.PlaybackState
import com.example.backgroundmusicplayer.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for MainActivity.
 * Maintains UI state across configuration changes (like screen rotation).
 */
class MainViewModel : ViewModel() {

    // Available demo tracks (hardcoded for demo purposes)
    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    // Currently selected track
    private val _selectedTrack = MutableStateFlow<Track?>(null)
    val selectedTrack: StateFlow<Track?> = _selectedTrack.asStateFlow()

    // Service connection state
    private val _isServiceBound = MutableStateFlow(false)
    val isServiceBound: StateFlow<Boolean> = _isServiceBound.asStateFlow()

    init {
        // Initialize demo tracks
        // These URIs point to raw resources that will be created
        _tracks.value = listOf(
            Track(
                id = 1,
                name = "Demo Track 1",
                artist = "Demo Artist",
                album = "Demo Album",
                uri = "android.resource://com.example.backgroundmusicplayer/${R.raw.sample1}"
            ),
            Track(
                id = 2,
                name = "Demo Track 2",
                artist = "Demo Artist",
                album = "Demo Album",
                uri = "android.resource://com.example.backgroundmusicplayer/${R.raw.sample2}"
            ),
            Track(
                id = 3,
                name = "Demo Track 3",
                artist = "Demo Artist",
                album = "Demo Album",
                uri = "android.resource://com.example.backgroundmusicplayer/${R.raw.sample3}"
            )
        )
    }

    fun selectTrack(track: Track) {
        _selectedTrack.value = track
    }

    fun setServiceBound(bound: Boolean) {
        _isServiceBound.value = bound
    }
}

