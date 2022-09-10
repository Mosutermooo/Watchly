package com.example.watchly.use_cases

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.watchly.models.Likes
import com.example.watchly.uils.Constants
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource
import com.google.firebase.firestore.FirebaseFirestore

class IsVideoAlreadyLiked {
    val alreadyLikedState: MutableLiveData<Resource<Likes>> = MutableLiveData()
    private val firestore = FirebaseFirestore.getInstance()
    fun check(videoId:String){
        firestore.collection(Constants.Videos)
            .document(videoId).collection(Constants.Likes)
            .document(ReusableResource.uid()).addSnapshotListener { value, _ ->
                value?.let {
                    if(it.exists()) {
                        Log.e("ïn alr ex", "$it")
                        val like = it.toObject(Likes::class.java)
                        alreadyLikedState.postValue(Resource.Success(like))
                    }else{
                        alreadyLikedState.postValue(Resource.Error(null))
                    }
                }
            }
    }
}