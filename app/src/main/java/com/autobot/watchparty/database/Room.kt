package com.autobot.watchparty.database


data class Room(
    val roomId: String = "",
    val users: List<UserData> = emptyList()
)
