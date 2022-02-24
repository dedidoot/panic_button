package com.panic.button.core.model

import com.google.gson.annotations.SerializedName

class ApiReportModel(
    @SerializedName("chat_id")
    var chatId: String? = null,
    @SerializedName("text")
    var text: String? = null
)