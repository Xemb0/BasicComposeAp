package com.autobot.watchparty

import com.google.firebase.firestore.FirebaseFirestore

val db = FirebaseFirestore.getInstance()

fun createRoom(roomId: String, hostId: String, onComplete: (Boolean) -> Unit) {
    val room = hashMapOf(
        "hostId" to hostId,
        "createdAt" to System.currentTimeMillis()
    )
    db.collection("rooms").document(roomId)
        .set(room)
        .addOnSuccessListener { onComplete(true) }
        .addOnFailureListener { onComplete(false) }
}

fun joinRoom(roomId: String, userId: String, onComplete: (Boolean) -> Unit) {
    val participant = hashMapOf(
        "userId" to userId,
        "joinedAt" to System.currentTimeMillis()
    )
    db.collection("rooms").document(roomId)
        .collection("participants")
        .add(participant)
        .addOnSuccessListener { onComplete(true) }
        .addOnFailureListener { onComplete(false) }
}
