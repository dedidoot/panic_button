package com.panic.button.core.model

import com.google.gson.annotations.SerializedName

data class PatrolModel(
    @SerializedName("report") var report: String? = null,
    @SerializedName("count_video") var countVideo: Int? = null,
    @SerializedName("video_0") var video0Id: String? = null,
    @SerializedName("video_1") var video1Id: String? = null,
    @SerializedName("video_2") var video2Id: String? = null,
    @SerializedName("video_3") var video3Id: String? = null)