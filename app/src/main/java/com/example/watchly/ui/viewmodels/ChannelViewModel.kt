package com.example.watchly.ui.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.watchly.db.Database
import com.example.watchly.models.Channel
import com.example.watchly.models.Likes
import com.example.watchly.models.Subscriber
import com.example.watchly.models.User
import com.example.watchly.uils.Constants
import com.example.watchly.uils.Constants.Channels
import com.example.watchly.uils.Constants.Subscribers
import com.example.watchly.uils.Constants.USERS
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource
import com.example.watchly.uils.ReusableResource.currentDateAndTime
import com.example.watchly.uils.ReusableResource.uid
import com.example.watchly.use_cases.VideoId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ChannelViewModel(
    app: Application
) : AndroidViewModel(app) {

    private var firestore = FirebaseFirestore.getInstance()
    private var messeging = Firebase.messaging
    val channelCreationState : MutableLiveData<Resource<Channel>> = MutableLiveData()
    val channelUpdateState : MutableLiveData<Resource<Channel>> = MutableLiveData()
    val channelState : MutableLiveData<Resource<Channel>> = MutableLiveData()
    val subscribeState : MutableStateFlow<Resource<Subscriber>> = MutableStateFlow(Resource.Idle())
    val unSubscribeState : MutableStateFlow<Resource<Subscriber>> = MutableStateFlow(Resource.Idle())
    val isAlredySubscriber : MutableStateFlow<Resource<Subscriber>> = MutableStateFlow(Resource.Idle())
    private val db = Database.database(app)

    fun createChannelInFireStore(
        generatedChannelId: String,
        channel: Channel
    ){
        firestore.collection(Channels)
            .document(generatedChannelId)
            .set(channel, SetOptions.merge())
            .addOnSuccessListener {
                updateUserChannelStatus(channel)
            }.addOnFailureListener {
                channelCreationState.postValue(Resource.Error(it.message))
            }
    }

    private fun updateUserChannelStatus(channel: Channel) {
        val channelHashMap = HashMap<String, Any>()
        channelHashMap[Constants.channel] = channel.channelId.toString()
        firestore.collection(USERS)
            .document(uid())
            .update(channelHashMap)
            .addOnSuccessListener {
                channelCreationState.postValue(Resource.Success(null))
            }.addOnFailureListener {
                channelCreationState.postValue(Resource.Error(it.message))
            }
    }

    fun getChannelData(uid: String? = null, channelId: String? = null){
        channelState.postValue(Resource.Loading(null))
        firestore.collection(Constants.Channels)
            .whereEqualTo("ownedBy", uid ?: channelId)
            .addSnapshotListener { value, error ->
                value?.let { QuerySnapshot ->
                    QuerySnapshot.forEach {
                        val channel = it.toObject(Channel::class.java)
                        channelState.postValue(Resource.Success(channel))
                    }
                }
                error?.let {
                    channelState.postValue(Resource.Error(it.message))
                }
            }
    }




    fun updateChannelData(channelId: String, hashMap: HashMap<String, Any>) = viewModelScope.launch {
        firestore.collection(Channels)
            .document(channelId)
            .update(hashMap)
            .addOnSuccessListener {
                channelUpdateState.postValue(Resource.Success(null))
            }.addOnFailureListener {
                channelUpdateState.postValue(Resource.Error(null))
            }

    }

    fun cacheChannel(channel: Channel) = viewModelScope.launch {
        db.dbDao().cacheChannel(channel)
    }

    fun deleteCachedChannel() = viewModelScope.launch {
        db.dbDao().deleteCachedChannel()
    }

    fun getCachedChannel() = db.dbDao().getCachedChannel()

    fun updateChannelImageAndBanner(
        bannerUri: Uri?,
        imageUri: Uri?,
        channelId: String
    ){
        if(bannerUri != null){
            channelUpdateState.postValue(Resource.Loading(null))
            addUserBannerToStorage(
                bannerUri,
                channelId
            )
        }

        if(imageUri != null){
            channelUpdateState.postValue(Resource.Loading(null))
            addUserImageToStorage(
                imageUri,
                channelId
            )
        }

    }

    private fun addUserImageToStorage(
        imageUri: Uri,
        channelId: String
    ){
        FirebaseStorage.getInstance().reference.child(ReusableResource.uid())
            .child("/userData")
            .child("channel")
            .child("$channelId-IMAGE")
            .putFile(imageUri)
            .addOnSuccessListener{ task ->
                task.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    val imageHashMap = HashMap<String, Any>()
                    imageHashMap["image"] = it
                    updateChannelData(
                        channelId,
                        imageHashMap
                    )
                }
            }
    }

    private fun addUserBannerToStorage(
        bannerUri: Uri,
        channelId: String
    ){
        FirebaseStorage.getInstance().reference.child(ReusableResource.uid())
            .child("/userData")
            .child("channel")
            .child("$channelId-BANNER")
            .putFile(bannerUri)
            .addOnSuccessListener{ task ->
                task.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    val imageHashMap = HashMap<String, Any>()
                    imageHashMap["banner"] = it
                    updateChannelData(
                        channelId,
                        imageHashMap
                    )
                }
            }
    }

    fun subscribe(channelId: String, channelOwner: String){
        firestore.collection(USERS)
            .document(uid())
            .addSnapshotListener { value, error ->
                value?.let {
                    val userFetched = it.toObject(User::class.java)
                    userFetched?.let { user ->
                        val subscriber = Subscriber(
                            uid(),
                            currentDateAndTime(),
                            user.fcmToken
                        )

                        firestore.collection(Channels)
                            .document(channelId)
                            .collection(Subscribers)
                            .document(uid()).set(subscriber, SetOptions.merge())
                            .addOnSuccessListener {
                                viewModelScope.launch {
                                    subscribeState.emit(Resource.Success(null))
                                }
                            }
                    }
                }
            }
    }

    fun unSubscribe(channelId: String){
        firestore.collection(Channels)
            .document(channelId)
            .collection(Subscribers)
            .document(uid()).delete()
            .addOnSuccessListener {
                viewModelScope.launch {
                    unSubscribeState.emit(Resource.Success(null))
                }
            }
    }

    fun isAlreadySubscribed(channelId: String){
        firestore.collection(Channels)
            .document(channelId)
            .collection(Subscribers)
            .document(uid())
            .addSnapshotListener { value, error ->
                value?.let {
                    if(it.exists()) {
                        Log.e("ïn alr ex", "$it")
                        val subscriber = it.toObject(Subscriber::class.java)
                        viewModelScope.launch {
                            isAlredySubscriber.emit(Resource.Success(subscriber))
                        }
                    }else{
                        viewModelScope.launch {
                            isAlredySubscriber.emit(Resource.Error(null))
                        }
                    }
                }
            }
    }



}