package com.panic.button.core.model

import com.google.gson.annotations.SerializedName

data class SkckStepFiveModel(
    @SerializedName("police_certificate_id") var policeCertificateId: String? = null,

    @SerializedName("other_question") var other_question: Int = 5,

    @SerializedName("other_1_slug") var other_1_slug: String = "negara_dikunjungi",
    @SerializedName("other_1_answer") var other_1_answer: String? = null,

    @SerializedName("other_2_slug") var other_2_slug: String = "tahun_dikunjungi",
    @SerializedName("other_2_answer") var other_2_answer: String? = null,

    @SerializedName("other_3_slug") var other_3_slug: String = "keperluan_dikunjungi",
    @SerializedName("other_3_answer") var other_3_answer: String? = null,

    @SerializedName("other_4_slug") var other_4_slug: String = "hobi",
    @SerializedName("other_4_answer") var other_4_answer: String? = null,

    @SerializedName("other_5_slug") var other_5_slug: String = "no_telp_dihubungi",
    @SerializedName("other_5_answer") var other_5_answer: String? = null
)