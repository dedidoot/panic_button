package com.panic.button.feature.reportpolice

import com.panic.button.core.api.GSONManager
import com.panic.button.core.api.GetRequest
import com.panic.button.core.api.response.*
import com.panic.button.core.base.*
import com.panic.button.core.model.Urls

class HistoryReportViewModel : BaseViewModel() {

    val historyResponse = mutableLiveDataOf<HistoryResponse>()
    val isLoading = mutableLiveDataOf<Boolean>()
    private val pages = mutableLiveDataOf<Int>()

    init {
        pages.value = 1
    }

    fun getReportHistory() {
        isLoading.value = true
        val request = GetRequest(Urls.GET_INCIDENT_LIST)
        request.queries["creator_id"] = "${BaseApplication.sessionManager?.userId}"
        request.get({ responseText ->
            val response = GSONManager.fromJson(responseText, HistoryResponse::class.java)
            historyResponse.value = response
            isLoading.value = false
        })
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