package com.example.watchly.models

import androidx.room.PrimaryKey
import java.io.Serializable

sealed class SearchModels {

    data class Video (
        val id: Int? = null,
        val name: String? = null,
        val description: String? = null,
        val thumbnail: String? = null,
        val video: String? = null,
        val visibility: String? = null,
        val channel: String? = null,
        val user: String? = null,
        val videoId: String? = null,
        val views: Int? = null
    ): SearchModels(), Serializable

    data class Channel(
        val id: Int? = null,
        val name: String? = null,
        val description: String? = null,
        val image: String? = null,
        val banner: String? = null,
        val channelId: String? = null,
        val ownedBy: String? = null,
        val createAt: String? = null
    ) : SearchModels(), Serializable

    data class User (
        val name: String? = null,
        val lastname: String? = null,
        val username: String? = null,
        val uid: String? = null,
        val email: String? = null,
        val image: String? = null,
        val channel: String? = null,
        val registrationState: String? = null,
        val fcmToken: String? = null
    ): SearchModels(), Serializable


}