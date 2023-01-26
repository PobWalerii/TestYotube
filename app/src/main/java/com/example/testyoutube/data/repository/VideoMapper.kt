package com.example.testyoutube.data.repository

import com.example.testyoutube.data.apimodel.YoutubeResponse
import com.example.testyoutube.data.database.entity.ItemVideo

class VideoMapper(
    private val response: YoutubeResponse
) {
    fun mapToVideoList(): List<ItemVideo> {
        val list = mutableListOf<ItemVideo>()
        var ii: Long = 0
        response.items.forEach {item ->
            list.add(
                ItemVideo(
                    ii++,
                    item.id.videoId,
                    item.snippet.channelTitle,
                    item.snippet.title,
                    item.snippet.description,
                    item.snippet.thumbnails.medium.url,
                    item.snippet.thumbnails.medium.width,
                    item.snippet.thumbnails.medium.height,
                    item.snippet.thumbnails.default.url,
                    item.snippet.thumbnails.default.width,
                    item.snippet.thumbnails.default.height
                )
            )
        }
        return list
    }
}