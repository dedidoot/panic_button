package com.panic.button.feature.police

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.panic.button.R
import com.panic.button.core.base.BaseApplication
import com.panic.button.core.base.isVisible
import com.panic.button.core.model.SubmitReportModel
import com.panic.button.feature.splash.SplashActivity
import kotlinx.android.synthetic.main.activity_report_success.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class ReportSuccessActivity : AppCompatActivity() , CoroutineScope{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_success)
    }

    fun onFinish(view : View) {
        finishButton.isVisible = false
        loading.isVisible = true

        launch {
            BaseApplication.sessionManager?.resetCallVictim()
            EventBus.getDefault().post(SubmitReportModel(callId = SubmitReportModel.SUCCESS))
            startActivity(Intent(this@ReportSuccessActivity, SplashActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
    }

    override val coroutineContext get() = Dispatchers.Main
}