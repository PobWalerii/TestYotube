package com.example.testyoutube.data.videolistitem

data class ItemVideo(
    val id: Long,
    val videoId: String,
    val channelTitle: String,
    val title: String,
    val description: String,
    val imageurl: String,
    val imagewidth: Int,
    val imageheight: Int
)
