package com.panic.button.feature.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.panic.button.R
import com.panic.button.core.base.*
import com.panic.button.core.base.notification.NotificationServiceHelper
import com.panic.button.feature.home.HomeActivity
import com.panic.button.feature.login.LoginActivity
import com.panic.button.feature.police.CovidOfficerActivity
import com.panic.button.feature.police.PoliceActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class SplashActivity : AppCompatActivity() , CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initiateKillService()
        openHomeActivity()
    }

    private fun initiateKillService() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            return
        }
        val secondInterval = 60
        NotificationServiceHelper(this).setCurrentTimer(secondInterval).run()
    }

    private fun openHomeActivity() {
        makeHandler(1500) {
            val intent = Intent()

            if (isCovidOfficer()) {
                intent.setClass(this, CovidOfficerActivity::class.java)
            } else if (BaseApplication.sessionManager?.isPolice == true) {
                intent.setClass(this, PoliceActivity::class.java)
            } else if (BaseApplication.sessionManager?.accessToken.isNullOrEmpty()) {
                intent.setClass(this, LoginActivity::class.java)
            } else {
                intent.setClass(this, HomeActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }
}