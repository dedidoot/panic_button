package com.panic.button.core.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("fcm_token") var fcmToken: String? = null,
    @SerializedName("imei") val imeiDevice: String? = null,
    @SerializedName("citizen_card_id") val citizen_card_id: String? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("hamlet_id") val hamlet_id: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("roles") val roles: ArrayList<RoleModel>? = null
)