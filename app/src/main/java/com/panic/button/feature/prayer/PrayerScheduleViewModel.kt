package com.panic.button.feature.prayer

import androidx.lifecycle.MutableLiveData
import com.panic.button.core.api.GSONManager
import com.panic.button.core.api.PrayerRequest
import com.panic.button.core.api.log
import com.panic.button.core.api.response.PrayerScheduleData
import com.panic.button.core.api.response.PrayerScheduleResponse
import com.panic.button.core.base.BaseViewModel
import com.panic.button.core.base.getTodayDateString
import java.text.SimpleDateFormat
import java.util.*

class PrayerScheduleViewModel : BaseViewModel() {

    val isSuccess = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    val date = MutableLiveData<String>()
    val prayerData = MutableLiveData<PrayerScheduleData>()
    val lastScheduleName = MutableLiveData<String>()

    var remainingHours = "0"
    var remainingMinutes = "0"

    private val formatDateNow = "dd MMM yyyy"
    private val dateNow = getTodayDateString(format = formatDateNow, locale = Locale.ENGLISH)
    private val calendar = Calendar.getInstance()
    private val hoursMinutesFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    private val hoursMinutesNow =
        hoursMinutesFormat.format(calendar.time ?: Date(System.currentTimeMillis()))

    init {
        date.value = getTodayDateString()
    }

    fun getSchedule() {
        isLoading.value = true
        PrayerRequest().get({ responsePrayerSchedule ->
            isLoading.value = false

            val response =
                GSONManager.fromJson(responsePrayerSchedule, PrayerScheduleResponse::class.java)

            response.data?.forEach {
                if (it.date?.readable?.equals(dateNow, ignoreCase = true) == true) {
                    prayerData.value = it
                    lastScheduleName.value = getLastTime()
                    log("remainingHours $remainingHours")
                    log("remainingMinutes $remainingMinutes")
                }
            }
        })
    }

