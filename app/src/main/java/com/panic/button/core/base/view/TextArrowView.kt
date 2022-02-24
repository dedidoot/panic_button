package com.panic.button.core.base.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.panic.button.R
import com.panic.button.core.base.isVisible
import com.panic.button.core.model.GeneralModel
import kotlinx.android.synthetic.main.view_text_arrow.view.*

class TextArrowView : FrameLayout {

    private var event: ((Int) -> Unit?)? = null
    private var isArrowDown = true
    private var eventClickVillageName: ((String) -> Unit?)? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes)

    init {
        inflate(context, R.layout.view_text_arrow, this)
        expandView.setOnClickListener {
            event?.apply {
                this(statsView.childCount)
            }
        }
    }

    fun collapseView() {
        statsView.isVisible = false
        imageView.setImageResource(R.drawable.ic_white_drop_down)
    }

    fun expandView() {
        statsView.isVisible = true
        imageView.setImageResource(R.drawable.ic_arrow_up)
    }

    fun setEventClick(event: (Int) -> Unit) {
        this.event = event
    }

    fun setText(title: String?) {
        textView.text = title ?: "-"
    }

    fun setImage(resourceId: Int) {
        imageView.setImageResource(resourceId)
    }

    fun setEventClickVillageName(event: (String) -> Unit) {
        eventClickVillageName = event
    }

    fun bindStats(statsModel: ArrayList<GeneralModel>?) {
        statsModel?.forEach {
            val viewStats = StatisticVillageView(context)
            viewStats.setEventClickVillageName { villageName ->
                eventClickVillageName?.let { it1 -> it1(villageName) }
            }
            viewStats.setVillageName(it.name)
            viewStats.setCountDirawat("Positif\n${it.totalCovidPositive}")
            viewStats.setCountMeninggal("Meninggal\n${it.totalCovidDeath}")
            viewStats.setCountSembuh("Sembuh\n${it.totalCovidRecovered}")
            statsView.addView(viewStats)
        }
    }
}