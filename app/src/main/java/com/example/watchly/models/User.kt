package com.example.watchly.models

data class User (
    val name: String? = null,
    val lastname: String? = null,
    val username: String? = null,
    val uid: String? = null,
    val email: String? = null,
    val image: String? = null,
    val channel: String? = null,
    val registrationState: String? = null,
    val fcmToken: String? = null
        )