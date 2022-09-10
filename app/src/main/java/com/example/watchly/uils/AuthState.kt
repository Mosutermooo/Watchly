package com.example.watchly.uils

import java.lang.Exception

sealed class AuthState(val exception: Exception? = null) {

    object Success : AuthState(null)
    class Error(data: Exception?) : AuthState(data)
    object Loading : AuthState(null)
    object IsSignedIn : AuthState(null)
    object IsNotSignedIn : AuthState(null)

}