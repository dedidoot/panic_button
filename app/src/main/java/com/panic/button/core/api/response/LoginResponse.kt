package com.panic.button.core.api.response

import com.google.gson.annotations.SerializedName
import com.panic.button.core.model.LoginModel

data class LoginResponse(@SerializedName("data") val loginModel: LoginModel? = null) : BaseResponse()