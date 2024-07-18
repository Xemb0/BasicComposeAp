package com.autobot.basicapp

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.io.File

val storage = FirebaseStorage.getInstance()
val storageRef = storage.reference

fun uploadMovie(uri: Uri, onComplete: (Boolean) -> Unit) {
    val movieRef = storageRef.child("movies/${uri.lastPathSegment}")
    movieRef.putFile(uri)
        .addOnSuccessListener { onComplete(true) }
        .addOnFailureListener { onComplete(false) }
}

fun downloadMovie(fileName: String, onComplete: (Boolean, Uri?) -> Unit) {
    val movieRef = storageRef.child("movies/$fileName")
    val localFile = File.createTempFile("movie", "mp4")
    
    movieRef.getFile(localFile)
        .addOnSuccessListener { onComplete(true, Uri.fromFile(localFile)) }
        .addOnFailureListener { onComplete(false, null) }
}
