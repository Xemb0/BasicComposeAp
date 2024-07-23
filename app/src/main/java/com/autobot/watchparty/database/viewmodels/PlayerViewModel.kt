package com.autobot.watchparty.database.viewmodels

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.autobot.watchparty.R
import com.autobot.watchparty.database.Movie
import com.autobot.watchparty.database.Playback
import com.autobot.watchparty.database.repsitories.PlayerRepository
import com.autobot.watchparty.exoplayer.MetaDataReader
import com.autobot.watchparty.exoplayer.VideoItem
import com.autobot.watchparty.workmanager.DownloadWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Thread.State
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerRepo: PlayerRepository,
    private val savedStateHandle: SavedStateHandle,
    val player: Player,
    private val metaDataReader: MetaDataReader
) : ViewModel() {

    private val currentUriState = savedStateHandle.getStateFlow("currentUri", Uri.EMPTY)
    private val _currentPlayback = MutableStateFlow(Playback())
    val currentPlayback: StateFlow<Playback> = _currentPlayback
    private val _currentMovie = MutableStateFlow<Movie?>(null)
    val currentMovie: StateFlow<Movie?> = _currentMovie

    init {
        player.prepare()
        val savedUri = savedStateHandle.get<Uri>("currentUri") ?: Uri.EMPTY
        val savedPlayback = savedStateHandle.get<Playback>("playbackState") ?: Playback()

        if (savedUri != Uri.EMPTY) {
            playVideo(savedUri)
            player.seekTo(savedPlayback.timestamp)
            player.playWhenReady = !savedPlayback.videoPaused
        }
    }

    fun addVideoUri(uri: Uri) {
        savedStateHandle["currentUri"] = uri
        val videoItem = VideoItem(
            contentUri = uri,
            mediaItem = MediaItem.fromUri(uri),
            name = metaDataReader.getMetaDataFromUri(uri)?.fileName ?: "No name"
        )
        player.addMediaItem(videoItem.mediaItem)
        playVideo(uri)
    }

    fun playVideo(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
    }

    fun toggleStreaming() {
        player.playWhenReady = !player.playWhenReady
        updatePlaybackState()
    }

    fun updatePlaybackState() {
        val playback = Playback(
            timestamp = player.currentPosition,
            videoPaused = !player.playWhenReady
        )
        _currentPlayback.value = playback
        savedStateHandle["playbackState"] = playback
        viewModelScope.launch {
            playerRepo.updatePlayback("roomId", playback)
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }


    fun updatePlayback(roomId: String,playback: Playback){
        viewModelScope.launch {
            playerRepo.updatePlayback(roomId,playback)
            listenForPlayback(roomId)
        }
    }

    fun listenForPlayback(roomId: String){
      viewModelScope.launch {
          playerRepo.listenForPlayback(roomId) {
            _currentPlayback.value = it
        }
      }
    }

    fun updateIsPlaying(playing: Boolean) {
        _currentPlayback.value = _currentPlayback.value.copy(videoPaused = !playing)
        viewModelScope.launch {
            playerRepo.updatePlayback("roomId",_currentPlayback.value)
        }

    }

    fun updateTimeStamp(seekForwardIncrementMs: Long) {
        _currentPlayback.value = _currentPlayback.value.copy(timestamp = seekForwardIncrementMs)
        viewModelScope.launch {
            playerRepo.updateTimestamp("roomId",seekForwardIncrementMs)
        }

    }

    fun updateMovieName(movieName: String) {
        _currentPlayback.value = _currentPlayback.value.copy(movieName = movieName)
        viewModelScope.launch {
            playerRepo.updateMovieName("roomId",movieName)
        }
    }



}
