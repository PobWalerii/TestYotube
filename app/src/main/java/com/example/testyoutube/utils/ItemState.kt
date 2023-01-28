package com.example.delivery.utils

sealed class ItemState<T> (val data: T? = null) {
    class ExchangeCurrentItem<T>(data: T? = null) : ItemState<T>(data)
}