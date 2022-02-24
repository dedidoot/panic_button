package com.panic.button.core.api

import com.panic.button.core.base.getDetailReportDevice
import com.panic.button.core.model.ApiReportModel
import com.panic.button.core.model.Urls
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

object ReportRequest {

    private val request = RestClient.createStringService(UrlApi::class.java, Urls.API_REPORT)
    private const val maxLength = 4065

    suspend fun post(text : String) {
        try {
            var realText = text
            if (realText.length > maxLength) {
                realText = realText.substring(0, maxLength)
            }

            val params = ApiReportModel(chatId = Urls.CHAT_ID, text = realText)
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                GSONManager.toJson(params)
            )
            val map = HashMap<String, String>()
            request.postRequestSuspendApi(Urls.API_REPORT + Urls.API_BOT, map, map, body)
        } catch (throwable : Throwable) {
            throwable.printStackTrace()
        }
    }

    suspend fun reportPostWithInformationDevice(text : String) {
        post(getDetailReportDevice())
        post(text)
    }
}