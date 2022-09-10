package com.example.watchly.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.watchly.db.Database
import com.example.watchly.models.*
import com.example.watchly.uils.Constants
import com.example.watchly.uils.Constants.USERS
import com.example.watchly.uils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firestore.v1.Document
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SearchViewModel(app: Application) : AndroidViewModel(app) {

    private val db = Database.database(app).dbDao()
    private val firestore = FirebaseFirestore.getInstance()
    val feedDataState : MutableStateFlow<Resource<MutableList<SearchItemView>>> = MutableStateFlow(Resource.Idle())

    fun addLatestQueryToDB(query: String?) = viewModelScope.launch {
        val c: Calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val strDate: String = sdf.format(c.time)
        if(query != null && query != ""){
            val search = Search(
                null,
                query,
                strDate
            )

            db.query(search)
        }
    }
    fun deleteQueryFromDB(id: Int) = viewModelScope.launch { db.deleteQuery(id) }
    fun quarries(): LiveData<List<Search>> {
        return db.quarries()
    }
    fun deleteEveryQuery() = viewModelScope.launch { db.deleteEveryQuery() }

    fun getFeedData() = viewModelScope.launch {
        feedDataState.emit(Resource.Loading(null))
        firestore.collection(Constants.Channels)
            .limit(20)
            .get().addOnSuccessListener { channelSnapShot ->
                val channel = channelSnapShot.toObjects(SearchItemView.Channel::class.java)
                getVideoData(channel)
            }
    }
    private fun getVideoData(channel: List<SearchItemView.Channel>) {
        firestore.collection(Constants.Videos)
            .whereGreaterThan("views", 20)
            .whereEqualTo("visibility", "public")
            .orderBy("views", Query.Direction.DESCENDING)
            .limit(20)
            .get().addOnSuccessListener { videosSnapShot ->
                val videos = videosSnapShot.toObjects(SearchItemView.Video::class.java)
                getUserData(videos, channel)
            }
    }


    private fun getUserData(
        videos: List<SearchItemView.Video>,
        channel: List<SearchItemView.Channel>,
    ) {
        val feedData = mutableListOf<SearchItemView>()
        firestore.collection(USERS)
            .limit(20)
            .get().addOnSuccessListener { userSnapShot ->
                val users = userSnapShot.toObjects(SearchItemView.User::class.java)
                feedData.add(SearchItemView.Sections(1, "Videos"))
                feedData.addAll(videos)
                feedData.add(SearchItemView.Sections(2, "Channels"))
                feedData.addAll(channel)
                feedData.add(SearchItemView.Sections(3, "Users"))
                feedData.addAll(users)
                viewModelScope.launch {
                    feedDataState.emit(Resource.Success(feedData))
                }
            }
    }

}