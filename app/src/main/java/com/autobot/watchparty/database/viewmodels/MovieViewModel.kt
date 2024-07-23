package com.autobot.watchparty.database.viewmodels

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.autobot.watchparty.R
import com.autobot.watchparty.database.Movie
import com.autobot.watchparty.database.UserData
import com.autobot.watchparty.database.repsitories.MovieRepository
import com.autobot.watchparty.workmanager.DownloadWorker
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MovieViewModel: ViewModel(){
        private val roomId = "123"
        private val movieRepository = MovieRepository()
        private val _movies = MutableStateFlow<List<Movie>>(emptyList())
        val movies: StateFlow<List<Movie>> = _movies

        private val _loading = MutableStateFlow(false)
        val loading: StateFlow<Boolean> = _loading

        private val _downloadProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
        val downloadProgress: StateFlow<Map<String, Float>> = _downloadProgress


        private val _isDownloaded = MutableLiveData<Boolean>()
        val isDownloaded: LiveData<Boolean> = _isDownloaded

        private val _movieList = MutableStateFlow<List<Movie>>(emptyList())
        val movieList: StateFlow<List<Movie>> = _movieList
        fun getMoviesList(){
                viewModelScope.launch {
                        movieRepository.fetchMovies()
                }
        }

        fun listenForMoviesUpdate(roomId: String){
                viewModelScope.launch {
                        movieRepository.listenForMovieChanges(roomId){
                                _movieList.value = it
                        }
                }
        }
        fun updateDownloadProgress(progress: Float, fileName: String) {
                val currentProgress = _downloadProgress.value.toMutableMap()
                currentProgress[fileName] = progress
                _downloadProgress.value = currentProgress
        }

        fun getDownloadStatus(fileName: String,context: Context): Boolean {
                viewModelScope.launch {
                        _isDownloaded.value = movieRepository.isFileDownloaded(fileName, context)
                }
                if (_isDownloaded.value !=null) {
                        return _isDownloaded.value == true
                }
                return false
        }
        fun fetchMoviesList(){
                viewModelScope.launch {
                        _loading.value = true
                        _movies.value = movieRepository.fetchMovies()
                        _loading.value = false
                }
        }
        fun uploadMovie(roomId: String,uri: Uri,fileName: String, onProgress: (Float) -> Unit ,onComplete: (Uri?) -> Unit) {
                viewModelScope.launch {
                        movieRepository.uploadMovie(uri,roomId,fileName, onProgress = {
                                onProgress(it)
                        },
                                onComplete = {
                                        onComplete(it)
                                        updateMovieMetaData(roomId,Movie(fileName,uri.toString(),))
                                }
                        )
                }
        }


        fun updateMovieMetaData(roomId: String,movie: Movie){
                viewModelScope.launch {
                        movieRepository.addMovieMetadata(roomId,movie)
                }
        }



        fun downloadMovieWithNotification(context: Context,roomId: String, url: String, fileName: String) {
                createNotificationChannel(context)
                val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
                        .setInputData(workDataOf("fileUrl" to url, "fileName" to fileName, "roomId" to roomId))
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