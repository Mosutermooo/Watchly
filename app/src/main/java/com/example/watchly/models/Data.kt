package com.example.watchly.models

import com.google.gson.annotations.SerializedName

data class Data (
    @SerializedName("channelName")
    val channelName: String? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("thumbnail")
    val thumbnail: String? = null,
    @SerializedName("video")
    val video: String? = null,
    @SerializedName("visibility")
    val visibility: String? = null,
    @SerializedName("channel")
    val channel: String? = null,
    @SerializedName("user")
    val user: String? = null,
    @SerializedName("videoId")
    val videoId: String? = null,
    @SerializedName("views")
    val views: Int? = null
        )