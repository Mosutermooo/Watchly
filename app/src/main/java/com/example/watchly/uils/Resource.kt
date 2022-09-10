package com.example.watchly.uils

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {

    class Success<T>(data: T?) : Resource<T>(data = data)
    class Error<T>(message: String?) : Resource<T>(message = message)
    class Loading<T>(data: T?) : Resource<T>(data = data)
    class Idle<T>() : Resource<T>()

}