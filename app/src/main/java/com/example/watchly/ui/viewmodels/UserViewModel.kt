package com.example.watchly.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.watchly.db.Database
import com.example.watchly.models.User
import com.example.watchly.uils.Constants
import com.example.watchly.uils.Constants.USERS
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource
import com.example.watchly.uils.ReusableResource.uid
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class UserViewModel (
    application: Application
) : AndroidViewModel(application){

    private val firestore = FirebaseFirestore.getInstance()
    val userData : MutableLiveData<Resource<User>> = MutableLiveData()
    val updateUserDataState : MutableLiveData<Resource<User>> = MutableLiveData()
    private val db = Database.database(application)
    val app = application

    fun getUserData() = viewModelScope.launch {
        userData.postValue(Resource.Loading(null))
        firestore.collection(Constants.USERS)
            .document(ReusableResource.uid())
            .addSnapshotListener { value, error ->
                value?.let {
                    val user = it.toObject(User::class.java)
                    userData.postValue(Resource.Success(user))
                }
                error?.let {
                    userData.postValue(Resource.Error(it.message))
                }

            }
    }


    fun updateUserData(userMap: HashMap<String, Any>){
        updateUserDataState.postValue(Resource.Loading(null))
        firestore.collection(USERS)
            .document(uid())
            .update(userMap)
            .addOnSuccessListener {
                updateUserDataState.postValue(Resource.Success(null))
            }.addOnFailureListener {
                updateUserDataState.postValue(Resource.Error(it.message.toString()))
            }
    }




}