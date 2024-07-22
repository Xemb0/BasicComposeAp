package com.autobot.watchparty.database

import com.autobot.watchparty.signin.UserData

data class Room(
    val roomId: String = "",
    val users: List<UserData> = emptyList()
)
