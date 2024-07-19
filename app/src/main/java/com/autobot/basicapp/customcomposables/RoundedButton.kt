package com.autobot.basicapp.customcomposables
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.launcher.arclauncher.compose.theme.MyAppThemeColors
import com.launcher.arclauncher.compose.theme.MyAppThemeComposable
import com.launcher.arclauncher.compose.theme.MyAppThemeShapes

@Composable
fun RoundedButton(
    modifier: Modifier = Modifier,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        ),
        shape = MyAppThemeShapes.current.large
    ) {
        Text(
            text = label,
            color = MyAppThemeColors.current.tertiary
        )
    }
}

@Preview
@Composable
fun RoundedButtonPreview() {
    MyAppThemeComposable {
        RoundedButton(
            label = "Click me",
            color = Color.Blue,
            onClick = { /*TODO*/ }
        )
    }
}
