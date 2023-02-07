package com.example.testyoutube.ui.videoplayscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testyoutube.data.database.entity.ItemVideo
import com.example.testyoutube.data.exchange.VideoExchange
import com.example.testyoutube.utils.ExchangeItem
import com.example.testyoutube.utils.ItemState
import com.example.testyoutube.utils.ItemUiState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlayViewModel @Inject constructor(
    private val exchange: VideoExchange
) : ViewModel() {

    private var _state: MutableLiveData<ItemUiState> = MutableLiveData()
    val state: LiveData<ItemUiState> = _state

    var currentId: String = ""
    var lastPlayId: String = ""

    fun navigationVideo(bias: Int) {
        viewModelScope.launch {
            exchange
                .navigationVideo(bias)
                .collect(::handleExchange)
        }
    }

    private fun handleExchange(resource: ItemState<ItemVideo>) {
        when (resource) {
            is ItemState.ExchangeCurrentItem -> {
                if (resource.data != null) {
                    _state.value = ExchangeItem(resource.data)
                }
            }
        }
    }

}


