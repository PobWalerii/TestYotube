package com.example.testyoutube.data.apimodel


import com.google.gson.annotations.SerializedName

class Item(
    @SerializedName("etag")
    val etag: String,
    @SerializedName("id")
    val id: Id,
    @SerializedName("kind")
    val kind: String,
    @SerializedName("snippet")
    val snippet: Snippet
)