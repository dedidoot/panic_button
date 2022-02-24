package com.panic.button.feature.location_interval

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.panic.button.core.api.PostRequest
import com.panic.button.core.api.log
import com.panic.button.core.base.BaseApplication
import com.panic.button.core.base.location.RequestLocation
import com.panic.button.core.base.makeHandler
import com.panic.button.core.model.CallModel
import com.panic.button.core.model.Urls

class LocationIntervalReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, dataIntent: Intent?) {
        log("LocationIntervalReceiver")
        if (BaseApplication.sessionManager?.accessToken.isNullOrBlank()) {
            return
        }
        try {
            RequestLocation(context as Activity)
        } catch (e : Exception) { }

        makeHandler(10000) {
            val model = CallModel(
                latitude = BaseApplication.sessionManager?.latitude,
                longitude = BaseApplication.sessionManager?.longitude
            )
            PostRequest<CallModel>(Urls.UPDATE_POSITION_CITIZEN).post(
                params = model,
                response = {
                })
        }
    }

}