package com.autobot.basicapp

import android.net.Uri
import androidx.compose.runtime.Composable

@Composable
fun UploadScreen(onUpload: (Uri) -> Unit) {
    // UI for selecting and uploading movie file
}

@Composable
fun DownloadScreen(onDownload: (String) -> Unit) {
    // UI for downloading the movie file
}
