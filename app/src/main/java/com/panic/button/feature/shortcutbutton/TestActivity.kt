package com.panic.button.feature.shortcutbutton

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.panic.button.core.api.log
import com.panic.button.core.base.RecorderVoiceHelper
import com.panic.button.core.base.makeHandler
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class TestActivity : AppCompatActivity() {

    private val mReceiver = ScreenReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startSchedule()
    }

    private fun startSchedule() {
        stopSchedule()
        startService(Intent(this, LockService::class.java))
    }

    private fun stopSchedule() {
        stopService(Intent(this, LockService::class.java))
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        log("onKeyDown")
        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        super.onResume()
    }

    @Subscribe
    fun screenEventListener(model: ScreenEventListener?) {
        log("Subscribe event bus ScreenEventListener $shortcutButtonCount")

        val recorder = RecorderVoiceHelper(this)
        recorder.startRecord()
        makeHandler(10000) {
            recorder.stopRecord()
            /*UploadRequest(Urls.UPLOAD_PANIC_MANUAL)
                .uploadFilePanicButton(recorder.file, "9201dd6f-002d-4616-a2bb-e40c4f6b90db", {
                    log("response upload $it")
                    shortcutButtonCount = 0
                })*/
        }
    }

    var shortcutButtonCount = 0

    @Subscribe
    fun commandEventListener(model: CommandEventListener?) {
        log("Subscribe event bus CommandEventListener")
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        //filter.addAction(Intent.ACTION_SCREEN_OFF);
        //filter.addAction(Intent.ACTION_USER_PRESENT);
        log("isScreenReceiverRunning ${mReceiver}")
        log("isScreenReceiverRunning ${mReceiver.isInitialStickyBroadcast}")
        log("isScreenReceiverRunning ${mReceiver.isOrderedBroadcast}")
        log("isScreenReceiverRunning ${mReceiver.abortBroadcast}")

        try {
            try {
                registerReceiver(mReceiver, filter)
            } catch (e : Exception) {
            }
        } catch (e : Exception) {
            log("error $e")
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}