package com.example.watchly.data

import com.example.watchly.models.FcmNotificationRequest
import com.example.watchly.models.FcmSendNotificationRequest
import com.example.watchly.retorfit.FcmService

class FcmRepository(private val apiService: FcmService) {
    suspend fun notificationKey(
        authKey: String,
        project_id: String,
        fcmRequest: FcmNotificationRequest
    ) = apiService.notificationKey(authKey, project_id, fcmRequest)

    suspend fun pushNotification(
        authKey: String,
        pushRequest: FcmSendNotificationRequest
    ) = apiService.pushNotification(
        authKey, pushRequest
    )

}