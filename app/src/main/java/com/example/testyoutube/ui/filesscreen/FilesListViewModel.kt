package com.example.testyoutube.ui.filesscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testyoutube.audiodata.entity.ItemAudio
import com.example.testyoutube.audiodata.exchange.AudioExchange
import com.example.testyoutube.audiodata.repository.AudioRepository
import com.example.testyoutube.data.database.entity.ItemVideo
import com.example.testyoutube.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilesListViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
    private val exchange: AudioExchange
) : ViewModel() {

    private var _state: MutableLiveData<AudioUiState> = MutableLiveData()
    val state: LiveData<AudioUiState> = _state

    private var _stateItem: MutableLiveData<ItemAudioUiState> = MutableLiveData()
    val stateItem: LiveData<ItemAudioUiState> = _stateItem

    var currentPlayAudio: ItemAudio? = null
    fun getAllAudioFromDevice() {
        viewModelScope.launch {
            audioRepository
                .getAllAudioFromDevice()
                .collect(::handleAudioResponse)
        }
    }

    private fun handleAudioResponse(resourse: AudioListState<List<ItemAudio>>) {
        when (resourse) {
            is AudioListState.Error -> {
                _state.value = AudioError(resourse.message)
            }
            is AudioListState.Loading -> {
                _state.value = AudioLoading(resourse.isLoading)
            }
            is AudioListState.Success -> {
                _state.value = AudioLoaded(resourse.data ?: emptyList())
            }
        }
    }

    fun navigationAudio(bias: Int) {
        viewModelScope.launch {
            exchange
                .navigationAudio(bias)
                .collect(::handleExchange)
        }
    }

    private fun handleExchange(resource: ItemAudioState<ItemAudio>) {
        when (resource) {
            is ItemAudioState.ExchangeCurrentItem -> {
                if (resource.data != null) {
                    _stateItem.value = ExchangeAudioItem(resource.data)
                }
            }
        }
    }

    fun setCurrentAudio(item: ItemAudio) {
        exchange.setCurrentAudio(item)
    }

    fun getCurrentAudio() : ItemAudio? = exchange.getCurrentAudio()

    fun setCurrentList(list: List<ItemAudio>) {
        exchange.setCurrentList(list)
    }


}