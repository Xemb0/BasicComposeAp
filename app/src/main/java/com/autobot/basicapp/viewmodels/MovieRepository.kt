package com.autobot.basicapp.viewmodels

import com.autobot.basicapp.database.Movie
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class MovieRepository {

    private val storage = FirebaseStorage.getInstance()

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
}
