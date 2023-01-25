package com.example.delivery.utils

import com.example.testyoutube.data.videolistitem.ItemVideo


sealed class UiState
    data class Loaded(val data: List<ItemVideo>) : UiState()
    data class Loading(val isLoading: Boolean) : UiState()
    data class Error(val exception: Exception) : UiState()

