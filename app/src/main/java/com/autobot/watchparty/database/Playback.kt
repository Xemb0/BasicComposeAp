package com.autobot.watchparty.database

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playback(
    val timestamp: Long=0,
    val videoPaused: Boolean = true,
    val movieName: String = ""
): Parcelable
