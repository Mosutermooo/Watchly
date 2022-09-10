package com.example.watchly.models

import com.google.gson.annotations.SerializedName

data class FcmNotificationResponse (
    @SerializedName("notification_key")
    val notification_key: String? = null
        )