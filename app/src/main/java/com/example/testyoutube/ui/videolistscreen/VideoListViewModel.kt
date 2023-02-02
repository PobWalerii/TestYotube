package com.example.testyoutube.ui.videolistscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testyoutube.data.repository.VideoRepository
import com.example.testyoutube.data.database.entity.ItemVideo
import com.example.testyoutube.data.exchange.VideoExchange
import com.example.testyoutube.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val repository: VideoRepository,
    private val exchange: VideoExchange
) : ViewModel() {

    private var _stateList: MutableLiveData<ListUiState> = MutableLiveData()
    val stateList: LiveData<ListUiState> = _stateList

    var keyWord = ""
    var isStarted = false
    //var isBaseLoaded = false

    fun setCurrentVideo(item: ItemVideo) {
        exchange.setCurrentVideo(item)
    }
    fun getCurrentVideo(): ItemVideo? = exchange.getCurrentVideo()

    fun getSizeVideoList() = exchange.getSizeList()

    fun setCurrentList(list: List<ItemVideo>) {
        exchange.setCurrentList(list)
    }

    fun getVideoList(keyWord: String) {
        viewModelScope.launch {
            repository
                .getVideoList(keyWord)
                .collect(::handleResponse)
        }
    }

    fun getVideoFromDatabase() {
        viewModelScope.launch {
            repository
                .getVideoFromDatabase()
                .collect(::handleResponse)
        }
    }

    private fun handleResponse(resourse: ResponseState<List<ItemVideo>>) {
        when (resourse) {
            is ResponseState.Error -> {
                _stateList.value = ListError(resourse.message)
            }
            is ResponseState.Loading -> {
                _stateList.value = ListLoading(resourse.isLoading)
            }
            is ResponseState.Success -> {
                _stateList.value = ListLoaded(resourse.data ?: emptyList())
            }
            is ResponseState.Database -> {
                _stateList.value = ListFromBase(resourse.data ?: emptyList())
            }
        }
    }
}