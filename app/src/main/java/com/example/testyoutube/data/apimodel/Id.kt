package com.example.testyoutube.data.apimodel


import com.google.gson.annotations.SerializedName

class Id(
    @SerializedName("kind")
    val kind: String,
    @SerializedName("videoId")
    val videoId: String
)