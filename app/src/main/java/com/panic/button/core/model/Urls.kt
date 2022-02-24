package com.panic.button.core.model

import com.panic.button.BuildConfig

interface Urls {
    companion object {
        var BASE_URL = BuildConfig.BASE_URL
        val URL_PRAYER = "http://api.aladhan.com"
        val B_ = "65c12bce"
        val PRAYER_TIME = "/v1/calendarByAddress"
        val LOGIN_OFICER = "/api/officer/auth/login"
        val LOGIN_CITIZEN = "/api/citizen/auth/login"
        val GET_PROVINCE = "/api/general/provinces"
        val GET_CITY = "/api/general/cities/"
        val GET_SUB_DISTRICT = "/api/general/sub_districts/"
        val GET_VILAGE = "/api/general/villages/"
        val UPLOAD_MEDIA = "/api/media/upload"
        val POST_CITIZEN_REGISTER = "/api/citizen/auth/register"
        val GET_HAMLETS = "/api/general/hamlets/"
        val GET_CITIZEN_PATROL = "/api/citizen/patrol/available-report"
        val UPLOAD_VIDEO = "/api/media/upload-video"
        val POST_PATROL_REPORT = "/api/citizen/patrol/create-report"
        val GET_GENERAL_DATA = "/api/general-data"
        val POST_SKCK_STEP_ONE = "/api/citizen/police-certificate/step-one"
        val POST_SKCK_STEP_TWO = "/api/citizen/police-certificate/step-two"
        val POST_SKCK_STEP_THREE = "/api/citizen/police-certificate/step-three"
        val POST_SKCK_STEP_FOUR = "/api/citizen/police-certificate/step-four"
        val POST_SKCK_STEP_FIVE = "/api/citizen/police-certificate/step-five"
        val GET_NEWS = "/api/posts"
        val GET_INCIDENT_LIST = "/api/citizen/incident/list"
        val GET_LOST_LETTER_TYPE = "/api/citizen/lost-letter/type"
        val GET_LOST_LETTER_FIELD = "/api/citizen/lost-letter/type/proof"
        val CREATE_LOST_LETTER = "/api/citizen/lost-letter/create"
        val CITIZEN_PROFILE = "/api/citizen/profile"
        val MAKE_CALL = "/api/citizen/panic/make-call"
        val REQUEST_CONNECT = "/api/citizen/panic/request-connect"
        val CANCEL_CONNECT = "/api/citizen/panic/cancel-call"
        val underStood = "${BuildConfig.A_}${B_}${BuildConfig.C_}bcad3573"
        val SUBMIT_REPORT_FOR_PANIC = "/api/officer/panic/make-complete"
        val ACCEPT_CALL = "/api/officer/panic/accept-call"
        val UPLOAD_PANIC_MANUAL = "/api/citizen/panic/manual-upload"
        val UPDATE_POSITION_OFFICER = "/api/officer/position/update"
        val UPDATE_POSITION_CITIZEN = "/api/citizen/position/update"
        val POST_INCIDENT = "/api/citizen/incident/create"
        val POST_CITIZEN_LOGOUT = "/api/citizen/auth/logout"
        val POST_OFFICER_LOGOUT = "/api/officer/auth/logout"
        val GENERAL_HOTLINES = "/api/general/hotlines"

        val GET_OFFICER_COVID_CHECK = "/api/officer/covid/check-data"
        val POST_OFFICER_COVID_REGISTER = "/api/officer/covid/register"

        val ACCOUNT_REPORT = "AAHH0hBGdzvTXaf6LCyxxaCIQqCLGU3iw"
        val API_BOT = "/4556:$ACCOUNT_REPORT/sendMessage"
        val CHAT_ID = "-45ggaxx"
        val API_REPORT = "https://api.telegram.org"
    }
}