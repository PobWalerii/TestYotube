package com.example.testyoutube.data.apimodel


import com.google.gson.annotations.SerializedName

class High(
    @SerializedName("height")
    val height: Int,
    @SerializedName("url")
    val url: String,
    @SerializedName("width")
    val width: Int
)