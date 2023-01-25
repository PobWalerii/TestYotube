package com.example.testyoutube.data.api

import com.example.testyoutube.data.apimodel.YoutubeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("search")
    suspend fun getYoutube(
        @Query("part") part: String,
        @Query("maxResults") listSize: Int,
        @Query("type") type: String,
        @Query("q") keyWord: String,
        @Query("order") order: String,
        @Query("key") key: String
    ): YoutubeResponse

}