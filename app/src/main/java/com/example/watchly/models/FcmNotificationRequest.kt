package com.example.watchly.models

import com.google.gson.annotations.SerializedName

data class FcmNotificationRequest (
    @SerializedName("operation")
    val operation: String? = null,
    @SerializedName("notification_key_name")
    val notification_key_name: String? = null,
    @SerializedName("registration_ids")
    val registration_ids: List<String>? = null
        )