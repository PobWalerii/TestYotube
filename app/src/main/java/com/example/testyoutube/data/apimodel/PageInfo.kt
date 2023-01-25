package com.example.testyoutube.data.apimodel


import com.google.gson.annotations.SerializedName

class PageInfo(
    @SerializedName("resultsPerPage")
    val resultsPerPage: Int,
    @SerializedName("totalResults")
    val totalResults: Int
)