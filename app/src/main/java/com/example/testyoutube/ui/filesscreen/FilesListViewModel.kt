package com.example.testyoutube.ui.filesscreen

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testyoutube.audiodata.entity.AudioFiles
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

    fun getAllAudioFromDevice(context: Context) {
        viewModelScope.launch {
            audioRepository
                .getAllAudioFromDevice(context)
                .collect(::handleAudioResponse)
        }
    }

    private fun handleAudioResponse(resourse: AudioListState<List<AudioFiles>>) {
        when (resourse) {
            is AudioListState.Error -> {
                _state.value = AudioListError(resourse.message)
            }
            is AudioListState.Loading -> {
                _state.value = AudioListLoading(resourse.isLoading)
            }
            is AudioListState.Success -> {
                _state.value = AudioListLoaded(resourse.data ?: emptyList())
            }
        }
    }



}