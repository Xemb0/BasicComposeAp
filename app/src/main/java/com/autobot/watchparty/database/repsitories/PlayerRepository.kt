package com.autobot.watchparty.database.repsitories

import android.util.Log
import com.autobot.watchparty.database.Playback
import com.google.firebase.firestore.FirebaseFirestore

class PlayerRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun listenForPlayback(roomId: String, onUpdate: (Playback) -> Unit) {
        firestore.collection("rooms").document(roomId).collection("playback").document("playback")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("Firestore", "Error getting playback: ", exception)
                    return@addSnapshotListener
                }
                snapshot?.let { documentSnapshot ->
                    val playback = documentSnapshot.toObject(Playback::class.java)
                    playback?.let {
                        Log.d("Firestore", "Playback updated: ${playback.timestamp}")
                        onUpdate(playback)
                    }
                } ?: Log.d("Firestore", "No playback found")
            }
    }
    fun updatePlayback(roomId: String,playback: Playback) {
        firestore.collection("rooms").document(roomId).collection("playback").document("playback")
            .set(playback)
            .addOnSuccessListener {
                Log.d("Firestore", "Playback updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error updating playback", e)
            }
    }

    fun updateIsPlaying(roomId: String, isPlaying: Boolean) {
        firestore.collection("rooms").document(roomId).collection("playback").document("playback")
            .update("videoPaused", !isPlaying)
            .addOnSuccessListener {
                Log.d("Firestore", "IsPlaying updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error updating isPlaying", e)
            }
    }

    fun updateTimestamp(s: String, seekForwardIncrementMs: Long) {
        firestore.collection("rooms").document(s).collection("playback").document("playback")
            .update("timestamp", seekForwardIncrementMs)
            .addOnSuccessListener {
                Log.d("Firestore", "Timestamp updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error updating timestamp", e)
            }

    }

    fun updateMovieName(roomId: String, movieName: String) {
        firestore.collection("rooms").document(roomId).collection("playback").document("playback")
            .update("movieName", movieName)
            .addOnSuccessListener {
                Log.d("Firestore", "Movie name updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error updating movie name", e)
            }
    }

}