package com.panic.button.core.model

import com.google.gson.annotations.SerializedName

data class SubmitReportModel (@SerializedName("call_id") val callId: String? = null,
                              @SerializedName("title") val title: String? = null,
                              @SerializedName("description") val description: String? = null) {


    companion object {
        const val SUCCESS = "success"
    }
}