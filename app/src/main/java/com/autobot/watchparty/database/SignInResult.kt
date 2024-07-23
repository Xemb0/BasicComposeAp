package com.autobot.watchparty.database

import kotlinx.serialization.Serializable

@Serializable
data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)
