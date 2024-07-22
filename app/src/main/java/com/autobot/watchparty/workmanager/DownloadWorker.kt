package com.autobot.watchparty.workmanager

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.autobot.watchparty.viewmodels.PlayerViewModel
import com.autobot.watchparty.downloadMovie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val fileUrl = inputData.getString("fileUrl") ?: return Result.failure()
        val fileName = inputData.getString("fileName") ?: return Result.failure()


        val viewModel = PlayerViewModel()
        return withContext(Dispatchers.IO) {
            var isSuccess = false
            downloadMovie(applicationContext, fileName,
                onProgress = { progress ->
                    // Ensure notifications are updated
                    viewModel.showNotification(applicationContext, progress.toInt())
                    viewModel.updateProgress(progress, fileName)

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

