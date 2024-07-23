package com.autobot.watchparty.customviews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.autobot.watchparty.R
import com.autobot.watchparty.database.UserData
import com.launcher.arclauncher.compose.theme.MyAppThemeColors
import com.launcher.arclauncher.compose.theme.MyAppThemeSizes

@Composable
fun UserView(
    userData: UserData,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isStreaming: Boolean = false
) {
    var streaming by remember { mutableStateOf(isStreaming) }

    Column(
        modifier = modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(24.dp))
            .padding(4.dp)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val painter: Painter =
            rememberAsyncImagePainter(
                ImageRequest.Builder
                (LocalContext.current).data(data = userData.profilePictureUrl).apply(block = fun ImageRequest.Builder.() {
                placeholder(R.drawable.ic_user_moive)
                error(R.drawable.ic_user_moive)
            }).build()
            )

        Image(
            painter = painter,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(48.dp)
                .clip(MaterialTheme.shapes.medium),
        )


        userData.username?.let {
            Text(text = it.take(6), color = MyAppThemeColors.current.myText, modifier = Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(MyAppThemeSizes.current.large))
                .background(if(streaming){
                MyAppThemeColors.current.secondary}else{
                MyAppThemeColors.current.primary})
                .padding(horizontal = 8.dp)
            )


        }



    }
}

@Preview
@Composable
fun UserViewPrev() {
    UserView(
        userData = UserData("","Userrs",""),
        onClick = {  },
        isStreaming = false
    )
}
