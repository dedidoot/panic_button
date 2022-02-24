package com.panic.button.feature.ktpupload

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.panic.button.R
import com.panic.button.feature.splash.SplashActivity

class SuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)
    }

    fun buttonOkeSuccess(view: View) {
        startActivity(Intent(this, SplashActivity::class.java))
        finish()
    }

    companion object {
        fun onStartActivity(context: Context) {
            val intent = Intent(context, SuccessActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }
}