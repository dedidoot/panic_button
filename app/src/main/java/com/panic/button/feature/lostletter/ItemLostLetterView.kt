package com.panic.button.feature.lostletter

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.panic.button.R
import com.panic.button.core.base.MediaHelper
import com.panic.button.core.base.showAlert
import com.panic.button.core.base.view.ItemUploadView
import kotlinx.android.synthetic.main.item_lost_letter.view.*
import java.io.File

class ItemLostLetterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var index = 0
    var slug = ""
    var itemsUploadView = ArrayList<ItemUploadView>()

    init {
        inflate(context, R.layout.item_lost_letter, this)
    }

    fun setViewSlug(value: String?) {
        slug = value ?: ""
    }

    fun setNameTypeTxt(value: String?) {
        nameTypeTxt.text = value ?: ""
    }

    fun setFile(file: File?) {
        file?.absolutePath?.let {
            itemsUploadView.lastOrNull()?.apply {
                setImageBitmap(MediaHelper.decodeScaledBitmapFromSdCard(it, 104, 94))
                setAlreadyImage()
                setItemViewNoClick()
                setRemoveEventListener {
                    removeUploadedView(this)
                }
            }
        } ?: kotlin.run {
            showAlert(context, "Image Error")
        }
    }

    private fun removeUploadedView(itemUploadView: ItemUploadView) {
        val count = rootUploadView?.childCount ?: 0
        for (viewIndex in 0 until count) {
            if (rootUploadView.getChildAt(viewIndex) is ItemUploadView) {
                val view = rootUploadView.getChildAt(viewIndex) as ItemUploadView
                if (view == itemUploadView) {
                    rootUploadView.removeView(view)
                    itemsUploadView.remove(itemUploadView)
                    break
                }
            }
        }
    }

    fun setAddUploadEventListener(index: Int, event: () -> Unit) {
        fabButton.setOnClickListener {
            val countFilesNotUploaded = ArrayList<Boolean>()
            val count = rootUploadView?.childCount ?: 0
            for (viewIndex in 0 until count) {
                if (rootUploadView.getChildAt(viewIndex) is ItemUploadView) {
                    val view = rootUploadView.getChildAt(viewIndex) as ItemUploadView
                    if (!view.isIsAlreadyImage()) {
                        countFilesNotUploaded.add(true)
                    }
                }
            }

            if (countFilesNotUploaded.size == 0 || itemsUploadView.size == 0) {
                val newItemUploadView = ItemUploadView(context)
                newItemUploadView.tag = "${nameTypeTxt.tag}"
                newItemUploadView.setItemViewEventListener {
                    this.index = index
                    event()
                }
                rootUploadView.addView(newItemUploadView, rootUploadView.childCount - 1)
                itemsUploadView.add(newItemUploadView)
            } else {
                showAlert(context, "Mohon upload sebelumnya terlebih dahulu!")
            }
        }
    }
}