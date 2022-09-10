package com.example.watchly.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "uploadVideo")
data class UploadVideo(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val name: String,
    val description: String,
    val thumbnail: String,
    val video: String,
    val visibility: String,
    val channel: String,
    val user: String,
    val videoId: String
): Serializable
