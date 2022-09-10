package com.example.watchly.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.example.watchly.R
import com.example.watchly.models.Data
import com.example.watchly.models.Video
import com.example.watchly.ui.activities.VideoPlayActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class SendVideoUploadNotificationService : FirebaseMessagingService() {

    private lateinit var  mBuilder : NotificationCompat.Builder
    private lateinit var  mNotifyManager : NotificationManager
    private lateinit var  notificationManager : NotificationManagerCompat


    companion object {
        const val PendingIntentRequestCode = 1
        const val Channel_ID = "notify_subscriber_new_video"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_NAME = "notify_subscriber_new_video_channel_name"
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val channelName = message.data["channelName"]
        val id = message.data["id"]
        val name = message.data["name"]
        val description = message.data["description"]
        val thumbnail = message.data["thumbnail"]
        val video = message.data["video"]
        val visibility = message.data["visibility"]
        val channel = message.data["channel"]
        val user = message.data["user"]
        val videoId = message.data["videoId"]
        val views = message.data["views"]
        val data = Data(
            channelName,
            id?.toInt(),
            name,
            description, thumbnail, video, visibility, channel, user, videoId,views?.toInt()
        )
        sendNotification(data)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }


    private fun sendNotification(data: Data) {
        createNotificationChannel()
        val video = Video(
            data.id,
            data.name,
            data.description,
            data.thumbnail,
            data.video,
            data.visibility,
            data.channel,
            data.user,
            data.videoId,
            data.views
        )

        val intent = Intent(this, VideoPlayActivity::class.java)
        intent.putExtra("video", video)

        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        mBuilder = NotificationCompat.Builder(this, Channel_ID)
        notificationManager = NotificationManagerCompat.from(this)
        mBuilder.setContentTitle(data.channelName.toString())
            .setContentText(data.name)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(Glide.with(this).asBitmap().load(data.thumbnail).submit().get())
                .bigLargeIcon(null))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setOnlyAlertOnce(true)

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build())
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Channel_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                lightColor = Color.GREEN
                enableLights(true)
            }
            mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotifyManager.createNotificationChannel(channel)
        }
    }


}