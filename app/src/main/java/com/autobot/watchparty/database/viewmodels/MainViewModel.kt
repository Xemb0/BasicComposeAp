package com.autobot.watchparty.database.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.autobot.watchparty.database.Playback
import com.autobot.watchparty.database.UserData
import com.autobot.watchparty.exoplayer.MetaDataReader
import com.autobot.watchparty.database.repsitories.RoomRepository
import com.autobot.watchparty.exoplayer.VideoItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    val player: Player,
    private val metaDataReader: MetaDataReader,
    private val repository: RoomRepository
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _currentPlayback = MutableStateFlow(Playback())
    val currentPlayback: StateFlow<Playback> = _currentPlayback
    private val _users = MutableStateFlow<List<UserData>>(emptyList())
    val users: StateFlow<List<UserData>> = _users
    fun createRoom(roomId: String) {
        val currentUser = auth.currentUser ?: return
        val user = UserData(
            currentUser.uid,currentUser.displayName,currentUser.photoUrl.toString()
        )

        viewModelScope.launch {
            repository.createRoom(roomId, user) { success ->
                if (success) {
                    listenForRoomUsers(roomId)
                }
            }
        }
    }

    fun joinRoom(roomId: String, callback: (Boolean) -> Unit) {
        val currentUser = auth.currentUser ?: run {
            Log.e("MainViewModel", "No current user found")
            callback(false)
            return
        }

        val user = UserData(
            currentUser.uid,
            currentUser.displayName ?: "",
            currentUser.photoUrl?.toString() ?: ""
        )

        viewModelScope.launch {
            repository.createRoom(roomId, user) { success ->
                if (success) {
                    Log.d("MainViewModel", "Successfully joined room: $roomId")
                    listenForRoomUsers(roomId)
                } else {
                    Log.d("MainViewModel", "Failed to join room: $roomId")
                }
                callback(success)
            }
        }
    }

    fun exitRoom(roomId: String,userId:String){
        repository.exitRoom(roomId,userId)
    }
    fun listenForRoomUsers(roomId: String) {
        repository.listenForRoomUsers(roomId) { userList ->
            _users.value = userList
        }
    }

    fun toggleStreaming() {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }


}
