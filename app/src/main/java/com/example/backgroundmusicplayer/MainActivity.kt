package com.example.backgroundmusicplayer

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import com.example.backgroundmusicplayer.databinding.ActivityMainBinding
import com.example.backgroundmusicplayer.model.PlaybackState
import com.example.backgroundmusicplayer.model.Track
import com.example.backgroundmusicplayer.service.MusicPlayerService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Main Activity that provides UI for controlling music playback.
 *
 * Features:
 * - Request runtime permissions for audio file access
 * - Display list of demo tracks
 * - Bind to MusicPlayerService for playback control
 * - Show current playback state (Playing/Paused/Stopped)
 * - Provide Play/Pause/Stop controls
 * - Display and control seek position
 *
 * Lifecycle considerations:
 * - Screen rotation: ViewModel preserves state, service continues playing
 * - App backgrounded (HOME): Service continues, Activity pauses
 * - Back pressed: Activity destroyed, service continues if playing
 * - Service binding: onStart() binds, onStop() unbinds
 */
@UnstableApi
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    // Service binding
    private var musicService: MusicPlayerService? = null
    private var isServiceBound = false

    // ServiceConnection for binding to MusicPlayerService
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "Service connected")
            val binder = service as MusicPlayerService.MusicPlayerBinder
            musicService = binder.getService()
            isServiceBound = true
            viewModel.setServiceBound(true)

            // Observe playback state from service
            observePlaybackState()

            // Start periodic position updates
            startPositionUpdates()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "Service disconnected")
            musicService = null
            isServiceBound = false
            viewModel.setServiceBound(false)
        }
    }

    // Permission launcher for requesting audio access
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                this,
                "Permission denied. Cannot access audio files from storage.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate()")

        // Request permissions
        checkAndRequestPermissions()

        // Setup UI
        setupTrackButtons()
        setupControlButtons()
        setupSeekBar()

        // Observe ViewModel state
        observeViewModel()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() - Binding to service")

        // Bind to service if it's running
        // If service is not running, this will not start it
        val intent = Intent(this, MusicPlayerService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() - Unbinding from service")

        // Unbind from service
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
            viewModel.setServiceBound(false)
        }
    }

    /**
     * Check and request necessary permissions based on Android version.
     */
    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()

        // Add media permission based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ (API 33+): Use granular media permissions
            permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
            // Also need POST_NOTIFICATIONS for Android 13+
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // Android 6.0 to 12 (API 23-32): Use READ_EXTERNAL_STORAGE
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        // Check which permissions are not granted
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isEmpty()) {
            Log.d(TAG, "All permissions already granted")
        } else {
            // Request the first missing permission
            // In a production app, you might want to request all at once
            permissionLauncher.launch(permissionsToRequest.first())
        }
    }

    /**
     * Setup scrollable track list with LinearLayout.
     */
    private fun setupTrackButtons() {
        // Observe tracks from ViewModel and dynamically add views
        lifecycleScope.launch {
            try {
                viewModel.tracks.collect { tracks ->
                    Log.d(TAG, "Tracks collected: ${tracks.size} tracks")

                    // Clear existing views on main thread
                    runOnUiThread {
                        try {
                            binding.linearLayoutTracks.removeAllViews()

                            tracks.forEachIndexed { index, track ->
                                try {
                                    // Create a simple card for each track
                                    val cardView = com.google.android.material.card.MaterialCardView(this@MainActivity).apply {
                                        layoutParams = android.widget.LinearLayout.LayoutParams(
                                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                                        ).apply {
                                            setMargins(16, 8, 16, 8)
                                        }
                                        radius = 12f
                                        cardElevation = 4f
                                        setContentPadding(32, 24, 32, 24)
                                        isClickable = true
                                        isFocusable = true
                                    }

                                    // Create text view for track info
                                    val textView = android.widget.TextView(this@MainActivity).apply {
                                        text = "${index + 1}. ${track.name}\n${track.artist}"
                                        textSize = 16f
                                        setTextColor(getColor(android.R.color.black))
                                    }

                                    cardView.addView(textView)

                                    // Set click listener
                                    cardView.setOnClickListener {
                                        Log.d(TAG, "Track clicked: ${track.name}")
                                        playTrack(track)
                                    }

                                    // Add to container
                                    binding.linearLayoutTracks.addView(cardView)

                                } catch (e: Exception) {
                                    Log.e(TAG, "Error creating view for track: ${track.name}", e)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error updating track list UI", e)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up track list", e)
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error loading tracks: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * Setup playback control buttons (Play/Pause/Stop/Previous/Next/Loop).
     */
    private fun setupControlButtons() {
        binding.btnPrevious.setOnClickListener {
            musicService?.previous()
        }

        binding.btnPlay.setOnClickListener {
            musicService?.play()
        }

        binding.btnPause.setOnClickListener {
            musicService?.pause()
        }

        binding.btnNext.setOnClickListener {
            musicService?.next()
        }

        binding.btnStop.setOnClickListener {
            musicService?.stop()
        }

        binding.btnLoop.setOnClickListener {
            musicService?.let { service ->
                // Cycle loop mode: 0 -> 1 -> 2 -> 0
                val newLoopMode = service.cycleLoopMode()
                updateLoopButton(newLoopMode)

                // Show feedback to user
                val message = when (newLoopMode) {
                    1 -> "🔁 Loop current track: 1 time"
                    2 -> "🔁 Loop current track: 2 times"
                    else -> "🔁 Loop: OFF"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Update loop button appearance based on loop mode.
     */
    private fun updateLoopButton(loopMode: Int) {
        when (loopMode) {
            1 -> {
                binding.btnLoop.text = "🔁 1x"
                binding.btnLoop.strokeColor = getColorStateList(android.R.color.holo_blue_light)
            }
            2 -> {
                binding.btnLoop.text = "🔁 2x"
                binding.btnLoop.strokeColor = getColorStateList(android.R.color.holo_green_light)
            }
            else -> {
                binding.btnLoop.text = "🔁 OFF"
                binding.btnLoop.strokeColor = getColorStateList(android.R.color.darker_gray)
            }
        }
    }

    /**
     * Setup seek bar for track position control.
     */
    private fun setupSeekBar() {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicService?.seekTo(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    /**
     * Play a selected track by starting the service.
     */
    private fun playTrack(track: Track) {
        try {
            Log.d(TAG, "Playing track: ${track.name}")
            Toast.makeText(this, "Playing: ${track.name}", Toast.LENGTH_SHORT).show()

            viewModel.selectTrack(track)

            // Get all tracks and create playlist
            val allTracks = viewModel.tracks.value
            val trackIndex = allTracks.indexOf(track)

            // Convert tracks to service TrackInfo format
            val playlist = allTracks.map { t ->
                MusicPlayerService.TrackInfo(
                    uri = t.uri,
                    name = t.name,
                    artist = t.artist
                )
            }

            // Start service with track information
            val intent = Intent(this, MusicPlayerService::class.java).apply {
                putExtra(MusicPlayerService.EXTRA_TRACK_URI, track.uri)
                putExtra(MusicPlayerService.EXTRA_TRACK_NAME, track.name)
                putExtra(MusicPlayerService.EXTRA_ARTIST, track.artist)
            }

            // Start service (will become foreground service)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
                Log.d(TAG, "Service started successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error starting service", e)
                Toast.makeText(this, "Error starting playback service", Toast.LENGTH_LONG).show()
                return
            }

            // Set playlist in service after binding
            lifecycleScope.launch {
                delay(500) // Wait for service to be bound
                musicService?.setPlaylist(playlist, trackIndex)
                Log.d(TAG, "Playlist set with ${playlist.size} tracks, starting at index $trackIndex")
            }

            // Bind to service if not already bound
            if (!isServiceBound) {
                try {
                    bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
                    Log.d(TAG, "Binding to service")
                } catch (e: Exception) {
                    Log.e(TAG, "Error binding to service", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in playTrack", e)
            Toast.makeText(this, "Error playing track: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Observe playback state from service.
     */
    private fun observePlaybackState() {
        lifecycleScope.launch {
            musicService?.playbackState?.collect { state ->
                Log.d(TAG, "Playback state: $state")
                updateUI(state)
            }
        }
    }

    /**
     * Observe ViewModel state changes.
     */
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.selectedTrack.collect { track ->
                binding.tvCurrentTrack.text = track?.name ?: "No track selected"
            }
        }
    }

    /**
     * Update UI based on playback state.
     */
    private fun updateUI(state: PlaybackState) {
        when (state) {
            is PlaybackState.Playing -> {
                binding.tvStatus.text = "▶ Now Playing"
                binding.tvCurrentTrack.text = state.trackName
                binding.btnPlay.isEnabled = false
                binding.btnPause.isEnabled = true
                binding.btnStop.isEnabled = true
                binding.btnPrevious.isEnabled = true
                binding.btnNext.isEnabled = true
                binding.btnLoop.isEnabled = true
                updateSeekBar(state.position, state.duration)
            }
            is PlaybackState.Paused -> {
                binding.tvStatus.text = "⏸ Paused"
                binding.tvCurrentTrack.text = state.trackName
                binding.btnPlay.isEnabled = true
                binding.btnPause.isEnabled = false
                binding.btnStop.isEnabled = true
                binding.btnPrevious.isEnabled = true
                binding.btnNext.isEnabled = true
                binding.btnLoop.isEnabled = true
                updateSeekBar(state.position, state.duration)
            }
            is PlaybackState.Stopped -> {
                binding.tvStatus.text = "■ Stopped"
                binding.tvCurrentTrack.text = "No track selected"
                binding.btnPlay.isEnabled = false
                binding.btnPause.isEnabled = false
                binding.btnStop.isEnabled = false
                binding.btnPrevious.isEnabled = false
                binding.btnNext.isEnabled = false
                binding.btnLoop.isEnabled = false
                binding.seekBar.progress = 0
                binding.tvPosition.text = "00:00"
                binding.tvDuration.text = "00:00"
            }
            is PlaybackState.Preparing -> {
                binding.tvStatus.text = "⏳ Loading..."
                binding.tvCurrentTrack.text = state.trackName
                binding.btnPlay.isEnabled = false
                binding.btnPause.isEnabled = false
                binding.btnStop.isEnabled = true
                binding.btnPrevious.isEnabled = true
                binding.btnNext.isEnabled = true
                binding.btnLoop.isEnabled = true
            }
        }
    }

    /**
     * Update seek bar position and time labels.
     */
    private fun updateSeekBar(position: Long, duration: Long) {
        if (duration > 0) {
            binding.seekBar.max = duration.toInt()
            binding.seekBar.progress = position.toInt()
            binding.tvPosition.text = formatTime(position)
            binding.tvDuration.text = formatTime(duration)
        }
    }

    /**
     * Start periodic updates of playback position.
     */
    private fun startPositionUpdates() {
        lifecycleScope.launch {
            while (isServiceBound) {
                musicService?.let { service ->
                    if (service.isPlaying()) {
                        val position = service.getCurrentPosition()
                        val duration = service.getDuration()
                        updateSeekBar(position, duration)
                    }
                }
                delay(500) // Update every 500ms
            }
        }
    }

    /**
     * Format time in milliseconds to MM:SS format.
     */
    private fun formatTime(timeMs: Long): String {
        val totalSeconds = timeMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}

