package com.autobot.basicapp.signin

import kotlinx.serialization.Serializable

@Serializable
data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

@Serializable
data class UserData(
    val userId: String? ="",
    val username: String? = "",
    val profilePictureUrl: String? = ""
)
