package com.example.watchly.models

import com.google.gson.annotations.SerializedName

data class FcmSendNotificationResponse (
    @SerializedName("success")
    val success: Int? = null,
    @SerializedName("failure")
    val failure: Int? = null
        )