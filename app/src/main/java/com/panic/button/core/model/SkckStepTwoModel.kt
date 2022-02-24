package com.panic.button.core.model

import com.google.gson.annotations.SerializedName

data class SkckStepTwoModel(
    @SerializedName("police_certificate_id") var policeCertificateId: String? = null,

    @SerializedName("father_full_name") var father_full_name: String? = null,
    @SerializedName("father_year_of_birth") var father_year_of_birth: String? = null,
    @SerializedName("father_religion") var father_religion: String? = null,
    @SerializedName("father_nationality") var father_nationality: String? = null,
    @SerializedName("father_job") var father_job: String? = null,
    @SerializedName("father_address") var father_address: String? = null,


    @SerializedName("mother_full_name") var mother_full_name: String? = null,
    @SerializedName("mother_year_of_birth") var mother_year_of_birth: String? = null,
    @SerializedName("mother_religion") var mother_religion: String? = null,
    @SerializedName("mother_nationality") var mother_nationality: String? = null,
    @SerializedName("mother_job") var mother_job: String? = null,
    @SerializedName("mother_address") var mother_address: String? = null,


    @SerializedName("spouse_full_name") var spouse_full_name: String? = null,
    @SerializedName("spouse_year_of_birth") var spouse_year_of_birth: String? = null,
    @SerializedName("spouse_religion") var spouse_religion: String? = null,
    @SerializedName("spouse_nationality") var spouse_nationality: String? = null,
    @SerializedName("spouse_job") var spouse_job: String? = null,
    @SerializedName("spouse_address") var spouse_address: String? = null,


    @SerializedName("siblings") var siblings: Int = 1,


    @SerializedName("sibling_1_full_name") var sibling_1_full_name: String? = null,
    @SerializedName("sibling_1_year_of_birth") var sibling_1_year_of_birth: String? = null,
    @SerializedName("sibling_1_religion") var sibling_1_religion: String? = null,
    @SerializedName("sibling_1_nationality") var sibling_1_nationality: String? = null,
    @SerializedName("sibling_1_job") var sibling_1_job: String? = null,
    @SerializedName("sibling_1_address") var sibling_1_address: String? = null
)