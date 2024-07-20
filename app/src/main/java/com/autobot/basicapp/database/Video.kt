package com.autobot.basicapp.database

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Video(
    val name: String = "",
    val url: String = ""
): Parcelable
