package com.example.testyoutube.data.exchange

import com.example.testyoutube.data.database.entity.ItemVideo
import com.example.testyoutube.utils.ItemState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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

    fun navigationVideo(bias: Int): Flow<ItemState<ItemVideo>> {
        return flow {
            val newIndex = listVideo.indexOf(currentItem) + bias
            if (newIndex >= 0 && newIndex < listVideo.size) {
                currentItem = listVideo[newIndex]
            }
            emit(ItemState.ExchangeCurrentItem(currentItem))
        }
    }

}