package com.panic.button.core.api.response

import com.google.gson.annotations.SerializedName
import com.panic.button.core.model.GeneralModel

data class ProvinceResponse(@SerializedName("provinces") val provinces: ArrayList<GeneralModel>? = null) : BaseResponse()

data class CityResponse(@SerializedName("cities") val cities: ArrayList<GeneralModel>? = null) : BaseResponse()

data class SubDistrictResponse(@SerializedName("sub_districts") val subDistricts: ArrayList<GeneralModel>? = null) : BaseResponse()

data class VillageResponse(@SerializedName("villages") val villages: ArrayList<GeneralModel>? = null) : BaseResponse()