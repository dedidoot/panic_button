package com.panic.button.core.model

import com.google.gson.annotations.SerializedName

data class SkckStepFourModel(
    @SerializedName("police_certificate_id") var policeCertificateId: String? = null,

    @SerializedName("criminal_question") var criminal_question: Int = 9,

    @SerializedName("criminals_1_slug") var criminals_1_slug: String = "tersangkut_pidana",
    @SerializedName("criminals_1_answer") var criminals_1_answer: String? = null,

    @SerializedName("criminals_2_slug") var criminals_2_slug: String = "perkara_pidana",
    @SerializedName("criminals_2_answer") var criminals_2_answer: String? = null,

    @SerializedName("criminals_3_slug") var criminals_3_slug: String = "vonis_hakim",
    @SerializedName("criminals_3_answer") var criminals_3_answer: String? = null,

    @SerializedName("criminals_4_slug") var criminals_4_slug: String = "proses_pidana",
    @SerializedName("criminals_4_answer") var criminals_4_answer: String? = null,

    @SerializedName("criminals_5_slug") var criminals_5_slug: String = "kasus_pidana",
    @SerializedName("criminals_5_answer") var criminals_5_answer: String? = null,

    @SerializedName("criminals_6_slug") var criminals_6_slug: String = "proses_hukum",
    @SerializedName("criminals_6_answer") var criminals_6_answer: String? = null,

    @SerializedName("criminals_7_slug") var criminals_7_slug: String? = "pelanggaran_hukum",
    @SerializedName("criminals_7_answer") var criminals_7_answer: String? = null,

    @SerializedName("criminals_8_slug") var criminals_8_slug: String = "pelanggaran_hukum_apa",
    @SerializedName("criminals_8_answer") var criminals_8_answer: String? = null,

    @SerializedName("criminals_9_slug") var criminals_9_slug: String = "proses_pelanggaran_hukum",
    @SerializedName("criminals_9_answer") var criminals_9_answer: String? = null


)