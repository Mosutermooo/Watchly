package com.example.watchly.use_cases

import androidx.lifecycle.MutableLiveData
import com.example.watchly.models.Likes
import com.example.watchly.uils.Constants
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class LikeVideo {
    val likeVideoState: MutableLiveData<Resource<Likes>> = MutableLiveData()
    val removeLikeVideoState: MutableLiveData<Resource<Likes>> = MutableLiveData()
    val firestore = FirebaseFirestore.getInstance()
    fun like(videoId: String, likedBy: String) {
        val likes = Likes(
            likedBy
        )
        firestore.collection(Constants.Videos)
            .document(videoId).collection(Constants.Likes)
            .document(likedBy).set(likes, SetOptions.merge())
            .addOnSuccessListener {
                likeVideoState.postValue(Resource.Success(null))
                DislikeVideo().removeDislike(videoId, likedBy)
            }

    }

    fun removeLike(videoId: String){
        firestore.collection(Constants.Videos)
            .document(videoId).collection(Constants.Likes)
            .document(ReusableResource.uid()).delete()
            .addOnSuccessListener {
                removeLikeVideoState.postValue(Resource.Success(null))
            }
    }

}