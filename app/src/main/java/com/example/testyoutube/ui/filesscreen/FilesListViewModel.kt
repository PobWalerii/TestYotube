package com.example.testyoutube.ui.filesscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testyoutube.audiodata.entity.ItemAudio
import com.example.testyoutube.audiodata.repository.AudioRepository
import com.example.testyoutube.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilesListViewModel @Inject constructor(
    private val audioRepository: AudioRepository
) : ViewModel() {

    private var _state: MutableLiveData<AudioUiState> = MutableLiveData()
    val state: LiveData<AudioUiState> = _state

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




}