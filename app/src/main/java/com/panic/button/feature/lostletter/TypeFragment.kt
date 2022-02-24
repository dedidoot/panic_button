package com.panic.button.feature.lostletter

import android.content.Context
import android.content.res.ColorStateList
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import com.panic.button.BR
import com.panic.button.R
import com.panic.button.core.base.MvvmFragment
import com.panic.button.core.base.showAlert
import com.panic.button.core.model.GeneralModel
import com.panic.button.databinding.FragmentTypeBinding
import kotlinx.android.synthetic.main.fragment_type.*

class TypeFragment : MvvmFragment<FragmentTypeBinding, LostLetterViewModel>(LostLetterViewModel::class) {

    private lateinit var mContext: Context
    private var colorWhite: Int = 0

    override val layoutResource: Int = R.layout.fragment_type

    override val bindingVariable: Int = BR.viewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        colorWhite = ContextCompat.getColor(context, R.color.white)
        mContext = context
    }

    override fun viewLoaded() {
        binding.fragment = this
        registerObserver()
    }

    private fun registerObserver() {
        viewModel.lostLetterTypeResponse.observe(this, Observer {
            it?.apply {
                setupTypeView(data)
            }
        })
    }

    private fun setupTypeView(data: ArrayList<GeneralModel>?) {
        lostLetterView.removeAllViews()
        data?.forEach {
            lostLetterView.addView(getTypeCheckBox(it))
        }
    }

    private fun getTypeCheckBox(model: GeneralModel): AppCompatCheckBox {
        val view = AppCompatCheckBox(mContext)
        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.topMargin = resources.getDimensionPixelSize(R.dimen._8sdp)
        view.layoutParams = params
        view.buttonTintList = ColorStateList.valueOf(colorWhite)
        view.typeface = ResourcesCompat.getFont(mContext, R.font.font_regular)
        view.setTextSize(COMPLEX_UNIT_SP, resources.getDimension(R.dimen._4sdp))
        view.setTextColor(colorWhite)
        view.tag = model.id
        view.text = model.name
        return view
    }

    fun onContinue() {
        val typesId = ArrayList<String>()
        for (index in 0 until lostLetterView.childCount) {
            val view = lostLetterView.getChildAt(index) as AppCompatCheckBox
            if (view.isChecked) {
                typesId.add("${view.tag}")
            }
        }
        if (typesId.size > 0) {
            viewModel.nextPage()
            viewModel.getLostLetterField(typesId)
        } else {
            showAlert(mContext, "Silahkan Pilih Surat Kehilangan")
        }
    }
}