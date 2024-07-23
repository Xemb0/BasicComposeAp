package com.autobot.watchparty.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Parcelize
@Entity
data class Movie(
    @PrimaryKey(autoGenerate = false)
    val name: String="",
    val url: String=""
): Parcelable
