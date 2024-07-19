package com.autobot.basicapp.signin

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

@Parcelize
@Serializable
data class UserData(
    val userId: String ="",
    val username: String? = "",
    val profilePictureUrl: String? = "",
): Parcelable
