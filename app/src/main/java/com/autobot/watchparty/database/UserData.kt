package com.autobot.watchparty.database

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class UserData(
    val userId: String ="",
    val username: String? = "",
    val profilePictureUrl: String? = "",
): Parcelable
