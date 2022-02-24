package com.panic.button.core.base.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.panic.button.R
import kotlinx.android.synthetic.main.view_hospital.view.*

class HospitalView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.view_hospital, this)
    }

    fun setTitle(title : String?) {
        titleTextView.text = title ?: ""
    }

    fun setDesc(desc : String?) {
        descTextView.text = desc ?: ""
    }

    fun setClickCall(event: () -> Unit) {
        button.setOnClickListener {
            event()
        }
    }

}