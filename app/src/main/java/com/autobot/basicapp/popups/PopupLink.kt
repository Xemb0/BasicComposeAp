package com.autobot.basicapp.popups

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

@Composable
fun PopupLink() {
    var isPopupVisible by remember { mutableStateOf(true) }
    var roomId by remember { mutableStateOf("43253") }

    val context = LocalContext.current

    if (isPopupVisible) {
        AlertDialog(
            onDismissRequest = { isPopupVisible = false },
            title = {
                Text(text = "Room Id")
            },
            text = {
                Column {
                    Text(text = "Enter the Room Id")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(value = roomId, onValueChange = { roomId = it })
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isPopupVisible = false
                        // Handle the room ID confirmation action here
                        // For example, you can print the room ID or call a function
                        println("Room ID: $roomId")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Confirm"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm")
                }
            },
            containerColor = Color.White,
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, Color.Gray, RoundedCornerShape(24.dp))
        )
    }
}
