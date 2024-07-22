package com.autobot.watchparty

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
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

@Composable
fun ScreenUpload(onExit: () -> Unit) {
    var showConfirmExitDialog by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var isUploading by remember { mutableStateOf(false) }
    var fileName by remember { mutableStateOf<String?>(null) }

    if (showConfirmExitDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Confirm Exit") },
            text = { Text("Are you sure you want to exit? Upload will be canceled.") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmExitDialog = false
                    onExit()
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
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isUploading) {
            Text("Uploading... ${progress.toInt()}%", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(
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
                        uploadMovie(it,
                            fileName!!, // Ensure fileName is not null
                            onProgress = { progressValue ->
                                isUploading = true
                                progress = progressValue
                            },
                            onComplete = { downloadUri ->
                                isUploading = false
                                if (downloadUri != null) {
                                    Toast.makeText(context, "Upload Complete", Toast.LENGTH_SHORT).show()
                                    onExit()
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

fun uploadMovie(uri: Uri, fileName: String, onProgress: (Float) -> Unit, onComplete: (Uri?) -> Unit) {
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    val movieRef = storageRef.child("movies/$fileName")

    val uploadTask = movieRef.putFile(uri)

    uploadTask.addOnSuccessListener {
        movieRef.downloadUrl.addOnSuccessListener { downloadUri ->
            onComplete(downloadUri)
        }.addOnFailureListener {
            onComplete(null)
        }
    }.addOnFailureListener {
        onComplete(null)
    }

    uploadTask.addOnProgressListener { snapshot ->
        val progress = (100.0 * snapshot.bytesTransferred / snapshot.totalByteCount).toFloat()
        onProgress(progress)
    }
}

@Composable
fun DownloadScreen(onDownload: (String) -> Unit) {
    // UI for downloading the movie file
}
