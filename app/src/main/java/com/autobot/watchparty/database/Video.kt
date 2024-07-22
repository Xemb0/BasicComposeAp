package com.autobot.watchparty.database

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Video(
    val name: String = "",
    val url: String = ""
): Parcelable
