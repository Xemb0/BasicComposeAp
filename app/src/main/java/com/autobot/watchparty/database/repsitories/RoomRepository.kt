package com.autobot.watchparty.database.repsitories

import android.util.Log
import com.autobot.watchparty.database.Playback
import com.autobot.watchparty.database.UserData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.json.Json

class RoomRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val json = Json { encodeDefaults = true }

    interface RoomExistsListener {
        fun onResult(exists: Boolean)
        fun onError(e: Exception)
    }




    fun createRoom(roomId: String, user: UserData, onComplete: (Boolean) -> Unit) {
        val userId = user.userId ?: ""
        if (userId.isEmpty()) {
            Log.e("Firestore", "User ID is empty")
            onComplete(false)
            return
        }
        firestore.collection("rooms").document(roomId).collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                Log.d("Firestore", "Room created and user added successfully")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error creating room and adding user", e)
                onComplete(false)
            }
    }


    fun checkRoomExists(roomId: String, listener: RoomExistsListener) {
        val db = FirebaseFirestore.getInstance()
        db.collection("rooms").document(roomId).get()
            .addOnSuccessListener { document ->
                listener.onResult(document.exists())
            }
            .addOnFailureListener { e ->
                listener.onError(e)
            }
    }

    fun joinRoomIfExists(roomId: String, user: UserData, onComplete: (Boolean) -> Unit) {
        val userId = user.userId ?: ""
        if (userId.isEmpty()) {
            Log.e("Firestore", "User ID is empty")
            onComplete(false)
            return
        }
        firestore.collection("rooms").document(roomId).collection("users").document(userId)
        val roomRef = firestore.collection("rooms").document(roomId)
        roomRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d("Firestore", "Room found, adding user...")
                    roomRef.collection("users").document(userId)
                        .set(user)
                        .addOnSuccessListener {
                            Log.d("Firestore", "User joined room successfully")
                            onComplete(true)
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error joining room", e)
                            onComplete(false)
                        }
                } else {
                    Log.d("Firestore", "Room does not exist")
                    onComplete(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error checking room existence", e)
                onComplete(false)
            }
    }
    fun exitRoom(roomId: String, userId: String) {
        firestore.collection("rooms").document(roomId).collection("users").document(userId)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "User exited room successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error exiting room", e)
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
                    val userList = documentSnapshot.documents.mapNotNull { document ->
                        document.toObject(UserData::class.java)
                    }
                    Log.d("Firestore", "Users updated: ${userList.size} users found")
                    userList.forEach { user ->
                        Log.d("Firestore", "User: ${user.username}")
                    }
                    onUpdate(userList)
                } ?: Log.d("Firestore", "No users found")
            }
    }

}
