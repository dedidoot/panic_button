package com.panic.button.feature.police

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.panic.button.R
import kotlinx.android.synthetic.main.activity_history.*

class HistoryPoliceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        historyView1.setOnClickListener {
            startActivity(Intent(this, HistoryDetailActivity::class.java))
        }
        historyView2.setOnClickListener {
            startActivity(Intent(this, HistoryDetailActivity::class.java))
        }
        historyView3.setOnClickListener {
            startActivity(Intent(this, HistoryDetailActivity::class.java))
        }
    }

    fun onBack(view: View) {
        finish()
    }
}