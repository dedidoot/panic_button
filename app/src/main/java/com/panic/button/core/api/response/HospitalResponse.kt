package com.panic.button.core.api.response

import com.google.gson.annotations.SerializedName
import com.panic.button.core.model.HospitalModel
import com.panic.button.core.model.MediaModel

data class HospitalResponse(@SerializedName("data") val data: ArrayList<HospitalModel>? = null) : BaseResponse()