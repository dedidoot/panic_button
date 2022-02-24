package com.panic.button.core.api.response

import com.google.gson.annotations.SerializedName
import com.panic.button.core.model.GeneralModel

data class LostLetterTypeResponse(@SerializedName("data") val data: ArrayList<GeneralModel>? = null) : BaseResponse()

data class LostLetterFieldResponse(@SerializedName("data") val data: ArrayList<FieldModel>? = null) : BaseResponse()

data class FieldModel(@SerializedName("title") val title: String? = null,
                      @SerializedName("slug") val slug: String? = null,
                      @SerializedName("is_required") val is_required: Boolean? = null)