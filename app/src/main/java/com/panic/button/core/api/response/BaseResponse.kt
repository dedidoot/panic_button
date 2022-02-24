package com.panic.button.core.api.response

import com.google.gson.annotations.SerializedName

open class BaseResponse(
    @SerializedName("success") val isSuccess: Boolean? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("errors") val errors: Error? = null,
    @SerializedName("status") val status: Boolean? = null
){
    fun isSuccess():Boolean{
        return isSuccess == true
    }
}

open class Error(
    @SerializedName("email") val email: ArrayList<String>? = null,
    @SerializedName("password") val password: ArrayList<String>? = null
)