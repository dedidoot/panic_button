package com.panic.button.core.base.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.widget.FrameLayout
import com.panic.button.R
import com.panic.button.core.base.isVisible
import com.panic.button.core.base.loadGlide
import kotlinx.android.synthetic.main.item_upload.view.*

class ItemUploadView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    var fileId: String? = null

    init {
        inflate(context, R.layout.item_upload, this)
    }

    fun setImageBitmap(resource: Bitmap) {
        img_1.setImageBitmap(resource)
    }

    fun setAlreadyImage() {
        remove_1.isVisible = true
        add_1.isVisible = false
    }

    fun isIsAlreadyImage() : Boolean {
        return remove_1.isVisible
    }

    fun setDefault() {
        remove_1.isVisible = false
        add_1.isVisible = true
        itemView.isEnabled = true
        img_1.setImageBitmap(null)
    }

    fun setItemViewEventListener(event: () -> Unit) {
        itemView.isEnabled = true
        itemView.setOnClickListener {
            event()
        }
    }

    fun setItemViewNoClick() {
        itemView.isEnabled = false
    }

    fun setRemoveEventListener(event: () -> Unit) {
        remove_1.setOnClickListener {
            event()
        }
    }

    fun setImageUrl(resource: String) {
        img_1.loadGlide(resource)
    }

    fun setIcon(iconId : Int) {
        add_1.setImageResource(iconId)
    }

    fun setGoneAdd_1Image() {
        add_1.isVisible = false
    }

    fun setBackgroundCard(colorId: Int) {
        itemView.setCardBackgroundColor(colorId)
    }
}