package com.autobot.watchparty.mainscreens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.autobot.watchparty.database.viewmodels.MovieViewModel
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

@Composable
fun ScreenUpload() {
    var showConfirmExitDialog by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var isUploading by remember { mutableStateOf(false) }
    var fileName by remember { mutableStateOf<String?>(null) }
    val movieViewModel: MovieViewModel = viewModel()

    if (showConfirmExitDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Confirm Exit") },
            text = { Text("Are you sure you want to Cancel The upload? ") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmExitDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showConfirmExitDialog = false
                }) {
                    Text("No")
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isUploading) {
            Text("Uploading... ${progress.toInt()}%", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { progress / 100f }
            )
            Button(onClick = {
                showConfirmExitDialog = true
            }) {
                Text(text = "Cancel Upload")
            }
        } else {
            val context = LocalContext.current
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                uri?.let {
                    fileName = getFileName(context, it) ?: UUID.randomUUID().toString()
                    if (fileName != null) {
                        movieViewModel.uploadMovie("123",it,fileName.toString(),
                            onProgress = { progressValue ->
                                isUploading = true
                                progress = progressValue
                            },
                            onComplete = { downloadUri ->
                                isUploading = false
                                if (downloadUri != null) {
                                    Toast.makeText(context, "Upload Complete", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
            Button(onClick = {
                launcher.launch("video/*")
            }) {
                Text("Upload Movie")
            }
        }
    }
}

fun getFileName(context: Context, uri: Uri): String? {
    var fileName: String? = null
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst()) {
            fileName = cursor.getString(nameIndex)
        }
    }
    return fileName
}

