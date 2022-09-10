package com.example.watchly.use_cases

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.watchly.models.Likes
import com.example.watchly.uils.Constants
import com.example.watchly.uils.Resource
import com.google.firebase.firestore.FirebaseFirestore

class AllDislikes {
    private val firestore = FirebaseFirestore.getInstance()
    val allDislikesState: MutableLiveData<Resource<List<Likes>>> = MutableLiveData()
    fun get(videoId: String){
        allDislikesState.postValue(Resource.Loading(null))
        firestore.collection(Constants.Videos)
            .document(videoId).collection(Constants.Dislikes)
            .addSnapshotListener { value, error ->
                value?.let {
                    val likes = it.toObjects(Likes::class.java)
                    Log.e("likes 32", "${likes}")
                    Log.e("likes 3223", "${likes.size}")
                    allDislikesState.postValue(Resource.Success(likes))
                }
                error?.let {
                    allDislikesState.postValue(Resource.Error(it.message))
                }

            }

    }
}