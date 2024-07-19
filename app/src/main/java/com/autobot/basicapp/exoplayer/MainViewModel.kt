package com.autobot.basicapp.exoplayer

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.autobot.basicapp.signin.UserData
import com.google.firebase.auth.FirebaseAuth
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

    private val _users = MutableStateFlow<List<UserData>>(emptyList())
    val users: StateFlow<List<UserData>> = _users

    private val videoUris = savedStateHandle.getStateFlow("videoUris", emptyList<Uri>())

    val videoItems = videoUris.map { uris ->
        uris.map { uri ->
            VideoItem(
                contentUri = uri,
                mediaItem = MediaItem.fromUri(uri),
                name = metaDataReader.getMetaDataFromUri(uri)?.fileName ?: "No name"
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        player.prepare()
    }

    fun addVideoUri(uri: Uri) {
        savedStateHandle["videoUris"] = videoUris.value + uri
        player.addMediaItem(MediaItem.fromUri(uri))
    }

    fun playVideo(uri: Uri) {
        player.setMediaItem(
            videoItems.value.find { it.contentUri == uri }?.mediaItem ?: return
        )
    }

    fun createRoom(roomId: String) {
        val currentUser = auth.currentUser ?: return
        val user = mapOf(
            "uid" to currentUser.uid,
            "name" to (currentUser.displayName ?: "Unknown"),
            "image" to currentUser.photoUrl.toString(),
            "email" to (currentUser.email ?: "Unknown")
        )

        viewModelScope.launch {
            repository.createRoom(roomId, user) { success ->
                if (success) {
                    listenForRoomUsers(roomId)
                }
            }
        }
    }

    fun joinRoom(roomId: String, userData: UserData, callback: (Boolean) -> Unit) {
        val currentUser = auth.currentUser ?: return

        // Create the user data map
        val userMap = mapOf(
            "uid" to userData.userId,
            "name" to userData.username,
            "image" to userData.profilePictureUrl
        )

        // Use repository to handle Firestore operations
        repository.joinRoom(roomId, userMap) { success ->
            if (success) {
                callback(true)
                listenForRoomUsers(roomId)
            } else {
                callback(false)
            }
        }
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
