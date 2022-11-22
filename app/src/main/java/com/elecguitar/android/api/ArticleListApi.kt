package com.elecguitar.android.api

import com.elecguitar.android.response.ArticleResponse
import retrofit2.Call
import retrofit2.http.GET

interface ArticleListApi {

    @GET("article-api/article")
    fun getAllArticleList(): Call<List<ArticleResponse>>
}