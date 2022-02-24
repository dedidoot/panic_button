package com.panic.button.core.api.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(@SerializedName("errors") val errorModel: String? = null) : BaseErrorResponse()