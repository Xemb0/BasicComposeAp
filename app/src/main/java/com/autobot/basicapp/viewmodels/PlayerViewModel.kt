package com.autobot.basicapp.viewmodels

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.autobot.basicapp.R
import com.autobot.basicapp.database.Movie
import com.autobot.basicapp.workmanager.DownloadWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


class PlayerViewModel : ViewModel() {

    private val _currentMovie = MutableStateFlow<Movie>(Movie("", ""))
    val currentMovie: StateFlow<Movie> = _currentMovie

    val repository = MovieRepository()

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _downloadProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
    val downloadProgress: StateFlow<Map<String, Float>> = _downloadProgress


    private val _isDownloaded = MutableLiveData<Boolean>()
    val isDownloaded: LiveData<Boolean> = _isDownloaded



    private val _player = MutableLiveData<ExoPlayer>()
    val player: LiveData<ExoPlayer> = _player

    init {
    }

    fun playVideo(movie: Movie) {
        _player.value?.let { player ->
            val mediaItem = MediaItem.fromUri(movie.url)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        }
    }





    private fun isFileDownloaded(fileName: String,context: Context): Boolean {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), fileName)
        return file.exists()
    }


    init {
        fetchMovies()
    }

     fun updateProgress(progress: Float, fileName: String) {
        val currentProgress = _downloadProgress.value.toMutableMap()
        currentProgress[fileName] = progress
        _downloadProgress.value = currentProgress
     }
    fun getDownloadStatus(fileName: String,context: Context): Boolean {
        _isDownloaded.value = isFileDownloaded(fileName,context)
        return isFileDownloaded(fileName,context)
    }

    fun fetchMovies() {
        viewModelScope.launch {
            _loading.value = true
            val movieList = repository.fetchMovies()
            _movies.value = movieList
            _loading.value = false
        }
    }
    fun downloadMovieWithNotification(context: Context, url: String, fileName: String) {
        createNotificationChannel(context)

        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(workDataOf("fileUrl" to url, "fileName" to fileName))
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
        // Check if WorkManager enqueued the request
        Log.d("PlayerViewModel", "Download work request enqueued for $fileName")
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "download_channel_id",
                "Download Notifications",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for download progress"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
            // Check if channel creation is successful
            Log.d("PlayerViewModel", "Notification channel created")
        }
    }

    fun showNotification(context: Context, progress: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, "download_channel_id")
            .setContentTitle("Downloading Movie")
            .setContentText("Download progress: $progress%")
            .setSmallIcon(R.drawable.ic_user_moive)
            .setProgress(100, progress, false)
            .build()

        notificationManager.notify(1, notification)
        // Check if notification is shown
        Log.d("PlayerViewModel", "Notification shown with progress $progress%")
    }


}
