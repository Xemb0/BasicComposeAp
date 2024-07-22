package com.autobot.watchparty.customcomposables
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RoundedOutlineButton(
    modifier: Modifier = Modifier,
    label: String,
    onClick: () -> Unit,
    outlineColor: Color = Color.Black,
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
        ), // No elevation for a flat look
    )
    {
        Text(
            text = label,
            color = outlineColor,
        )
    }
}

@Preview
@Composable
fun RoundedButtonOutlinePreview() {
    RoundedOutlineButton(
        label = "Outline Button",
        onClick = { /* Handle button click */ },
        outlineColor = Color.Black // Specify the outline color
    )
}
