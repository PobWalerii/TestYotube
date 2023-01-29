package com.example.testyoutube.utils

import com.example.testyoutube.data.database.entity.ItemVideo

sealed class ItemUiState
    data class ExchangeItem(val data: ItemVideo) : ItemUiState()

