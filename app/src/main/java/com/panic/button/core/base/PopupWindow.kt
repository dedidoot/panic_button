package com.panic.button.core.base

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.panic.button.R
import com.panic.button.core.model.GeneralModel
import kotlinx.android.synthetic.main.view_popup_window.view.*

class PopupWindow : FrameLayout {

    private var clickEventListener: ((GeneralModel) -> Unit)? = null
    private var adapter = Adapter(context, arrayListOf())
    private var popupWindow: PopupWindow? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes)

    init {
        inflate(context, R.layout.view_popup_window, this)
        popupWindow = PopupWindow(this, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow?.animationStyle = android.R.style.Animation_Dialog
        popupWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow?.isOutsideTouchable = false

        popRecyclerView.adapter = adapter
    }

    fun showPopup(view: View) {
        popupWindow?.showAsDropDown(view)
    }

    fun setEventListener(eventListener: ((GeneralModel) -> Unit)? = null) {
        this.clickEventListener = eventListener
    }

    fun addItems(items: ArrayList<GeneralModel>) {
        adapter.addItems(items)
    }

    fun clearAll() {
        adapter.clearAll()
    }

    fun setWidthOrHeight(width: Int? = null, height: Int? = null) {
        val params = itemView.layoutParams
        width?.apply {
            params.width = this
        }
        height?.apply {
            params.height = this
        }
    }

    fun setGravity(gravity: Int) {
        val viewGroup = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        viewGroup.gravity = gravity
        val dimension = resources.getDimensionPixelSize(R.dimen.rajawali_10dp)
        val dimensionTop = resources.getDimensionPixelSize(R.dimen.rajawali_4dp)
        viewGroup.setMargins(dimension, dimensionTop, dimension, dimension)
        itemView.layoutParams = viewGroup
    }

    inner class Adapter(context: Context, items: ArrayList<GeneralModel>) : RecyclerAdapter<GeneralModel, Adapter.DataItem>(context, items) {

        override fun loadMore() {}

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataItem {
            val textView = TextView(context)
            textView.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
            textView.typeface = ResourcesCompat.getFont(context, R.font.font_regular)
            textView.textSize = resources.getDimensionPixelSize(R.dimen.rajawali_6sp).toFloat()
            textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val dimensionLeftRight = resources.getDimensionPixelSize(R.dimen.rajawali_10dp)
            val dimensionTopBottom = resources.getDimensionPixelSize(R.dimen.rajawali_6dp)
            textView.setPadding(dimensionLeftRight, dimensionTopBottom, dimensionLeftRight, dimensionTopBottom)
            return DataItem(textView)
        }

        inner class DataItem(private val textView: TextView) : BaseViewHolder(textView) {

            override fun bind(item: GeneralModel) {
                textView.text = item.name
                textView.setOnClickListener {
                    clickEventListener?.let { it1 -> it1(item) }
                    popupWindow?.dismiss()
                }
            }
        }
    }
}