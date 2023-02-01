package com.example.testyoutube.utils

sealed class ItemAudioState<T> (val data: T? = null) {
    class ExchangeCurrentItem<T>(data: T? = null) : ItemAudioState<T>(data)
}