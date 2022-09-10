package com.example.watchly.retorfit

import com.example.watchly.models.FcmNotificationRequest
import com.example.watchly.models.FcmNotificationResponse
import com.example.watchly.models.FcmSendNotificationRequest
import com.example.watchly.models.FcmSendNotificationResponse
import com.example.watchly.uils.Constants.notification_post
import com.example.watchly.uils.Constants.notification_send_post
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface FcmService {

    @Headers("Content-Type: application/json")
    @POST(notification_post)
    suspend fun notificationKey(
        @Header("Authorization") authKey: String,
        @Header("project_id") project_id: String,
        @Body fcmRequest : FcmNotificationRequest
    ) : Response<FcmNotificationResponse>

    @Headers("Content-Type: application/json")
    @POST(notification_send_post)
    suspend fun pushNotification(
        @Header("Authorization") authKey: String,
        @Body pushRequest : FcmSendNotificationRequest
    ) : Response<FcmSendNotificationResponse>



}