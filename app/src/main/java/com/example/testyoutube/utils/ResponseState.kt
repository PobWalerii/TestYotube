package com.example.delivery.utils

sealed class ResponseState<T> (val data: T? = null, val exception: Exception? = null){
    class Success<T>(data: T?) : ResponseState<T>(data)
    class Loading<T>(val isLoading: Boolean) : ResponseState<T>(null)
    class Error<T>(exception: Exception?, data: T? = null) : ResponseState<T>(data, exception)
}
