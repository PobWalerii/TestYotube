package com.example.testyoutube.data.repository

import com.example.delivery.utils.ResponseState
import com.example.testyoutube.data.api.ApiService
import com.example.testyoutube.data.apimodel.YoutubeResponse
import com.example.testyoutube.data.videolistitem.ItemVideo
import com.example.testyoutube.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val apiService: ApiService
){
    fun getVideoList(keyWord: String): Flow<ResponseState<List<ItemVideo>>> {
        return flow {
            emit(ResponseState.Loading(true))
            try {
                val response: YoutubeResponse = apiService.getYoutube(
                    Constants.API_PART,
                    Constants.API_PAGING_SIZE,
                    Constants.API_TYPE_CONTENT,
                    keyWord,
                    Constants.API_ORDER,
                    Constants.API_KEY
                )
                val list: List<ItemVideo> = VideoMapper(response).mapToVideoList()
                emit(ResponseState.Success(list))
            } catch (exception: Exception) {
                emit(ResponseState.Error(exception.message ?: "Data loading error!"))
            }
            emit(ResponseState.Loading(false))
        }
    }

}