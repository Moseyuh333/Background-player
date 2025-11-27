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
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ (API 33+): Use granular media permissions
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            // Android 6.0 to 12 (API 23-32): Use READ_EXTERNAL_STORAGE
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED -> {
                Log.d(TAG, "Permission already granted")
            }
            shouldShowRequestPermissionRationale(permission) -> {
                // Show explanation to user
                Toast.makeText(
                    this,
                    "Permission needed to access audio files",
                    Toast.LENGTH_LONG
                ).show()
                permissionLauncher.launch(permission)
            }
            else -> {
                // Request permission
                permissionLauncher.launch(permission)
            }
        }
    }

    /**
     * Setup click listeners for track selection buttons.
     */
    private fun setupTrackButtons() {
        lifecycleScope.launch {
            viewModel.tracks.collect { tracks ->
                if (tracks.isNotEmpty()) {
                    // Setup track buttons
                    binding.btnTrack1.text = tracks.getOrNull(0)?.name ?: "Track 1"
                    binding.btnTrack2.text = tracks.getOrNull(1)?.name ?: "Track 2"
                    binding.btnTrack3.text = tracks.getOrNull(2)?.name ?: "Track 3"

                    binding.btnTrack1.setOnClickListener {
                        tracks.getOrNull(0)?.let { playTrack(it) }
                    }
                    binding.btnTrack2.setOnClickListener {
                        tracks.getOrNull(1)?.let { playTrack(it) }
                    }
                    binding.btnTrack3.setOnClickListener {
                        tracks.getOrNull(2)?.let { playTrack(it) }
                    }
                }
            }
        }
    }

    /**
     * Setup playback control buttons (Play/Pause/Stop).
     */
    private fun setupControlButtons() {
        binding.btnPlay.setOnClickListener {
            musicService?.play()
        }

        binding.btnPause.setOnClickListener {
            musicService?.pause()
        }

        binding.btnStop.setOnClickListener {
            musicService?.stop()
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
        Log.d(TAG, "Playing track: ${track.name}")
        viewModel.selectTrack(track)

        // Start service with track information
        val intent = Intent(this, MusicPlayerService::class.java).apply {
            putExtra(MusicPlayerService.EXTRA_TRACK_URI, track.uri)
            putExtra(MusicPlayerService.EXTRA_TRACK_NAME, track.name)
            putExtra(MusicPlayerService.EXTRA_ARTIST, track.artist)
        }

        // Start service (will become foreground service)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }

        // Bind to service if not already bound
        if (!isServiceBound) {
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
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
                binding.tvStatus.text = "Playing"
                binding.tvCurrentTrack.text = state.trackName
                binding.btnPlay.isEnabled = false
                binding.btnPause.isEnabled = true
                binding.btnStop.isEnabled = true
                updateSeekBar(state.position, state.duration)
            }
            is PlaybackState.Paused -> {
                binding.tvStatus.text = "Paused"
                binding.tvCurrentTrack.text = state.trackName
                binding.btnPlay.isEnabled = true
                binding.btnPause.isEnabled = false
                binding.btnStop.isEnabled = true
                updateSeekBar(state.position, state.duration)
            }
            is PlaybackState.Stopped -> {
                binding.tvStatus.text = "Stopped"
                binding.btnPlay.isEnabled = false
                binding.btnPause.isEnabled = false
                binding.btnStop.isEnabled = false
                binding.seekBar.progress = 0
                binding.tvPosition.text = "00:00"
                binding.tvDuration.text = "00:00"
            }
            is PlaybackState.Preparing -> {
                binding.tvStatus.text = "Loading..."
                binding.tvCurrentTrack.text = state.trackName
                binding.btnPlay.isEnabled = false
                binding.btnPause.isEnabled = false
                binding.btnStop.isEnabled = true
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

