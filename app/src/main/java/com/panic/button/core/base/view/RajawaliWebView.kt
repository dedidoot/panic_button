package com.panic.button.core.base.view

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView

class RajawaliWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    fun getMaxWidthScroll() : Int {
        return computeHorizontalScrollRange() - width
    }

    fun getMaxHeightScroll() : Int {
        return computeVerticalScrollRange() - height
    }
}