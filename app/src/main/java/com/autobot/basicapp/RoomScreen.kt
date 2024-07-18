//package com.autobot.basicapp
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//
//@Composable
//fun RoomScreen(viewModel: RoomViewModel) {
//    var roomId by remember { mutableStateOf("") }
//
//    Column {
//        TextField(
//            value = roomId,
//            onValueChange = { roomId = it },
//            label = { Text("Room ID") }
//        )
//        Button(onClick = { viewModel.createRoom(roomId) }) {
//            Text("Create Room")
//        }
//        Button(onClick = { viewModel.joinRoom(roomId) }) {
//            Text("Join Room")
//        }
//    }
//}
