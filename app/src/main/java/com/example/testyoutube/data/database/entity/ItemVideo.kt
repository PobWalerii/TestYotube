package com.example.testyoutube.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ItemVideo(
    @PrimaryKey
    val id: Long,
    val videoId: String,
    val title: String,
    val channelTitle: String,
    val description: String,
    val imageUrl: String,
    val imageWidth: Int,
    val imageHeight: Int,
)
