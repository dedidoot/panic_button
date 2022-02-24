package com.panic.button.core.model

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("auth") val authModel: AuthModel? = null,
    @SerializedName("user") val userModel: UserModel? = null
)