    private fun getLastTime(): String {
        val hoursNow = getHours(hoursMinutesNow)
        val minutesNow = getMinutes(hoursMinutesNow)

        val fajrHours = getHours(prayerData.value?.timings?.fajr)
        val fajrMinutes = getMinutes(prayerData.value?.timings?.fajr)

        val sunriseHours = getHours(prayerData.value?.timings?.sunrise)
        val sunriseMinutes = getMinutes(prayerData.value?.timings?.sunrise)

        val dhuhrHours = getHours(prayerData.value?.timings?.dhuhr)
        val dhuhrMinutes = getMinutes(prayerData.value?.timings?.dhuhr)

        val asrHours = getHours(prayerData.value?.timings?.asr)
        val asrMinutes = getMinutes(prayerData.value?.timings?.asr)

        val magribHours = getHours(prayerData.value?.timings?.maghrib)
        val magribMinutes = getMinutes(prayerData.value?.timings?.maghrib)

        val ishaHours = getHours(prayerData.value?.timings?.isha)
        val ishaMinutes = getMinutes(prayerData.value?.timings?.isha)

        val imsakHours = getHours(prayerData.value?.timings?.imsak)
        val imsakMinutes = getMinutes(prayerData.value?.timings?.imsak)

        if (hoursNow < imsakHours) {
            if (imsakHours > hoursNow) {
                remainingHours = "${imsakHours - hoursNow}"
            } else {
                remainingHours = "${hoursNow - imsakHours}"
            }

            if (imsakMinutes > minutesNow) {
                remainingMinutes = "${imsakMinutes - minutesNow}"
            } else if (imsakMinutes == 0) {
                val minutes = 60 - (minutesNow - imsakMinutes)
                remainingMinutes = "$minutes"
            } else {
                remainingMinutes = "${minutesNow - imsakMinutes}"
            }

            if (remainingHours.toIntOrNull() == 1) {
                remainingHours = "0"
            }

            return "Imsyak"
        } else if (hoursNow < fajrHours) {
            if (fajrHours > hoursNow) {
                remainingHours = "${fajrHours - hoursNow}"
            } else {
                remainingHours = "${hoursNow - fajrHours}"
            }

            if (fajrMinutes > minutesNow) {
                remainingMinutes = "${fajrMinutes - minutesNow}"
            } else if (fajrMinutes == 0) {
                val minutes = 60 - (minutesNow - fajrMinutes)
                remainingMinutes = "$minutes"
            } else {
                remainingMinutes = "${minutesNow - fajrMinutes}"
            }

            if (remainingHours.toIntOrNull() == 1) {
                remainingHours = "0"
            }

            return "Subuh"
        } else if (hoursNow < sunriseHours) {
            if (sunriseHours > hoursNow) {
                remainingHours = "${sunriseHours - hoursNow}"
            } else {
                remainingHours = "${hoursNow - sunriseHours}"
            }

            if (sunriseMinutes > minutesNow) {
                remainingMinutes = "${sunriseMinutes - minutesNow}"
            } else if (sunriseMinutes == 0) {
                val minutes = 60 - (minutesNow - sunriseMinutes)
                remainingMinutes = "$minutes"
            } else {
                remainingMinutes = "${minutesNow - sunriseMinutes}"
            }

            if (remainingHours.toIntOrNull() == 1) {
                remainingHours = "0"
            }

            return "Matahari Terbit / Dhuha"
        } else if (hoursNow < dhuhrHours) {
            if (dhuhrHours > hoursNow) {
                remainingHours = "${dhuhrHours - hoursNow}"
            } else {
                remainingHours = "${hoursNow - dhuhrHours}"
            }

            if (dhuhrMinutes > minutesNow) {
                remainingMinutes = "${dhuhrMinutes - minutesNow}"
            } else if (dhuhrMinutes == 0) {
                val minutes = 60 - (minutesNow - dhuhrMinutes)
                remainingMinutes = "$minutes"
            } else {
                remainingMinutes = "${minutesNow - dhuhrMinutes}"
            }

            if (remainingHours.toIntOrNull() == 1) {
                remainingHours = "0"
            }

            return "Dzuhur"
        } else if (hoursNow < asrHours) {
            if (asrHours > hoursNow) {
                remainingHours = "${asrHours - hoursNow}"
            } else {
                remainingHours = "${hoursNow - asrHours}"
            }

            if (asrMinutes > minutesNow) {
                remainingMinutes = "${asrMinutes - minutesNow}"
            } else if (asrMinutes == 0) {
                val minutes = 60 - (minutesNow - asrMinutes)
                remainingMinutes = "$minutes"
            } else {
                remainingMinutes = "${minutesNow - asrMinutes}"
            }

            if (remainingHours.toIntOrNull() == 1) {
                remainingHours = "0"
            }

            return "Ashr"
        } else if (hoursNow < magribHours) {
            if (magribHours > hoursNow) {
                remainingHours = "${magribHours - hoursNow}"
            } else {
                remainingHours = "${hoursNow - magribHours}"
            }

            if (magribMinutes > minutesNow) {
                remainingMinutes = "${magribMinutes - minutesNow}"
            } else if (magribMinutes == 0) {
                val minutes = 60 - (minutesNow - magribMinutes)
                remainingMinutes = "$minutes"
            } else {
                remainingMinutes = "${minutesNow - magribMinutes}"
            }

            if (remainingHours.toIntOrNull() == 1) {
                remainingHours = "0"
            }

            return "Maghrib"
        } else if (hoursNow < ishaHours) {
            if (ishaHours > hoursNow) {
                remainingHours = "${ishaHours - hoursNow}"
            } else {
                remainingHours = "${hoursNow - ishaHours}"
            }

            if (ishaMinutes > minutesNow) {
                remainingMinutes = "${ishaMinutes - minutesNow}"
            } else if (ishaMinutes == 0) {
                val minutes = 60 - (minutesNow - ishaMinutes)
                remainingMinutes = "$minutes"
            } else {
                remainingMinutes = "${minutesNow - ishaMinutes}"
            }

            if (remainingHours.toIntOrNull() == 1) {
                remainingHours = "0"
            }

            return "Isya"
        } else {
            return "Imsyak"
        }
    }

    private fun getHours(timings: String?): Int {
        return getTiming(timings).split(":").getOrNull(0)?.toIntOrNull() ?: 0
    }

    private fun getMinutes(timings: String?): Int {
        return getTiming(timings).split(":").getOrNull(1)?.toIntOrNull() ?: 0
    }

    fun getTiming(timing: String?): String {
        return timing?.replace("(WIB)", "")?.trim() ?: ""
    }
}