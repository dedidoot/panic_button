package com.panic.button.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.panic.button.core.api.UploadRequest
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MediaModel(
    @SerializedName("url") val url: String? = null,
    @SerializedName("section") val section: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
): Parcelable {

    fun isVideo() : Boolean {
        return section?.equals(UploadRequest.INCIDENT_VIDEO, ignoreCase = true) == true
    }

    fun isPdf() : Boolean {
        return url?.contains(".pdf", ignoreCase = true) == true
    }
}