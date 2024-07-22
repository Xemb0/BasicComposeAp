package com.autobot.watchparty

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.autobot.watchparty.signin.UserData
import com.launcher.arclauncher.compose.theme.MyAppThemeSizes

@Composable
fun ScreenCreateRoom(
    userData: UserData?,
    onSignOut: () -> Unit,
    onCreateRoom: (String) -> Unit,
    onJoinRoom: () -> Unit
) {
    Row (
        modifier = Modifier.fillMaxSize().padding(MyAppThemeSizes.current.large),
        horizontalArrangement = Arrangement.End,
    ){
        Button(onClick = onSignOut) {
            Text(text = "Sign out")
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(userData?.profilePictureUrl != null) {
            AsyncImage(
                model = userData.profilePictureUrl,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        if(userData?.username != null) {
            Text(
                text = userData.username,
                textAlign = TextAlign.Center,
                fontSize = 36.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(onClick = onJoinRoom) {
            Text(text = "Join Party")
        }

        Button(onClick = {
            onCreateRoom(System.currentTimeMillis().toString())
        }) {
            Text(text = "Host New Party")
        }



    }
}