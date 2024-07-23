package com.autobot.watchparty.database.repsitories

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
import androidx.media3.exoplayer.ExoPlayer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.autobot.watchparty.R
import com.autobot.watchparty.database.Movie
import com.autobot.watchparty.workmanager.DownloadWorker
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.io.File

class MovieRepository {

    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()




     fun isFileDownloaded(fileName: String,context: Context): Boolean {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), fileName)
        return file.exists()
    }


    // Fetch the list of movie files in Firebase Storage
    suspend fun fetchMovies(): List<Movie> {
        val storageRef = storage.reference.child("movies")
        val movieList = mutableListOf<Movie>()

        try {
            val result = storageRef.listAll().await()
            for (item in result.items) {
                val fileName = item.name
                val downloadUrl = item.downloadUrl.await()
                movieList.add(Movie(name = fileName, url = downloadUrl.toString()))
            }
        } catch (e: Exception) {
            // Handle errors
        }

        return movieList
    }

    fun listenForMovieChanges(roomId: String,onMoviesUpdated:(List<Movie>)-> Unit) {

        firestore.collection("rooms").document(roomId).collection("movies")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle errors
                    return@addSnapshotListener
                }
                // Check if snapshot is not null and process changes
                val movies = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Movie::class.java)
                } ?: emptyList()
                Log.d("Firestore", "Movie updated: ${movies.size} users found")
                movies.forEach { user ->
                    Log.d("Firestore", "User: ${user.name}")
                }
                // Handle the list of movies (e.g., update UI or notify users)
                onMoviesUpdated(movies)
            }
    }

    suspend fun uploadMovie(
        uri: Uri,
        roomId: String,fileName: String,
        onProgress: (Float) -> Unit,
        onComplete: (Uri?) -> Unit
    ) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val movieRef = storageRef.child("movies/$roomId/${fileName}")

        try {
            // Create the upload task
            val uploadTask = movieRef.putFile(uri)

            // Add a listener to track progress
            uploadTask.addOnProgressListener { snapshot ->
                val progress = (100.0 * snapshot.bytesTransferred / snapshot.totalByteCount).toFloat()
                onProgress(progress)
            }

            // Await the upload task to finish
            uploadTask.await()

            // Retrieve the download URL
            val downloadUrl = movieRef.downloadUrl.await()  // Await to get the download URL

            // Call onComplete with the download URL
            onComplete(downloadUrl)
        } catch (e: Exception) {
            // Handle errors
            onComplete(null)
        }
    }


    fun sanitizeFileName(fileName: String): String {
        // Replace invalid characters with underscores or remove them
        return fileName.replace(Regex("[^a-zA-Z0-9-_]"), "_")
    }

    suspend fun addMovieMetadata(roomId: String, movie: Movie) {
        val sanitizedMovieName = sanitizeFileName(movie.name)
        val movieRef = firestore.collection("rooms").document(roomId).collection("movies").document(sanitizedMovieName)
        movieRef.set(movie).await()
    }




}