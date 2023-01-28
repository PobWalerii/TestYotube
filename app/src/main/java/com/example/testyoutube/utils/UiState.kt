package com.example.delivery.utils

import com.example.testyoutube.data.database.entity.ItemVideo


sealed class ListUiState
    data class ListLoaded(val data: List<ItemVideo>) : ListUiState()
    data class ListFromBase(val data: List<ItemVideo>) : ListUiState()
    data class ListLoading(val isLoading: Boolean) : ListUiState()
    data class ListError(val message: String?) : ListUiState()


