package com.panic.button.core.model

import com.google.gson.annotations.SerializedName

data class SkckStepOneModel(
    @SerializedName("citizen_card_id") var citizenCardId: String? = null,
    @SerializedName("date_of_birth") var dateOfBirth: String? = null,

    @SerializedName("gender") var gender: String? = null,

    @SerializedName("purpose") var purpose: String? = null,
    @SerializedName("nationality") var nationality: String? = null,
    @SerializedName("full_name") var fullName: String? = null,
    @SerializedName("place_of_birth") var placeOfBirth: String? = null,
    @SerializedName("marriage_status") var marriageStatus: String? = null,
    @SerializedName("religion") var religion: String? = null,
    @SerializedName("job") var job: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("citizen_card_address") var citizenCardAddress: String? = null,
    @SerializedName("current_address") var currentAddress: String? = null,
    @SerializedName("province") var province: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("sub_district_id") var subDistrictId: String? = null,
    @SerializedName("village_id") var villageId: String? = null,
    @SerializedName("height") var height: String? = null,
    @SerializedName("weight") var weight: String? = null,
    @SerializedName("face_shape") var faceShape: String? = null,
    @SerializedName("color_skin") var colorSkin: String? = null,
    @SerializedName("hair_skin") var hairSkin: String? = null,
    @SerializedName("blood_type") var bloodType: String? = null,
    @SerializedName("body_shape") var bodyShape: String? = null,
    @SerializedName("head_shape") var headShape: String? = null,
    @SerializedName("hair_type") var hairType: String? = null,
    @SerializedName("forehead_type") var foreheadType: String? = null,
    @SerializedName("eye_color") var eyeColor: String? = null,
    @SerializedName("eye_disorder") var eyeDisorder: String? = null,
    @SerializedName("nose_shape") var noseShape: String? = null,
    @SerializedName("lip_shape") var lipShape: String? = null,
    @SerializedName("teeth_shape") var teethShape: String? = null,
    @SerializedName("chin_shape") var chinShape: String? = null,
    @SerializedName("ear_shape") var earShape: String? = null,
    @SerializedName("police_certificate") var policeCertificateId: String? = null
)