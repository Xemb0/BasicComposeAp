package com.autobot.basicapp

import ProfileImage
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.ui.PlayerView
import com.autobot.basicapp.customcomposables.UserView
import com.autobot.basicapp.exoplayer.MainViewModel
import com.autobot.basicapp.popups.PopupLink
import com.autobot.basicapp.signin.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.launcher.arclauncher.compose.theme.MyAppThemeColors

@Composable
fun ScreenRoom(roomId: String, userData: UserData) {
    var isPopupVisible by remember { mutableStateOf(false) }

    val viewModel = hiltViewModel<MainViewModel>()
    val users by viewModel.users.collectAsState()
    val videoItems by viewModel.videoItems.collectAsState()
    val selectVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let(viewModel::addVideoUri)
        }
    )
    var lifecycle by remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MyAppThemeColors.current.tertiary)
                .padding(8.dp)
        ) {
            // Align the UserView to the start of the Box
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart) // Align UserView to start
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                UserView(
                    userData = userData,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.toggleStreaming() }
                )

                Spacer(modifier = Modifier.width(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(users) { item ->
                        UserView(
                            userData = item,
                            isStreaming = false,
                            onClick = { viewModel.toggleStreaming() }
                        )
                    }
                }
            }

            // Position the Add User icon at the top right
            IconButton(
                onClick = { isPopupVisible = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .background(MyAppThemeColors.current.primary)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add User"
                )
            }
        }

        AndroidView(
            factory = { context ->
                PlayerView(context).also {
                    it.player = viewModel.player
                }
            },
            update = {
                when (lifecycle) {
                    Lifecycle.Event.ON_PAUSE -> {
                        it.onPause()
                        it.player?.pause()
                    }
                    Lifecycle.Event.ON_RESUME -> {
                        it.onResume()
                    }
                    else -> Unit
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        IconButton(onClick = {
            selectVideoLauncher.launch("video/mp4")
        }) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Select video"
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(videoItems) { item ->
                Text(
                    text = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.playVideo(item.contentUri)
                        }
                        .padding(16.dp)
                )
            }
        }
        if (isPopupVisible) {
            AlertDialog(
                onDismissRequest = { isPopupVisible = false },
                title = {
                    Text(text = "Room Id")
                },
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
                            viewModel.joinRoom(roomId, userData) { exists ->
                                if (exists) {
                                    viewModel.listenForRoomUsers(roomId)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "copy"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ok")
                    }
                },
                containerColor = Color.White,
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, Color.Gray, RoundedCornerShape(24.dp))
            )
        }
    }
}
