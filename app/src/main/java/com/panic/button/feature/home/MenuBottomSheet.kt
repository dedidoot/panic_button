package com.panic.button.feature.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.panic.button.R
import com.panic.button.core.api.log
import com.panic.button.core.base.getScreenInches
import com.panic.button.core.base.inflate

class MenuBottomSheet : BottomSheetDialogFragment() {

    private var behavior: BottomSheetBehavior<*>? = null
    private var dialog: BottomSheetDialog? = null
    private var viewDialog: View? = null

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        context?.let {
            dialog = BottomSheetDialog(it, STYLE_NO_FRAME)
            dialog?.setCanceledOnTouchOutside(false)
            viewDialog = it.inflate(R.layout.bottom_sheet_menu, null)
            dialog?.setContentView(viewDialog ?: LinearLayout(it))
            behavior = BottomSheetBehavior.from(viewDialog?.parent as View)
            behavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(p0: View, newState: Float) {
                }

                override fun onStateChanged(p0: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        behavior?.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            })
            return dialog ?: Dialog(it)
        } ?: kotlin.run {
            return DialogFragment().onCreateDialog(savedInstanceState)
        }
    }

    override fun onStart() {
        super.onStart()
        val metrics = resources.displayMetrics
        val calculate = activity?.getScreenInches()?.toInt() ?: 0
        behavior?.peekHeight = metrics.heightPixels / calculate
    }

    fun showDialog(
        supportFragmentManager: FragmentManager,
        eventListener: (Int) -> Unit
    ): MenuBottomSheet {
        showNow(supportFragmentManager, "")

        /*dialog?.noTextView?.setOnClickListener {
            eventListener(VALUE_NO)
            dismiss()
        }*/
        return this
    }

    companion object {
        var VALUE_YES = 1
        var VALUE_NO = 0
    }
}