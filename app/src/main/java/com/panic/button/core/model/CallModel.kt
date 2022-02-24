package com.panic.button.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.panic.button.core.api.response.BaseResponse
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CallResponse(
    @SerializedName("data") val data: CallModel? = null
): BaseResponse(), Parcelable

@Parcelize
data class CallModel(
    @SerializedName("id") val id: String? = null,
    @SerializedName("call_id") val callId: String? = null, // for post make call
    @SerializedName("user_id") val user_id: String? = null,
    @SerializedName("latitude") val latitude: String? = null,
    @SerializedName("longitude") val longitude: String? = null,
    @SerializedName("status") val status: String? = null
): Parcelable