package com.panic.button.core.api.response

import com.google.gson.annotations.SerializedName

open class BaseErrorResponse(@SerializedName("success") val isSuccess: Boolean? = null,
                             @SerializedName("message") val message: String? = null){
    fun isSuccess():Boolean{
        return isSuccess == true
    }
}