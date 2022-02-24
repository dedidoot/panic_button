package com.panic.button.core.base.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.panic.button.R
import kotlinx.android.synthetic.main.view_statistic_village.view.*

class StatisticVillageView : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes)

    init {
        inflate(context, R.layout.view_statistic_village, this)
    }

    fun setVillageName(title: String?) {
        villageName.text = title ?: "-"
    }

    fun setCountDirawat(title: String?) {
        dirawat.text = title ?: "-"
    }

    fun setCountMeninggal(title: String?) {
        meninggal.text = title ?: "-"
    }

    fun setCountSembuh(title: String?) {
        sembuh.text = title ?: "-"
    }

    fun setEventClickVillageName(event: (String) -> Unit) {
        seeVillageName.setOnClickListener {
            event("${villageName.text}")
        }
    }
}