package com.panic.button.core.api.response

import com.google.gson.annotations.SerializedName
import com.panic.button.core.model.MediaModel

data class MediaResponse(@SerializedName("data") val mediaModel: MediaModel? = null) : BaseResponse()