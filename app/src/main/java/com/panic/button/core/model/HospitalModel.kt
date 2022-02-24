package com.panic.button.core.model

import com.google.gson.annotations.SerializedName

data class HospitalModel(@SerializedName("name") val name: String? = null,
                           @SerializedName("phone") val phone: String? = null)