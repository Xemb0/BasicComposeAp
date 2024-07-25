package com.autobot.watchparty.exoplayer

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.autobot.watchparty.database.Playback
import com.autobot.watchparty.database.viewmodels.PlayerViewModel
@Composable
fun ExoPlayerScreen(playerViewModel: PlayerViewModel = viewModel(), roomId: String) {
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    var lastKnownTimestamp by remember { mutableStateOf(-1L) }

    // Start listening for playback updates when the roomId changes
    LaunchedEffect(roomId) {
        playerViewModel.listenForPlayback(roomId)
    }

    // Observe playback position and URI from the ViewModel
    val playback by playerViewModel.currentPlayback.collectAsState()

    val context = LocalContext.current

    Column(modifier = Modifier) {

        AndroidView(
            factory = { context ->
                val playerView = PlayerView(context)
                playerView.player = playerViewModel.player

                playerViewModel.player.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        playerViewModel.updateIsPlaying(isPlaying)
                        println("ye to chlra hai")
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_READY) {
                        }
                    }

                    override fun onSeekForwardIncrementChanged(seekForwardIncrementMs: Long) {
                        playerViewModel.updateTimeStamp(playerViewModel.player.currentPosition)
                    }

                    override fun onMaxSeekToPreviousPositionChanged(maxSeekToPreviousPositionMs: Long) {
                        playerViewModel.updateTimeStamp(playerViewModel.player.currentPosition)
                    }
                })

                playerView
            },
            update = { playerView ->
                playerView.player?.apply {
                    playWhenReady = !playback.videoPaused
                    if (lastKnownTimestamp != playback.timestamp) {
                        seekTo(playback.timestamp)
                        lastKnownTimestamp = playback.timestamp
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            playerViewModel.player?.let { player ->
                playerViewModel.updatePlaybackState()
            }
        }) {
            Text("Sync")
        }
    }
}
