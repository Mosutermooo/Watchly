package com.example.watchly.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.watchly.R
import com.example.watchly.models.Channel
import com.example.watchly.models.UploadVideo
import com.example.watchly.models.Video
import com.example.watchly.ui.activities.VideoPlayActivity
import com.example.watchly.ui.viewmodels.VideoViewModel
import com.example.watchly.uils.Constants.Channels
import com.example.watchly.uils.Constants.Videos
import com.example.watchly.uils.ReusableResource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage


class UploadVideoService : Service(){
    companion object{
        const val CHANNEL_ID = "UploadNotification"
        const val CHANNEL_NAME = "UploadNotificationName"
        const val NOTIFICATION_ID = 0
        const val TAG = "Upload Video Service"
    }

    private lateinit var  mNotifyManager : NotificationManager
    private lateinit var  mBuilder : NotificationCompat.Builder
    private lateinit var  videoViewModel: VideoViewModel
    private lateinit var  thumbnail: Bitmap
    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private var downloadVideoUri : Uri? = null
    private var downloadImageUri : Uri? = null
    private var thumbnailUri : Uri? = null
    private lateinit var notificationManager : NotificationManagerCompat
    private var allVideosToUpload : Int? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        videoViewModel = ViewModelProvider.AndroidViewModelFactory(application).create(VideoViewModel::class.java)

        val observer = Observer<List<UploadVideo>> { listOfUploadVideos ->
            listOfUploadVideos.forEach { uploadVideo->
                sendNotification()
                uploadVideoToStorage(Uri.parse("file://${uploadVideo.video}"), uploadVideo)
            }

        }
        videoViewModel.receiveUploadVideoFromDB().observeForever(observer)
        return START_STICKY

    }

    private fun uploadVideoToStorage(video: Uri, uploadVideo: UploadVideo) {
        storage.reference.child("${ReusableResource.uid()}/").child("userData/")
            .child("channel/").child("videos/")
            .child("${uploadVideo.videoId}/")
            .child(uploadVideo.videoId)
            .putFile(video)
            .addOnSuccessListener{ task ->
                task.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    downloadVideoUri = it
                    uploadVideoThumbnailToStorage(Uri.parse(uploadVideo.thumbnail), uploadVideo)
                }
            }.addOnFailureListener{
                somethingWentWrongNotification(it)
                stopSelf()
            }
    }

    private fun uploadVideoThumbnailToStorage(image: Uri, uploadVideo: UploadVideo) {
        storage.reference.child("${ReusableResource.uid()}/").child("userData/")
            .child("channel/").child("videos/")
            .child("${uploadVideo.videoId}/")
            .child("${uploadVideo.videoId}-THUMBNAIL")
            .putFile(ReusableResource.thumbnailUri!!)
            .addOnSuccessListener{ task ->
                task.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    downloadImageUri = it
                    val uploadVideoModel = UploadVideo(
                        uploadVideo.id,
                        uploadVideo.name,
                        uploadVideo.description,
                        downloadImageUri.toString(),
                        downloadVideoUri.toString(),
                        uploadVideo.visibility,
                        uploadVideo.channel,
                        ReusableResource.uid(),
                        uploadVideo.videoId
                    )
                    createVideoToFirestore(uploadVideoModel)
                }
            }.addOnFailureListener{
                somethingWentWrongNotification(it)
                stopSelf()
            }

    }

    private fun createVideoToFirestore(uploadVideo: UploadVideo) {
        firestore.collection(Videos).document(uploadVideo.videoId)
            .set(uploadVideo, SetOptions.merge())
            .addOnSuccessListener {
                uploadCompleted(uploadVideo)
            }.addOnFailureListener{
                somethingWentWrongNotification(it)
                stopSelf()
            }
    }

    private fun somethingWentWrongNotification(it: Exception) {
        videoViewModel.deleteEverythingFromUploadVideo()
        mBuilder.setContentTitle("OOOPS!! Something went wrong")
            .setContentText(it.message)
            .setProgress(0, 0, false)
            .setOngoing(false)
        mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build())
    }

    private fun sendNotification(){
        createNotificationChannel()
        mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
        notificationManager = NotificationManagerCompat.from(this)
        mBuilder.setContentTitle("Video Uploading")
            .setContentText("Upload in progress")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setProgress(0, 0, true)

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build())
    }

    private fun uploadCompleted(uploadVideo: UploadVideo) {
        videoViewModel.deleteUploadVideoFromDB(uploadVideo.id!!)
       firestore.collection(Videos)
           .document(uploadVideo.videoId)
           .get().addOnSuccessListener {
               val video = it.toObject(Video::class.java)
               if(video != null){
                   getChannel(video)
               }
           }
    }

    private fun getChannel(video: Video) {
       firestore.collection(Channels)
           .document(video.channel.toString())
           .get().addOnSuccessListener {
               val channel = it.toObject(Channel::class.java)
               videoViewModel.notifySubscribers(channel, video)

               val intent = Intent(this, VideoPlayActivity::class.java)
               intent.putExtra("video", video)

               val pendingIntent = TaskStackBuilder.create(this).run {
                   addNextIntentWithParentStack(intent)
                   getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
               }



               Glide.with(this).asBitmap().load(video.thumbnail).into(object : CustomTarget<Bitmap>(){
                   override fun onResourceReady(
                       resource: Bitmap,
                       transition: Transition<in Bitmap>?,
                   ) {
                       sendNotification2(resource, video, channel, pendingIntent)
                   }

                   override fun onLoadCleared(placeholder: Drawable?) {

                   }

               })

           }

    }

    private fun sendNotification2(
        thumbnail: Bitmap,
        video: Video,
        channel: Channel?,
        pendingIntent: PendingIntent
    ) {
        Glide.with(this).asBitmap().load(channel?.image).into(object : CustomTarget<Bitmap>(){
            override fun onResourceReady(channelIcon: Bitmap, transition: Transition<in Bitmap>?) {
                mBuilder
                    .setContentTitle(channel?.name)
                    .setContentText(video.name)
                    .setLargeIcon(channelIcon)
                    .setSubText("Video Finish Uploading")
                    .setStyle(NotificationCompat.BigPictureStyle()
                        .bigPicture(thumbnail)
                        .bigLargeIcon(null))
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                    .setOnlyAlertOnce(true)
                    .setContentIntent(pendingIntent)
                notificationManager.notify(NOTIFICATION_ID, mBuilder.build())
                stopSelf()
            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }

        })
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                lightColor = Color.GREEN
                enableLights(true)
            }
            mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotifyManager.createNotificationChannel(channel)
        }
    }



    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}