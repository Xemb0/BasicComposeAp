package com.autobot.basicapp.exoplayer

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.autobot.basicapp.signin.UserData

class RoomRepository {

    private val firestore = FirebaseFirestore.getInstance()

    interface RoomExistsListener {
        fun onResult(exists: Boolean)
        fun onError(e: Exception)
    }

    fun checkRoomExists(roomId: String, listener: RoomExistsListener) {
        firestore.collection("rooms").document(roomId).get()
            .addOnSuccessListener { document ->
                listener.onResult(document.exists())
            }
            .addOnFailureListener { e ->
                listener.onError(e)
            }
    }

    fun createRoom(roomId: String, user: Map<String, Any>, onComplete: (Boolean) -> Unit) {
        firestore.collection("rooms").document(roomId).collection("users").document(user["uid"] as String)
            .set(user)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error creating room and adding user", e)
                onComplete(false)
            }
    }

    fun joinRoom(roomId: String, user: Map<String, String?>, onComplete: (Boolean) -> Unit) {
        firestore.collection("rooms").document(roomId).collection("users").document(user["uid"] as String)
            .set(user)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error joining room", e)
                onComplete(false)
            }
    }

    fun listenForRoomUsers(roomId: String, onUpdate: (List<UserData>) -> Unit) {
        firestore.collection("rooms").document(roomId).collection("users")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("Firestore", "Error getting users: ", exception)
                    return@addSnapshotListener
                }
                snapshot?.let { documentSnapshot ->
                    val userList = documentSnapshot.documents.mapNotNull { it.toObject(UserData::class.java) }
                    onUpdate(userList)
                }
            }
    }
}
