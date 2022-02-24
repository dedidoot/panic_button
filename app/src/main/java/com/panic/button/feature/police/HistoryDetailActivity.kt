package com.panic.button.feature.police

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.panic.button.R

class HistoryDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_history)
    }

    fun onBack(view: View) {
        finish()
    }
}