package com.example.testyoutube.data.repository

import com.example.testyoutube.data.api.ApiService
import com.example.testyoutube.data.apimodel.YoutubeResponse
import com.example.testyoutube.data.database.dao.VideoDao
import com.example.testyoutube.data.database.entity.ItemVideo
import com.example.testyoutube.utils.Constants
import com.example.testyoutube.utils.ResponseState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val apiService: ApiService,
    private val videoDao: VideoDao,
){
    fun getVideoList(keyWord: String): Flow<ResponseState<List<ItemVideo>>> {
        return flow {
            emit(ResponseState.Loading(true))
            try {
                val response: YoutubeResponse = apiService.getYoutube(
                    Constants.API_PART,
                    Constants.API_PAGING_SIZE,
                    Constants.API_TYPE_CONTENT,
                    Constants.CATEGORY_ID,
                    keyWord,
                    Constants.API_ORDER,
                    Constants.API_KEY
                )
                val list: List<ItemVideo> = VideoMapper(response).mapToVideoList()
                emit(ResponseState.Success(list))
                if(list.isNotEmpty()) {
                    saveToDatabase(list)
                }
            } catch (exception: Exception) {
                emit(ResponseState.Error(exception.message))
            }

            emit(ResponseState.Loading(false))
        }
    }

    fun getVideoFromDatabase(): Flow<ResponseState<List<ItemVideo>>> {
        return flow {
            val list: List<ItemVideo> = videoDao.getVideoList()
            emit(ResponseState.Database(list))
        }
    }

    private suspend fun saveToDatabase(list: List<ItemVideo>) {
        videoDao.deleteAll()
        list.map {
            videoDao.insertVideo(it)
        }
    }

}