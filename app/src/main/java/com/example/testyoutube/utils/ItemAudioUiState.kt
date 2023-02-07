package com.example.testyoutube.utils

import com.example.testyoutube.audiodata.entity.ItemAudio

sealed class ItemAudioUiState
    data class ExchangeAudioItem(val data: ItemAudio) : ItemAudioUiState()

