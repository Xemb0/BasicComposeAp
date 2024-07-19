package com.autobot.basicapp.database

import com.autobot.basicapp.signin.UserData

data class Room(
    val roomId: String = "",
    val users: List<UserData> = emptyList()
)
