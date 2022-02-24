package com.panic.button.feature.shortcutbutton

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.panic.button.core.api.log
import org.greenrobot.eventbus.EventBus

class LockService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            try {
                EventBus.getDefault().post(CommandEventListener())
            } catch (e : Exception) {
            }
        } catch (e : Exception) {
            log("error onStartCommand $e")
        }
        return super.onStartCommand(intent, flags, startId)
    }
}