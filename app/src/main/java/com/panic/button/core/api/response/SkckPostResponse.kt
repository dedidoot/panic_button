package com.panic.button.core.api.response

import com.google.gson.annotations.SerializedName

class SkckPostResponse(@SerializedName("police_certificate") val policeCertificate: PoliceCertificate? = null) : BaseErrorResponse()

class PoliceCertificate(@SerializedName("id") val id: String? = null) : BaseErrorResponse()