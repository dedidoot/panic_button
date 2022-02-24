package com.panic.button.core.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.panic.button.core.model.MediaModel
import kotlinx.android.parcel.Parcelize

data class HistoryResponse(@SerializedName("result") val result: ArrayList<HistoryModel>? = null) : BaseResponse()

@Parcelize
data class HistoryModel(@SerializedName("id") val id: String? = null,
                        @SerializedName("title") val title: String? = null,
                        @SerializedName("report") val report: String? = null,
                        @SerializedName("created_at") val created_at: String? = null,
                        @SerializedName("images") val images: ArrayList<MediaModel>? = null,
                        @SerializedName("follow_ups") var follow_ups: ArrayList<FollowUpModel>? = null) : Parcelable


@Parcelize
data class FollowUpModel(@SerializedName("id") val id: String? = null,
                        @SerializedName("content") val content: String? = null,
                        @SerializedName("images") val images: ArrayList<MediaModel>? = null) : Parcelable


