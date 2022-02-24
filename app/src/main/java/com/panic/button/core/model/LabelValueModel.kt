package com.panic.button.core.model

import com.google.gson.annotations.SerializedName
import com.panic.button.core.api.response.BaseResponse

data class LabelValueModel(@SerializedName("label") val label: String? = null,
                           @SerializedName("value") val value: String? = null) : BaseResponse()