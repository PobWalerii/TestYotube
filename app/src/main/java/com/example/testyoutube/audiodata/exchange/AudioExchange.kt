package com.example.testyoutube.audiodata.exchange

import com.example.testyoutube.audiodata.entity.ItemAudio
import com.example.testyoutube.utils.ItemAudioState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AudioExchange {
    private var currentItem: ItemAudio? = null
    private var fullListAudio: List<ItemAudio> = emptyList()
    private var listAudio: List<ItemAudio> = emptyList()

    fun setCurrentAudio(item: ItemAudio?) {
        currentItem = item
    }
    fun getCurrentAudio(): ItemAudio? = currentItem

    fun setCurrentList(list: List<ItemAudio>) {
        listAudio = list
    }

    fun setFullList(list: List<ItemAudio>) {
        fullListAudio = list
        listAudio = list
    }
    fun getFullList(): List<ItemAudio> = fullListAudio

    fun navigationAudio(bias: Int): Flow<ItemAudioState<ItemAudio>> {
        return flow {
            val newIndex = listAudio.indexOf(currentItem) + bias
            if (newIndex >= 0 && newIndex < listAudio.size) {
                currentItem = listAudio[newIndex]
            }
            emit(ItemAudioState.ExchangeCurrentItem(currentItem))
        }
    }

}