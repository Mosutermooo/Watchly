package com.example.watchly.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "channel")
data class Channel(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String? = null,
    val description: String? = null,
    val image: String? = null,
    val banner: String? = null,
    val channelId: String? = null,
    val ownedBy: String? = null,
    val createAt: String? = null
) : Serializable