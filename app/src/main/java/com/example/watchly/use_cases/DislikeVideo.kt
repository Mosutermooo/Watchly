package com.example.watchly.use_cases

import androidx.lifecycle.MutableLiveData
import com.example.watchly.models.Likes
import com.example.watchly.uils.Constants
import com.example.watchly.uils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class DislikeVideo {
    val dislikeVideoState: MutableLiveData<Resource<Likes>> = MutableLiveData()
    val removeDislikeVideoState: MutableLiveData<Resource<Likes>> = MutableLiveData()
    val firestore = FirebaseFirestore.getInstance()
    fun dislike(videoId: String, likesBy: String){

        val dislikes = Likes(
            likesBy
        )

        firestore.collection(Constants.Videos)
            .document(videoId).collection(Constants.Dislikes)
            .document(likesBy).set(dislikes, SetOptions.merge())
            .addOnSuccessListener {
                dislikeVideoState.postValue(Resource.Success(null))
                LikeVideo().removeLike(videoId)
            }
    }

    fun removeDislike(videoId: String, likesBy: String){
        firestore.collection(Constants.Videos)
            .document(videoId).collection(Constants.Dislikes)
            .document(likesBy).delete()
            .addOnSuccessListener {
                removeDislikeVideoState.postValue(Resource.Success(null))
            }
    }
}