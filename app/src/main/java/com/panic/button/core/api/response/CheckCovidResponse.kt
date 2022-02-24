package com.panic.button.core.api.response

import com.google.gson.annotations.SerializedName
import com.panic.button.core.model.UserModel

data class CheckCovidResponse(@SerializedName("data") val data: UserModel? = null) : BaseResponse()