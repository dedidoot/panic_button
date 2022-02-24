package com.panic.button.feature.home

import com.panic.button.core.api.GSONManager
import com.panic.button.core.api.GetRequest
import com.panic.button.core.api.response.NewsItemModel
import com.panic.button.core.api.response.NewsResponse
import com.panic.button.core.base.BaseViewModel
import com.panic.button.core.base.mutableLiveDataOf
import com.panic.button.core.model.Urls

class HomeViewModel : BaseViewModel() {

    val newsResponse = mutableLiveDataOf<NewsResponse>()
    val pages = mutableLiveDataOf<Int>()

    init {
        pages.value = 1
    }

    fun getNews() {
        val request = GetRequest(Urls.GET_NEWS)
        request.queries["page"] = "${pages.value}"
        request.get({
            newsResponse.value = GSONManager.fromJson(it, NewsResponse::class.java)
        })
    }

    fun getNewsModel(): ArrayList<NewsItemModel> {
        return newsResponse.value?.newsModel?.items ?: arrayListOf()
    }
}