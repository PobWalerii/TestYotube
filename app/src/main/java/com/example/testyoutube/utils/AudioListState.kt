package com.example.testyoutube.utils

sealed class AudioListState<T> (val data: T? = null, val message: String? = null){
    class Success<T>(data: T?) : AudioListState<T>(data)
    class Loading<T>(val isLoading: Boolean) : AudioListState<T>(null)
    class Error<T>(message: String?, data: T? = null) : AudioListState<T>(data, message)
}
