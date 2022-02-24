package com.panic.button.core.model

import com.google.gson.annotations.SerializedName

data class SkckStepThreeModel(
    @SerializedName("police_certificate_id") var policeCertificateId: String? = null,

    @SerializedName("educations") var educations: Int = 3,


    @SerializedName("education_1_grade") var education_1_grade: String? = null,
    @SerializedName("education_1_institution_name") var education_1_institution_name: String? = null,
    @SerializedName("education_1_year_of_grade") var education_1_year_of_grade: String? = null,


    @SerializedName("education_2_grade") var education_2_grade: String? = null,
    @SerializedName("education_2_institution_name") var education_2_institution_name: String? = null,
    @SerializedName("education_2_year_of_grade") var education_2_year_of_grade: String? = null,


    @SerializedName("education_3_grade") var education_3_grade: String? = null,
    @SerializedName("education_3_institution_name") var education_3_institution_name: String? = null,
    @SerializedName("education_3_year_of_grade") var education_3_year_of_grade: String? = null
)