package com.example.testyoutube.utils

sealed class ItemState<T> (val data: T? = null) {
    class ExchangeCurrentItem<T>(data: T? = null) : ItemState<T>(data)
}