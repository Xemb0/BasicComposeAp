package com.launcher.arclauncher.compose.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MyAppColors(
//    val background: Color,
//    val onBackground: Color,
    val primary: Color,
//    val onPrimary: Color,
    val secondary: Color,
//    val onSecondary: Color,
    val tertiary: Color,

    val myText: Color
//    val onTertiary: Color,
)
data class MyAppTypography(
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,
    val body: TextStyle,
    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle
)

data class MyAppShapes(
    val small: Shape,
    val medium: Shape,
    val large: Shape,
    val container: Shape,
    val button: Shape,
)
data class MyAppSizes(
    val xsmall:Dp,
    val small: Dp,
    val medium: Dp,
    val large: Dp,
    val xlarge: Dp,
    val xxlarge: Dp,)

data class MyAppTypo(
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,
    val body: TextStyle,
    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle,
)

 val MyAppThemeColors = staticCompositionLocalOf {
     MyAppColors(

         primary = Color(0xff09d383),
//         onPrimary = Color(0xFFFFFFFF),
         secondary = Color(0xFFee272b),
//         onSecondary = Color(0xFF0A0C19),
         tertiary = Color(0xFFD7D7D7),
//         onTertiary = Color(0xFF0A0C19),
         myText = Color(0xFF0A0C19),
     )
 }

val MyAppThemeShapes = staticCompositionLocalOf {
    MyAppShapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(8.dp),
        large = RoundedCornerShape(16.dp),
        container = RectangleShape,
        button = CircleShape,
    )
}
val MyAppThemeSizes = staticCompositionLocalOf {
    MyAppSizes(
        small = 4.dp,
        medium = 8.dp,
        large = 16.dp,
        xsmall = 2.dp,
        xlarge = 32.dp,
        xxlarge = 64.dp,
    )
}
    val MyAppThemeTypo = staticCompositionLocalOf {
        MyAppTypo(
            titleLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            ),
            titleMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            titleSmall = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            body = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            ),
            labelLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            ),
            labelMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            ),
            labelSmall = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp
            ),
        )
    }

