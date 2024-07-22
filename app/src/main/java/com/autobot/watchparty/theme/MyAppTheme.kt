package com.launcher.arclauncher.compose.theme
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp

private val DarkColorScheme = MyAppColors(

    primary = myPrimaryDark,
    secondary = mySecodaryDark,
    tertiary = mytertiaryDark,
    myText = myTextDark
)

private val LightColorScheme = MyAppColors(
    primary = myPrimary,
    secondary = mySecodary,
    tertiary = mytertiary,
    myText = myText
)

private val shape = MyAppShapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp),
    container = RoundedCornerShape(16.dp),
    button = RoundedCornerShape(50)
)

private val size = MyAppSizes(
    xsmall = 4.dp,
    small = 8.dp,
    medium = 16.dp,
    large = 32.dp,
    xlarge = 64.dp,
    xxlarge = 128.dp
)

private val typo = MyAppTypo(
    titleLarge = titleLarge,
    titleMedium = titleMedium,
    titleSmall = titleSmall,
    body = body,
    labelLarge = labelLarge,
    labelMedium = labelMedium,
    labelSmall = labelSmall
)




@Composable
fun MyAppThemeComposable(content: @Composable () -> Unit) {
    val darkTheme = isSystemInDarkTheme()
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val rippleIndication = rememberRipple()

    CompositionLocalProvider(
        MyAppThemeColors provides colorScheme,
        MyAppThemeShapes provides shape,
        MyAppThemeSizes provides size,
        LocalIndication provides rippleIndication,
        MyAppThemeTypo provides typo,
        content = content
    )
}


object MyAppTheme {
    val colors: MyAppColors
        @Composable
        get() = MyAppThemeColors.current
    val shapes: MyAppShapes
        @Composable
        get() = MyAppThemeShapes.current
    val sizes: MyAppSizes
        @Composable
        get() = MyAppThemeSizes.current
    val typography: MyAppTypo
        @Composable
        get() = MyAppThemeTypo.current
}
