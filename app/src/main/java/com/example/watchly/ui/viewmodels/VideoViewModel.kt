package com.example.watchly.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchly.data.FcmRepository
import com.example.watchly.db.Database
import com.example.watchly.models.*
import com.example.watchly.retorfit.FcmInstanceApi
import com.example.watchly.uils.Constants
import com.example.watchly.uils.Constants.AUTH_KEY
import com.example.watchly.uils.Constants.Comments
import com.example.watchly.uils.Constants.KEY
import com.example.watchly.uils.Constants.Subscribers
import com.example.watchly.uils.Constants.Videos
import com.example.watchly.uils.Constants.Views
import com.example.watchly.uils.Constants.project_Id
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource.currentDateAndTime
import com.example.watchly.uils.ReusableResource.uid
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class VideoViewModel(
    app: Application
) : AndroidViewModel(app) {
    private val repository: FcmRepository = FcmRepository(FcmInstanceApi.apiService)
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val db = Database.database(app)
    val fetchHomeVideosState : MutableStateFlow<Resource<List<HomeRecyclerViewItem>>> = MutableStateFlow(Resource.Idle())
    val fetchPersonalVideosState : MutableStateFlow<Resource<List<PersonalVideosRecyclerViewItem>>> = MutableStateFlow(Resource.Idle())
    val fetchNewSuggestionVideosState : MutableStateFlow<Resource<List<Video>>> = MutableStateFlow(Resource.Idle())
    val fetchChannelVideosState : MutableStateFlow<Resource<List<PersonalVideosRecyclerViewItem>>> = MutableStateFlow(Resource.Idle())
    val viewsState : MutableStateFlow<Resource<List<Viewer>>> = MutableStateFlow(Resource.Idle())
    val commentState : MutableStateFlow<Resource<Comment>> = MutableStateFlow(Resource.Idle())
    val getCommentsState : MutableStateFlow<Resource<List<Comment>>> = MutableStateFlow(Resource.Idle())

    fun cacheUploadVideoToDB(uploadVideo: UploadVideo) = viewModelScope.launch {
        db.dbDao().cacheUploadVideo(uploadVideo)
    }

    fun receiveUploadVideoFromDB() = db.dbDao().receiveCachedUploadVideo()
    fun deleteUploadVideoFromDB(id: Int) = viewModelScope.launch {
        db.dbDao().deleteCachedUploadVideo(id)
    }
    fun deleteEverythingFromUploadVideo() = viewModelScope.launch {
        db.dbDao().deleteEverythingFromUploadVideo()
    }

    fun fetchHomeVideos() = viewModelScope.launch {
        fetchHomeVideosState.emit(Resource.Loading(null))
        firestore.collection(Constants.Videos)
            .whereEqualTo("visibility", "public")
            .limit(20)
            .addSnapshotListener { value, error ->
                value?.let {
                    viewModelScope.launch {
                        val homeItemsList = mutableListOf<HomeRecyclerViewItem>()
                        val videos = it.toObjects(HomeRecyclerViewItem.Video::class.java)
                        homeItemsList.addAll(videos)
                        fetchHomeVideosState.emit(Resource.Success(homeItemsList))
                    }
                }
                error?.let {
                    viewModelScope.launch {
                        fetchHomeVideosState.emit(Resource.Error(it.message))
                    }
                }
            }
    }


    fun fetchNewSuggestionVideos() = viewModelScope.launch {
        fetchNewSuggestionVideosState.emit(Resource.Loading(null))
        firestore.collection(Constants.Videos)
            .whereEqualTo("visibility", "public")
            .limit(30)
            .addSnapshotListener { value, error ->
                value?.let {
                    viewModelScope.launch {
                        val videos = it.toObjects(Video::class.java)
                        fetchNewSuggestionVideosState.emit(Resource.Success(videos))
                    }
                }
                error?.let {
                    viewModelScope.launch {
                        fetchNewSuggestionVideosState.emit(Resource.Error(it.message))
                    }
                }
            }
    }


    fun fetchPersonalVideos() =  viewModelScope.launch {
        fetchPersonalVideosState.emit(Resource.Loading(null))
       firestore.collection(Constants.Videos)
            .whereEqualTo("user", uid())
           .whereEqualTo("visibility", "public")
            .get().addOnSuccessListener { queryVideos ->
               viewModelScope.launch {
                   if(queryVideos != null) {
                       val videos = queryVideos.toObjects(PersonalVideosRecyclerViewItem.Video::class.java)
                       fetchPrivateVideos(videos)
                   }
               }
            }.addOnFailureListener {
               viewModelScope.launch {
                   fetchPersonalVideosState.emit(Resource.Error(it.message))
               }
           }
    }

    fun fetchChannelVideos(uid: String) =  viewModelScope.launch {
        fetchChannelVideosState.emit(Resource.Loading(null))
        firestore.collection(Constants.Videos)
            .whereEqualTo("user", uid)
            .whereEqualTo("visibility", "public")
            .get().addOnSuccessListener { queryVideos ->
                viewModelScope.launch {
                    if(queryVideos != null) {
                        val videosList = mutableListOf<PersonalVideosRecyclerViewItem>()
                        val videos = queryVideos.toObjects(PersonalVideosRecyclerViewItem.Video::class.java)
                        viewModelScope.launch {
                            videosList.addAll(videos)
                            fetchChannelVideosState.emit(Resource.Success(videosList))
                        }
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    fetchChannelVideosState.emit(Resource.Error(it.message))
                }
            }
    }

    private fun fetchPrivateVideos(videos: List<PersonalVideosRecyclerViewItem.Video>) {
        val personalVideosItemList = mutableListOf<PersonalVideosRecyclerViewItem>()
        firestore.collection(Constants.Videos)
            .whereEqualTo("user", uid())
            .whereEqualTo("visibility", "private")
            .get().addOnSuccessListener { queryVideos ->
                viewModelScope.launch {
                    val privateVideos = queryVideos.toObjects(PersonalVideosRecyclerViewItem.PrivateVideo::class.java)
                    if(queryVideos != null){
                        Log.e("private videos", "$videos")
                        personalVideosItemList.add(PersonalVideosRecyclerViewItem.Sections(1, "Private Videos"))
                        personalVideosItemList.addAll(privateVideos)
                        personalVideosItemList.add(PersonalVideosRecyclerViewItem.Sections(2, "Videos"))
                        personalVideosItemList.addAll(videos)
                        fetchPersonalVideosState.emit(Resource.Success(personalVideosItemList))
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    fetchPersonalVideosState.emit(Resource.Error(it.message))
                }
            }
    }

    fun addAComment(comment: String, videoId: String) = viewModelScope.launch {
        commentState.emit(Resource.Loading(null))
        if(comment.isNotEmpty()){

            val document = firestore.collection(Constants.Videos)
                .document(videoId)
                .collection(Comments)
                .document()
            val commentClass = Comment(
                uid(),
                comment,
                currentDateAndTime(),
                document.id
            )
            document.set(commentClass, SetOptions.merge())
                .addOnSuccessListener {
                    viewModelScope.launch {
                        document.id
                        commentState.emit(Resource.Success(null))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        commentState.emit(Resource.Error(it.message))
                    }
                }
        }
    }

    fun getComments(videoId: String) = viewModelScope.launch {
        getCommentsState.emit(Resource.Loading(null))
        firestore.collection(Constants.Videos)
            .document(videoId)
            .collection(Comments)
            .addSnapshotListener { value, error ->
                value?.let {
                    val comments = it.toObjects(Comment::class.java)
                    viewModelScope.launch {
                        getCommentsState.emit(Resource.Success(comments))
                    }
                }
                error?.let {
                    viewModelScope.launch {
                        getCommentsState.emit(Resource.Error(null))
                    }
                }
            }

    }

    fun updateVideo(videoId: String, videoUpdateRequests: HashMap<String, Any>){
        firestore.collection(Videos)
            .document(videoId)
            .update(videoUpdateRequests)

    }

    fun addVideoView(videoId: String){
        firestore.collection(Videos)
            .document(videoId)
            .collection(Views)
            .document().let {
                val documentId = it.id
                val viewer = Viewer(
                    uid(),
                    documentId
                )
                it.set(viewer).addOnSuccessListener {
                    firestore.collection(Videos)
                        .document(videoId)
                        .collection(Views)
                        .addSnapshotListener { value, _ ->
                            value?.let {
                                viewModelScope.launch {
                                    val viewers = it.toObjects(Viewer::class.java)
                                    val videoRequestHashMap = HashMap<String, Any>()
                                    videoRequestHashMap["views"] = viewers.size
                                    updateVideo(videoId, videoRequestHashMap)
                                }
                            }
                        }
                }
            }
    }

    fun getViewers(videoId: String){
        firestore.collection(Videos)
            .document(videoId)
            .collection(Views)
            .addSnapshotListener { value, _ ->
                value?.let {
                    viewModelScope.launch {
                        val viewers = it.toObjects(Viewer::class.java)
                        viewsState.emit(Resource.Success(viewers))
                    }
                }
            }
    }

    fun notifySubscribers(channel: Channel?, video: Video) {
        firestore.collection(Constants.Channels)
            .document(channel?.channelId.toString())
            .collection(Subscribers)
            .get().addOnSuccessListener { Snapshot ->
                val fcmTokens = mutableListOf<String>()
                val subscribers = Snapshot.toObjects(Subscriber::class.java)
                subscribers.forEach {
                    fcmTokens.add(it.fcmToken.toString())
                }
                getNotificationKey(fcmTokens, video, channel)
            }
    }

    private fun getNotificationKey(fcmTokens: MutableList<String>, video: Video, channel: Channel?) = viewModelScope.launch {
        Log.e("Fcm Token", "$fcmTokens")
        val notificationRequestBody = FcmNotificationRequest(
            "create",
            video.videoId,
            fcmTokens
        )
        val response = repository.notificationKey(
            "$KEY$AUTH_KEY",
            project_Id,
            notificationRequestBody
        )
        if(response.isSuccessful){
            val notificationKey = response.body()?.notification_key
            if(notificationKey != null){
                pushNotification(notificationKey, video, channel!!)
            }
        }else{
            Log.e("Error Response 1", "${response.errorBody()}")
            Log.e("Error Response 2", "${response.message()}")
            Log.e("Error Response 3", "${response.code()}")
        }
    }

    private fun pushNotification(notificationKey: String, video: Video, channel: Channel) = viewModelScope.launch {

        val data =  Data(
            channel.name,
            video.id,
            video.name,
            video.description,
            video.thumbnail,
            video.video,
            video.visibility,
            video.channel,
            video.user,
            video.videoId,
            video.views
        )

        val pushRequest = FcmSendNotificationRequest(
            notificationKey,
            data
        )

        val response = repository.pushNotification(
            "$KEY$AUTH_KEY",
            pushRequest
        )
        if(response.isSuccessful){
            response.body()?.let {
                Log.e("Pushed Notficaiton", "${it.success}")
                Log.e("Pushed F Notification", "${it.failure}")
            }
        }else{
            Log.e("Error Pushing notify 1", "${response.message()}")
            Log.e("Error Pushing notify 2", "${response.code()}")
            Log.e("Error Pushing notify 3", "${response.errorBody()}")
        }
    }


}