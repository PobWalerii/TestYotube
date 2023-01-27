package com.example.testyoutube.ui.videoplayscreen

import androidx.lifecycle.ViewModel
import com.example.testyoutube.data.database.entity.ItemVideo
import com.example.testyoutube.data.exchange.VideoExchange
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VideoPlayViewModel @Inject constructor(
    private val exchange: VideoExchange
) : ViewModel() {

    fun getCurrentVideo(): ItemVideo? = exchange.getCurrentVideo()
    fun setCurrentVideo(item: ItemVideo) {
        exchange.setCurrentVideo(item)
    }








}