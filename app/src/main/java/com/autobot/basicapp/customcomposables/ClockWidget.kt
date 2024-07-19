//package com.launcher.arclauncher.compose.customcomposables
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.launcher.arclauncher.compose.theme.MyAppThemeColors
//import com.launcher.arclauncher.database.viewmodels.SettingsViewModel
//import kotlinx.coroutines.delay
//import java.text.SimpleDateFormat
//import java.util.*
//
//@Composable
//fun ClockWidget(settingsViewModel: SettingsViewModel) {
//    val toggleArcClock by settingsViewModel.arcClock.collectAsStateWithLifecycle()
//    val toggleArcDate by settingsViewModel.arcDate.collectAsStateWithLifecycle()
//
//    var currentTime by remember { mutableStateOf(getCurrentTime()) }
//    var currentDate by remember { mutableStateOf(getCurrentDate()) }
//
//    LaunchedEffect(Unit) {
//        while (true) {
//            delay(1000L)
//            currentTime = getCurrentTime()
//            currentDate = getCurrentDate()
//        }
//    }
//
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            if (toggleArcClock) {
//                Text(
//                    text = currentTime,
//                    fontSize = 48.sp,
//                    modifier = Modifier.padding(16.dp),
//                    color = MyAppThemeColors.current.primary
//                )
//            }
//            if (toggleArcDate) {
//                Text(
//                    text = currentDate,
//                    fontSize = 24.sp,
//                    modifier = Modifier.padding(8.dp),
//                            color = MyAppThemeColors.current.primary
//                )
//            }
//        }
//    }
//}
//
//fun getCurrentTime(): String {
//    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
//    return dateFormat.format(Date())
//}
//
//fun getCurrentDate(): String {
//    val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
//    return dateFormat.format(Date())
//}
