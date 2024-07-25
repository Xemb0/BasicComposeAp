package com.autobot.watchparty.mainscreens

import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import com.autobot.watchparty.customviews.UserView
import com.autobot.watchparty.database.UserData
import com.autobot.watchparty.exoplayer.ExoPlayerScreen
import com.autobot.watchparty.database.viewmodels.MainViewModel
import com.autobot.watchparty.database.viewmodels.PlayerViewModel
import com.launcher.arclauncher.compose.theme.MyAppThemeColors
@OptIn(UnstableApi::class)
@Composable
fun ScreenRoom(roomId: String, userData: UserData, onExit: () -> Unit, onUpload: () -> Unit) {
    var isPopupVisible by remember { mutableStateOf(false) }
    var lastKnownTimestamp: Long = -1L
    val viewModel = hiltViewModel<MainViewModel>()
    val playerViewModel = hiltViewModel<PlayerViewModel>()
    val users by viewModel.users.collectAsState()
    val currentPlayback by viewModel.currentPlayback.collectAsState()
    var showConfirmExitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(roomId) {
        viewModel.listenForRoomUsers(roomId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Ensure there's enough space for the floating button
        ) {
            ExoPlayerScreen(playerViewModel, roomId)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MyAppThemeColors.current.tertiary)
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    LazyRow(modifier = Modifier.weight(1f)) {
                        items(users) { item ->
                            UserView(
                                userData = item,
                                isStreaming = false,
                                onClick = { viewModel.toggleStreaming() }
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { isPopupVisible = true },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .background(MyAppThemeColors.current.primary)
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add User")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Ensure this occupies space, so the floating button appears correctly
            ScreenUpload()

            Spacer(modifier = Modifier.height(16.dp))

            VideoListScreen(onSelectVideo = { uri ->
                playerViewModel.addVideoUri(uri)
            })
        }

        // Floating Upload Button
        FloatingActionButton(
            onClick = { onUpload() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MyAppThemeColors.current.primary
        ) {

            Icon(imageVector = Icons.Default.Share, contentDescription = "Upload")
        }

        if (isPopupVisible) {
            AlertDialog(
                onDismissRequest = { isPopupVisible = false },
                title = { Text(text = "Room Id") },
                text = {
                    Column {
                        Text(text = "Room Id : $roomId")
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            isPopupVisible = false
                            viewModel.joinRoom(roomId) { exists ->
                                if (exists) {
                                    viewModel.listenForRoomUsers(roomId)
                                }
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Copy")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("OK")
                    }
                },
                containerColor = Color.White,
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, Color.Gray, RoundedCornerShape(24.dp))
            )
        }

        if (showConfirmExitDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Confirm Exit") },
                text = { Text("Are you sure you want to exit the Watch Party?") },
                confirmButton = {
                    TextButton(onClick = {
                        showConfirmExitDialog = false
                        viewModel.exitRoom(roomId, userData.userId)
                        onExit()
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmExitDialog = false }) {
                        Text("No")
                    }
                }
            )
        }
    }

    BackHandler {
        showConfirmExitDialog = true
    }
}
