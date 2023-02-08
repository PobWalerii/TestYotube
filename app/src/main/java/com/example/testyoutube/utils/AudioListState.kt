package com.example.testyoutube.utils

sealed class AudioListState<T> (val data: T? = null, val message: String? = null){
    class Success<T>(data: T?) : AudioListState<T>(data)
    class Error<T>(message: String?, data: T? = null) : AudioListState<T>(data, message)
    class SearchFiles<T>(var isSearch: Boolean) : AudioListState<T>(null)
}
