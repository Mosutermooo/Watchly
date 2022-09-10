package com.example.watchly.models

import com.google.gson.annotations.SerializedName

data class FcmSendNotificationRequest (
    @SerializedName("to")
    val to: String? = null,
    @SerializedName("data")
    val data: Data? = null
        )