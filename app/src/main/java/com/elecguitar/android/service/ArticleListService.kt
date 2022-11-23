package com.elecguitar.android.service

import com.elecguitar.android.dto.Car
import com.elecguitar.android.response.ArticleResponse
import com.elecguitar.android.util.RetrofitCallback
import com.elecguitar.android.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticleListService {
    fun getAllArticleList(callback: RetrofitCallback<List<ArticleResponse>>){
        RetrofitUtil.articleListService.getAllArticleList().enqueue(object : Callback<List<ArticleResponse>> {
            override fun onResponse(call: Call<List<ArticleResponse>>, response: Response<List<ArticleResponse>>) {
                val res = response.body()
                if (response.code() == 200) {
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<List<ArticleResponse>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }
}