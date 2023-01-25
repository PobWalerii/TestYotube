package com.example.testyoutube.data.api

import com.example.testyoutube.data.apimodel.YoutubeResponse
import com.example.testyoutube.utils.Constants.API_KEY
import com.example.testyoutube.utils.Constants.API_ORDER
import com.example.testyoutube.utils.Constants.API_PAGING_SIZE
import com.example.testyoutube.utils.Constants.API_PART
import com.example.testyoutube.utils.Constants.API_TYPE_CONTENT

class ApiHelper (private val apiService: ApiService) {
    suspend fun getYoutube(keyWord: String): YoutubeResponse =
        apiService.getYoutube(
            API_PART,
            API_PAGING_SIZE,
            API_TYPE_CONTENT,
            keyWord,
            API_ORDER,
            API_KEY
    )
}