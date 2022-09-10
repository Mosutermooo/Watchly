package com.example.watchly.use_cases

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.watchly.models.Likes
import com.example.watchly.uils.Constants
import com.example.watchly.uils.Resource
import com.google.firebase.firestore.FirebaseFirestore

class AllLikes {
    private val firestore = FirebaseFirestore.getInstance()
    val allLikesState: MutableLiveData<Resource<List<Likes>>> = MutableLiveData()
    fun get(videoId: String){
        allLikesState.postValue(Resource.Loading(null))
        firestore.collection(Constants.Videos)
            .document(videoId).collection(Constants.Likes)
            .addSnapshotListener { value, error ->
                value?.let {
                    val likes = it.toObjects(Likes::class.java)
                    Log.e("likes 32", "${likes}")
                    Log.e("likes 3223", "${likes.size}")
                    allLikesState.postValue(Resource.Success(likes))
                }
                error?.let {
                    allLikesState.postValue(Resource.Error(it.message))
                }

            }

    }
}