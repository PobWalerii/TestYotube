package com.example.testyoutube.data.exchange

import com.example.testyoutube.data.database.entity.ItemVideo

class VideoExchange {
    var currentItem: ItemVideo? = null
    var listVideo: List<ItemVideo> = emptyList()

    fun setCurrentVideo(item: ItemVideo) {
        currentItem = item
    }
    fun getCurrentVideo(): ItemVideo? = currentItem

    fun setCurrentList(list: List<ItemVideo>) {
        listVideo = list
    }
}