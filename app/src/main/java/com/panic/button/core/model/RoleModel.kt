package com.panic.button.core.model

import com.google.gson.annotations.SerializedName

data class RoleModel(
    @SerializedName("id") val id: String? = null,
    @SerializedName("role_name") val roleName: String? = null,
    @SerializedName("role_slug") val roleSlug: String? = null
)