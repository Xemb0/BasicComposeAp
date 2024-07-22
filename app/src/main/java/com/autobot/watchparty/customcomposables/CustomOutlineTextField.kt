package com.autobot.watchparty.customcomposables
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.launcher.arclauncher.compose.theme.MyAppThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    labelColor: Color, // Added parameter for label color
    focusBorderColor: Color
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(.9f)
            .padding(bottom = 8.dp),
        value = value,
        onValueChange = onValueChange,
        label = label,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = focusBorderColor, // Change this to the desired color
            unfocusedBorderColor = Color.Gray, // Change this to the desired color
            disabledBorderColor = Color.LightGray, // Change this to the desired color
            cursorColor = MyAppThemeColors.current.primary
        )
    )
}