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
        // Initialize tracks with real songs
        _tracks.value = listOf(
            Track(
                id = 1,
                name = "Lemon",
                artist = "Kenshi Yonezu",
                album = "Lemon",
                uri = "android.resource://com.example.backgroundmusicplayer/${R.raw.lemon}"
            ),
            Track(
                id = 2,
                name = "Re：member",
                artist = "Aimyon",
                album = "Aimyon",
                uri = "android.resource://com.example.backgroundmusicplayer/${R.raw.remember}"
            ),
            Track(
                id = 3,
                name = "I Can't Stop the Loneliness",
                artist = "Various Artists",
                album = "Single",
                uri = "android.resource://com.example.backgroundmusicplayer/${R.raw.loneliness}"
            ),
            Track(
                id = 4,
                name = "カレイドスコープ",
                artist = "Deco*27",
                album = "GHOST",
                uri = "android.resource://com.example.backgroundmusicplayer/${R.raw.kaleidoscope}"
            ),
            Track(
                id = 5,
                name = "I Really Want to Stay at Your House",
                artist = "Rosa Walton",
                album = "Cyberpunk 2077",
                uri = "android.resource://com.example.backgroundmusicplayer/${R.raw.stay_at_your_house}"
            ),
            Track(
                id = 6,
                name = "EA7众选格斗",
                artist = "精彩鹤轩",
                album = "TikTok Song",
                uri = "android.resource://com.example.backgroundmusicplayer/${R.raw.ea7_tiktok}"
            ),
            Track(
                id = 7,
                name = "ルカルカ★ナイトフィーバー",
                artist = "DECO*27",
                album = "2024 Live",
                uri = "android.resource://com.example.backgroundmusicplayer/${R.raw.luka_luka_night_fever}"
            ),
            Track(
                id = 8,
                name = "メランコリック",
                artist = "Junky",
                album = "2024 Live",
                uri = "android.resource://com.example.backgroundmusicplayer/${R.raw.melancholic}"
            ),
            Track(
                id = 9,
                name = "カンタレラ",
                artist = "WhiteFlame feat. Hatsune Miku & KAITO",
                album = "2024 Live",
                uri = "android.resource://com.example.backgroundmusicplayer/${R.raw.cantarella}"
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

