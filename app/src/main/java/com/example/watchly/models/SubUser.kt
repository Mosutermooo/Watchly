package com.example.watchly.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "subUserTable")
data class SubUser(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val image: String? = null,
    val name: String? = null,
    val createdAt: String? = null,
    val account: String? = null,
    val subUserUid: String? = null
): Serializable
