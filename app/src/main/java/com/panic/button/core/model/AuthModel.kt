package com.panic.button.core.model

import com.google.gson.annotations.SerializedName

data class AuthModel(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
    @SerializedName("expires_in") val expiresIn: Long? = null
)