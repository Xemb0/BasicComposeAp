package com.autobot.watchparty.workmanager

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.autobot.watchparty.database.viewmodels.MovieViewModel
import com.autobot.watchparty.storageacess.downloadMovie
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class DownloadWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val fileUrl = inputData.getString("fileUrl") ?: return Result.failure()
        val fileName = inputData.getString("fileName") ?: return Result.failure()
        val roomId = inputData.getString("roomId") ?: return Result.failure()


        val viewModel = MovieViewModel()
        return withContext(Dispatchers.IO) {
            var isSuccess = false
            downloadMovie(applicationContext,roomId,fileName,
                onProgress = { progress ->
                    // Ensure notifications are updated
                    viewModel.showNotification(applicationContext, progress.toInt())
                    viewModel.updateDownloadProgress(progress, fileName)
                },
                onComplete = { success ->
                    isSuccess = success
                    // Log download completion
                    Log.d("DownloadWorker", "Download completed: $success")
                }
            )

            if (isSuccess) Result.success() else Result.failure()
        }
    }
}

fun downloadMovie(context: Context, roomId:String, fileName: String, onProgress: (Float) -> Unit, onComplete: (Boolean) -> Unit) {
    val storage = FirebaseStorage.getInstance()
    var storageRef = storage.reference.child("movies/$roomId/$fileName")
    if(fileName== "Maharaja.2024.720p.10bit.NF.WEBRip.2CH.x265.Esub-MICHEAL.mkv"){
         storageRef = storage.reference.child("movies/$fileName")
    }
    val localFile = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), fileName)

    storageRef.getFile(localFile).addOnSuccessListener {
        onComplete(true)
    }.addOnFailureListener {
        onComplete(false)
    }.addOnProgressListener { snapshot ->
        val progress = (100.0 * snapshot.bytesTransferred / snapshot.totalByteCount).toFloat()
        onProgress(progress)
    }
}
