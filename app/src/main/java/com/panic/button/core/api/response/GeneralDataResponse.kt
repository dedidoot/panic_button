package com.panic.button.core.api.response

import com.google.gson.annotations.SerializedName
import com.panic.button.core.model.GeneralModel
import com.panic.button.core.model.LabelValueModel

data class GeneralDataResponse(@SerializedName("data") val data: GeneraDataModel? = null) : BaseResponse()

data class GeneraDataModel(@SerializedName("gender") val gender: ArrayList<LabelValueModel>? = null,
                           @SerializedName("bloodType") val bloodType: ArrayList<LabelValueModel>? = null,
                           @SerializedName("marriageStatus") val marriageStatus: ArrayList<LabelValueModel>? = null,
                           @SerializedName("religion") val religion: ArrayList<LabelValueModel>? = null,
                           @SerializedName("bodyShape") val bodyShape: ArrayList<LabelValueModel>? = null,
                           @SerializedName("chinShape") val chinShape: ArrayList<LabelValueModel>? = null,
                           @SerializedName("colorSkin") val colorSkin: ArrayList<LabelValueModel>? = null,
                           @SerializedName("earShape") val earShape: ArrayList<LabelValueModel>? = null,
                           @SerializedName("eyeColor") val eyeColor: ArrayList<LabelValueModel>? = null,
                           @SerializedName("eyeDisorder") val eyeDisorder: ArrayList<LabelValueModel>? = null,
                           @SerializedName("faceShape") val faceShape: ArrayList<LabelValueModel>? = null,
                           @SerializedName("foreheadType") val foreheadType: ArrayList<LabelValueModel>? = null,
                           @SerializedName("hairSkin") val hairSkin: ArrayList<LabelValueModel>? = null,
                           @SerializedName("hairType") val hairType: ArrayList<LabelValueModel>? = null,
                           @SerializedName("headShape") val headShape: ArrayList<LabelValueModel>? = null,
                           @SerializedName("lipShape") val lipShape: ArrayList<LabelValueModel>? = null,
                           @SerializedName("noseShape") val noseShape: ArrayList<LabelValueModel>? = null,
                           @SerializedName("teethShape") val teethShape: ArrayList<LabelValueModel>? = null,
                           @SerializedName("relationShip") val relationShip: ArrayList<LabelValueModel>? = null,
                           @SerializedName("userStatus") val userStatus: ArrayList<LabelValueModel>? = null,
                           @SerializedName("mediaSection") val mediaSection: ArrayList<LabelValueModel>? = null,
                           @SerializedName("videoSection") val videoSection: ArrayList<LabelValueModel>? = null) : BaseResponse()