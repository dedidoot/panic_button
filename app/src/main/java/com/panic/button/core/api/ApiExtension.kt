package com.panic.button.core.api

import android.util.Log
import android.view.View
import androidx.databinding.BindingAdapter
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.bind.DateTypeAdapter
import com.panic.button.BuildConfig
import org.json.JSONObject
import java.util.*

fun log(message: String) {
    if (BuildConfig.DEBUG) {
        val desc =
            "[" + Exception().stackTrace[1].className + "] " +
                    "[" + Exception().stackTrace[1].lineNumber + "] " +
                    "{" + Exception().stackTrace[1].methodName + "} "

        if (message.length > 500) {
            try {
                val maxLogSize = 500
                for (i in 0..message.length / maxLogSize) {
                    val start = i * maxLogSize
                    var end = (i + 1) * maxLogSize
                    end = if (end > message.length) message.length else end
                    Log.e(":", desc + " => " + message.substring(start, end))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("log Exception", desc + " => " + e.message)
            }
        } else {
            Log.e(":", "$desc => $message")
        }
    }
}

class GSONManager {
    companion object {
        private val gson: Gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Date::class.java, DateTypeAdapter()).create()

        fun <T> fromJson(json: String, kelas: Class<T>): T {
            return gson.fromJson(json, kelas)
        }

        fun <T> fromJson(json: JSONObject, kelas: Class<T>): T {
            return fromJson(json.toString(), kelas)
        }

        fun <T> toJson(entity: T): String {
            return gson.toJson(entity)
        }
    }
}

@set:BindingAdapter("isVisible")
inline var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }