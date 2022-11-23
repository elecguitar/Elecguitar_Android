package com.elecguitar.android.util

import androidx.lifecycle.MutableLiveData
import com.elecguitar.android.response.ArticleResponse

class ArticleListLiveData : MutableLiveData<MutableList<ArticleResponse>>() {
    private val datas = mutableListOf<ArticleResponse>()

    init {
        value = datas
    }

    fun addAll(items: List<ArticleResponse>) {
        datas.addAll(items)
        value = datas
    }
}
