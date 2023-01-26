package com.example.testyoutube.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ItemVideo(
    @PrimaryKey
    val id: Long,
    val videoId: String,
    val channelTitle: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val imageWidth: Int,
    val imageHeight: Int,
    val imageDefaultUrl: String,
    val imageDefaultWidth: Int,
    val imageDefaultHeight: Int
)
