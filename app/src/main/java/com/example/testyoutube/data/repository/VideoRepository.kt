package com.example.testyoutube.data.repository

import android.content.Context
import android.widget.Toast
import com.example.testyoutube.R
import com.example.testyoutube.data.api.ApiService
import com.example.testyoutube.data.apimodel.YoutubeResponse
import com.example.testyoutube.data.database.dao.VideoDao
import com.example.testyoutube.data.database.entity.ItemVideo
import com.example.testyoutube.utils.Constants
import com.example.testyoutube.utils.ResponseState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val apiService: ApiService,
    private val videoDao: VideoDao,
    @ApplicationContext private val context: Context
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
                if(list.isNotEmpty()) {
                    saveToDatabase(list)
                }
            } catch (exception: Exception) {
                //val textError=exception.message.toString()
                //Toast.makeText(context,textError, Toast.LENGTH_LONG).show()
                //val stringError: String = this@VideoRepository.context.getString(R.string.data_error) +
                //    if(textError=="HTTP 400" || textError=="HTTP 401" || textError=="HTTP 403" || textError=="HTTP 404") {
                //        " ($textError)"
                //    } else {
                //        " "+ this@VideoRepository.context.getString(R.string.internet_error)
                //    }
                //stringError

                emit(ResponseState.Error(exception.message ?: "Data loading error!"))
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