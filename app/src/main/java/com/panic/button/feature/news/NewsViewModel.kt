package com.panic.button.feature.news

import com.panic.button.core.api.GSONManager
import com.panic.button.core.api.GetRequest
import com.panic.button.core.api.response.*
import com.panic.button.core.base.*
import com.panic.button.core.model.Urls

class NewsViewModel : BaseViewModel() {

    val newsResponse = mutableLiveDataOf<NewsResponse>()
    val isLoading = mutableLiveDataOf<Boolean>()
    private val pages = mutableLiveDataOf<Int>()

    init {
        pages.value = 1
    }

    fun getNews() {
        isLoading.value = true
        val request = GetRequest(Urls.GET_NEWS)
        request.queries["page"] = "${pages.value}"
        request.get({
            newsResponse.value = GSONManager.fromJson(it, NewsResponse::class.java)
            isLoading.value = false
        })
    }

    fun getNewsModel(): ArrayList<NewsItemModel> {
        return newsResponse.value?.newsModel?.items ?: arrayListOf()
    }

    fun updatePage() {
        pages.value?.apply {
            pages.value = this + 1
        }
    }

    fun isFirstPage() : Boolean {
        return pages.value == 1
    }
}