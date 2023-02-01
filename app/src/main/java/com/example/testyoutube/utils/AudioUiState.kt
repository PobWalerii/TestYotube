package com.example.testyoutube.utils

import com.example.testyoutube.audiodata.entity.ItemAudio

sealed class AudioUiState
    data class AudioLoaded(val data: List<ItemAudio>) : AudioUiState()
    data class AudioError(val message: String?) : AudioUiState()