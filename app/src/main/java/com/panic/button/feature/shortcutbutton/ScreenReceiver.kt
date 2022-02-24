package com.panic.button.feature.shortcutbutton

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.panic.button.core.api.log
import org.greenrobot.eventbus.EventBus

class ScreenReceiver : BroadcastReceiver() {

    private var shortcutButtonCount = 1

    override fun onReceive(context: Context, intent: Intent) {
        log("onReceive $shortcutButtonCount")

        /*if (shortcutButtonCount < 5) {
            shortcutButtonCount += 1
            return
        }*/
        EventBus.getDefault().post(ScreenEventListener())
        //shortcutButtonCount = 1

        if (intent.action == Intent.ACTION_SCREEN_OFF) {
            // do whatever you need to do here
            wasScreenOn = false
            log("kesini ACTION_SCREEN_OFF")
        } else if (intent.action == Intent.ACTION_SCREEN_ON) {
            // and do whatever you need to do here
            wasScreenOn = true
            log("kesini ACTION_SCREEN_ON")
        } else if (intent.action == Intent.ACTION_USER_PRESENT) {
            log("kesini ACTION_USER_PRESENT")
            val url = "http://www.stackoverflow.com"
            val i = Intent(Intent.ACTION_VIEW)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.data = Uri.parse(url)
            context.startActivity(i)
        }
    }

    companion object {
        var wasScreenOn = true
    }
}