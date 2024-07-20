package com.autobot.basicapp.database

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.storage.TaskState
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playback(
    val timestamp: Long=0,
    val isVideoPaused: Boolean = true
): Parcelable
