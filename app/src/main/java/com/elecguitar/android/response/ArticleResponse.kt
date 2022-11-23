package com.elecguitar.android.response

import com.google.gson.annotations.SerializedName

data class ArticleResponse(
    @SerializedName("articleId")val articleId: Int,
    @SerializedName("title")val title: String,
    @SerializedName("writer")val writer: String,
    @SerializedName("content")val content: String,
    @SerializedName("time")val time: String,
    @SerializedName("img")val img: String
)
