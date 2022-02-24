package com.panic.button.core.base

import android.content.Context
import android.content.SharedPreferences

open class SessionManager(context: Context) {

    private val pref: SharedPreferences = context.getSharedPreferences("app_session", 0)
    private val editor: SharedPreferences.Editor = pref.edit()

    private val isPrayerScheduleAlarmSession = "is_prayer_schedule_alarm"
    private val userIdSession = "user_id"
    private val accessTokenSession = "access_token"
    private val isPoliceSession = "is_police"
    private val registerCitizenSession = "register_citizen_session"
    private val skckStepOneSession = "skck_step_one_session"
    private val skckStepTwoSession = "skck_step_two_session"
    private val skckStepThreeSession = "skck_step_three_session"
    private val skckStepFourSession = "skck_step_four_session"
    private val skckStepFiveSession = "skck_step_five_session"
    private val latitudeSession = "latitude_session"
    private val roomsIdSession = "rooms_id_session"
    private val latitudeVictimSession = "latitude_victim_session"
    private val longitudeVictimSession = "longitude_victim_session"
    private val longitudeSession = "longitude_session"
    private val fcmTokenSession = "fcm_token_session"
    private val prayerScheduleNameSession = "prayer_schedule_name_session"
    private val policeCertificateSessionId = "police_certificate_id_session"
    private val emailSession = "email"
    private val fullNameSession = "full_name"
    private val loginParamsSession = "login_params_session"
    private val roleParamsSession = "role_params_session"

    var fullName: String?
        get() = pref.getString(fullNameSession, "")
        set(value) = editor.putString(fullNameSession, value).apply()

    var loginParams: String?
        get() = pref.getString(loginParamsSession, "")
        set(value) = editor.putString(loginParamsSession, value).apply()

    var email: String?
        get() = pref.getString(emailSession, "")
        set(value) = editor.putString(emailSession, value).apply()

    var userId: String?
        get() = pref.getString(userIdSession, "")
        set(value) = editor.putString(userIdSession, value).apply()

    var accessToken: String?
        get() = pref.getString(accessTokenSession, "")
        set(value) = editor.putString(accessTokenSession, value).apply()

    var registerCitizen: String?
        get() = pref.getString(registerCitizenSession, "")
        set(value) = editor.putString(registerCitizenSession, value).apply()

    var skckStepOne: String?
        get() = pref.getString(skckStepOneSession, "")
        set(value) = editor.putString(skckStepOneSession, value).apply()

    var skckStepTwo: String?
        get() = pref.getString(skckStepTwoSession, "")
        set(value) = editor.putString(skckStepTwoSession, value).apply()

    var skckStepThree: String?
        get() = pref.getString(skckStepThreeSession, "")
        set(value) = editor.putString(skckStepThreeSession, value).apply()

    var skckStepFour: String?
        get() = pref.getString(skckStepFourSession, "")
        set(value) = editor.putString(skckStepFourSession, value).apply()

    var skckStepFive: String?
        get() = pref.getString(skckStepFiveSession, "")
        set(value) = editor.putString(skckStepFiveSession, value).apply()

    var latitude: String?
        get() = pref.getString(latitudeSession, "321")
        set(value) = editor.putString(latitudeSession, value).apply()

    var longitude: String?
        get() = pref.getString(longitudeSession, "123")
        set(value) = editor.putString(longitudeSession, value).apply()

    var fcmToken: String?
        get() = pref.getString(fcmTokenSession, "")
        set(value) = editor.putString(fcmTokenSession, value).apply()

    var prayerScheduleName: String?
        get() = pref.getString(prayerScheduleNameSession, "")
        set(value) = editor.putString(prayerScheduleNameSession, value).apply()

    var policeCertificateId: String?
        get() = pref.getString(policeCertificateSessionId, "")
        set(value) = editor.putString(policeCertificateSessionId, value).apply()

    var roomsId: String?
        get() = pref.getString(roomsIdSession, "")
        set(value) = editor.putString(roomsIdSession, value).apply()

    var latitudeVictim: String?
        get() = pref.getString(latitudeVictimSession, "")
        set(value) = editor.putString(latitudeVictimSession, value).apply()

    var longitudeVictim: String?
        get() = pref.getString(longitudeVictimSession, "")
        set(value) = editor.putString(longitudeVictimSession, value).apply()

    var isPrayerScheduleAlarm: Boolean
        get() = pref.getBoolean(isPrayerScheduleAlarmSession, false)
        set(value) = editor.putBoolean(isPrayerScheduleAlarmSession, value).apply()

    var isPolice: Boolean
        get() = pref.getBoolean(isPoliceSession, false)
        set(value) = editor.putBoolean(isPoliceSession, value).apply()

    var role: String?
        get() = pref.getString(roleParamsSession, "")
        set(value) = editor.putString(roleParamsSession, value).apply()

    fun isLastLocationNotEmpty() : Boolean {
        return !latitude.isNullOrEmpty() && !longitude.isNullOrEmpty()
    }

    fun clearSession() {
        editor.clear()
        editor.commit()
    }

    fun resetCallVictim() {
        roomsId = ""
        latitudeVictim = ""
        longitudeVictim = ""
    }
}