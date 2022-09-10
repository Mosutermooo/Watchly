package com.example.watchly.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.watchly.models.User
import com.example.watchly.uils.AuthState
import com.example.watchly.uils.Constants.USERS
import com.example.watchly.uils.ReusableResource.signOut
import com.example.watchly.uils.ReusableResource.uid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch

class AuthViewModel(
    application: Application
) : AndroidViewModel(application){

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val state : MutableLiveData<AuthState> = MutableLiveData()
    val registerState : MutableLiveData<AuthState> = MutableLiveData()
    val isLoggedInState : MutableLiveData<AuthState> = MutableLiveData()

    fun login(email: String, password: String) = viewModelScope.launch {
        state.postValue(AuthState.Loading)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {request ->
                if(request.isSuccessful){
                    state.postValue(AuthState.Success)
                }else{
                    state.postValue(AuthState.Error(null))
                }
            }.addOnFailureListener {
                state.postValue(AuthState.Error(it))
            }
    }




    fun register(email: String, password: String) = viewModelScope.launch {
        registerState.postValue(AuthState.Loading)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { request ->
                if(request.isSuccessful){
                    val user = User(
                        null,
                        null,
                        null,
                        uid =  uid(),
                        email = request.result.user?.email,
                        registrationState = "not finished"
                    )
                    createUserInFirestore(user)
                }else{
                    registerState.postValue(AuthState.Error(null))
                }
            }.addOnFailureListener {
                registerState.postValue(AuthState.Error(it))
            }
    }

    private fun createUserInFirestore(user: User){
        firestore.collection(USERS).document(uid()).set(user, SetOptions.merge())
            .addOnSuccessListener {
                registerState.postValue(AuthState.Success)
                signOut()
            }.addOnFailureListener {
                registerState.postValue(AuthState.Error(it))
            }
    }

    fun isLoggedIn(){
        if(auth.currentUser?.uid != null){
            isLoggedInState.value = AuthState.IsSignedIn
        }else{
            isLoggedInState.value = AuthState.IsNotSignedIn
        }
    }




}