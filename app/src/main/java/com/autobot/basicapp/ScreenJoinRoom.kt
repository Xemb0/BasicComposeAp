package com.autobot.basicapp

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.autobot.basicapp.exoplayer.MainViewModel
import com.autobot.basicapp.signin.UserData
@Composable
fun ScreenJoinRoom(userData: UserData, onJoinPartyClick: (String) -> Unit, onCancelClick: () -> Unit) {
    var roomId by remember { mutableStateOf("") }
    var isInvalidRoom by remember { mutableStateOf(false) }
    val viewModel = hiltViewModel<MainViewModel>()

    AlertDialog(
        onDismissRequest = { onCancelClick() },
        title = {
            Text(text = "Join Room")
        },
        text = {
            Column {
                Text(text = "Enter Room ID:")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = roomId,
                    onValueChange = {
                        roomId = it
                        isInvalidRoom = false
                    },
                    isError = isInvalidRoom,
                    modifier = if (isInvalidRoom) {
                        Modifier.border(2.dp, Color.Red, RoundedCornerShape(8.dp))
                    } else {
                        Modifier
                    }
                )
                if (isInvalidRoom) {
                    Text(text = "Invalid Room ID", color = Color.Red)
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(
                    onClick = { onCancelClick() }
                ) {
                    Text(text = "Cancel")
                }
                TextButton(
                    onClick = {
                        viewModel.joinRoom(roomId, userData) { exists ->
                            if (exists) {
                                onJoinPartyClick(roomId)
                            } else {
                                isInvalidRoom = true
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Join"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Join")
                }
            }
        },
        containerColor = Color.White,
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .border(2.dp, Color.Gray, RoundedCornerShape(24.dp))
    )
}

