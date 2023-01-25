package com.example.testyoutube.data.repository

import com.example.delivery.utils.ResponseState
import com.example.testyoutube.data.api.ApiService
import com.example.testyoutube.data.videolistitem.ItemVideo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VideoRepository @Inject constructor(
    apiService: ApiService

){

    fun getVideoList(keyWord: String): Flow<ResponseState<List<ItemVideo>>> {
        return flow {



        }
    }



}