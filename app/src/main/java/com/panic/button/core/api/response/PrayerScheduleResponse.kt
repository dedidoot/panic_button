package com.panic.button.core.api.response

import com.google.gson.annotations.SerializedName

data class PrayerScheduleResponse(@SerializedName("data") val data: ArrayList<PrayerScheduleData>? = null)

data class PrayerScheduleData(@SerializedName("date") val date: PrayerScheduleDate? = null,
                              @SerializedName("timings") val timings: PrayerTimings? = null)

data class PrayerScheduleDate(@SerializedName("readable") val readable: String? = null)

data class PrayerTimings(@SerializedName("Fajr") val fajr: String? = null,
                         @SerializedName("Sunrise") val sunrise: String? = null,
                         @SerializedName("Dhuhr") val dhuhr: String? = null,
                         @SerializedName("Asr") val asr: String? = null,
                         @SerializedName("Sunset") val sunset: String? = null,
                         @SerializedName("Maghrib") val maghrib: String? = null,
                         @SerializedName("Isha") val isha: String? = null,
                         @SerializedName("Imsak") val imsak: String? = null,
                         @SerializedName("Midnight") val midnight: String? = null)