package com.example.testyoutube.data.apimodel


import com.google.gson.annotations.SerializedName

class Thumbnails(
    @SerializedName("default")
    val default: Default,
    @SerializedName("high")
    val high: High,
    @SerializedName("medium")
    val medium: Medium
)