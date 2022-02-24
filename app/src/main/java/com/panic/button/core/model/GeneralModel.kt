package com.panic.button.core.model

import com.google.gson.annotations.SerializedName

class GeneralModel(@SerializedName("id")
                   var id: String? = null,
                   @SerializedName("name")
                   var name: String? = null,
                   @SerializedName("totalCovidPositive")
                   var totalCovidPositive: Int? = null,
                   @SerializedName("totalCovidRecovered")
                   var totalCovidRecovered: Int? = null,
                   @SerializedName("totalCovidDeath")
                   var totalCovidDeath: Int? = null)