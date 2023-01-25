package com.example.testyoutube.ui.youtubescreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.engine.Resource
import com.example.delivery.utils.ResponseState
import com.example.delivery.utils.UiState
import com.example.testyoutube.data.repository.VideoRepository
import com.example.testyoutube.data.videolistitem.ItemVideo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val repository: VideoRepository
): ViewModel(){

    private var _state: MutableLiveData<UiState> = MutableLiveData()
    val state: LiveData<UiState> = _state

    fun getVideoList(keyWord: String) {
        viewModelScope.launch {
            repository
                .getVideoList(keyWord)
                .collect(::handleResponse)
        }
    }


    private fun handleResponse(resourse: ResponseState<List<ItemVideo>>) {
        when (resourse) {
            is ResponseState.Error -> {

            }
            is ResponseState.Loading -> {

            }
            is ResponseState.Success -> {

            }
        }



    }


}