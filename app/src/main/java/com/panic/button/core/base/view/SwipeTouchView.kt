package com.panic.button.core.base.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.panic.button.core.model.OnSwipeTouchListener

class SwipeTouchView(context: Context, view: View, onSwipeTouchListener: OnSwipeTouchListener) : View.OnTouchListener {

    private var gestureDetector: GestureDetector? = null

    init {
        gestureDetector = GestureDetector(context, GestureListener(onSwipeTouchListener))
        view.setOnTouchListener(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector?.onTouchEvent(event) == true
    }
}