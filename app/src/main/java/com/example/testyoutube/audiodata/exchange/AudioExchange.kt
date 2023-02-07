package com.example.testyoutube.audiodata.exchange

import com.example.testyoutube.audiodata.entity.ItemAudio
import com.example.testyoutube.data.database.entity.ItemVideo
import com.example.testyoutube.utils.ItemAudioState
import com.example.testyoutube.utils.ItemState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AudioExchange {
    private var currentItem: ItemAudio? = null
    private var listAudio: List<ItemAudio> = emptyList()

    fun setCurrentAudio(item: ItemAudio) {
        currentItem = item
    }

    fun getCurrentAudio(): ItemAudio? = currentItem

    fun setCurrentList(list: List<ItemAudio>) {
        listAudio = list
    }
    fun getSizeList() = listAudio.size

    fun navigationAudio(bias: Int): Flow<ItemAudioState<ItemAudio>> {
        return flow {
            val newIndex = listAudio.indexOf(currentItem) + bias
            if (newIndex >= 0 && newIndex < listAudio.size) {
                currentItem = listAudio[newIndex]
            }
            emit(ItemAudioState.ExchangeCurrentItem(currentItem))
        }
    }

    fun findItemForName(textSearch: String): ItemAudio? {
        val item: ItemAudio? = listAudio.find { audio -> audio.name?.substring(0,textSearch.length)?.toUpperCase() == textSearch.toUpperCase() }
        return item
    }

}