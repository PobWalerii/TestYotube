package com.example.testyoutube.utils

import com.example.testyoutube.audiodata.entity.AudioFiles

sealed class AudioUiState
    data class AudioListLoaded(val data: List<AudioFiles>) : AudioUiState()
    data class AudioListLoading(val isLoading: Boolean) : AudioUiState()
    data class AudioListError(val message: String?) : AudioUiState()


