package com.panic.button.feature.skck

import android.view.View
import androidx.lifecycle.Observer
import com.panic.button.R
import com.panic.button.BR
import com.panic.button.core.base.*
import com.panic.button.core.model.GeneralModel
import com.panic.button.databinding.FragmentViolationBinding
import kotlinx.android.synthetic.main.fragment_violation.*

class ViolationFragment : MvvmFragment<FragmentViolationBinding, SkckViewModel>(SkckViewModel::class) {

    private var pernahPidanaPopup: PopupWindow? = null
    private var sedangProsesPidanaPopup: PopupWindow? = null
    private var pernahPelanggaranHukumPopup: PopupWindow? = null

    override val layoutResource: Int = R.layout.fragment_violation

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.fragment = this
        setupPernahPidanaPopup()
        setupSedangProsesPidanaPopup()
        setupPernahPelanggaranHukumPopup()
        viewModel.stepFourErrorsMessage.observe(this, Observer {
            it?.apply {
                makeHandler(500) {
                    scrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        })
        viewModel.skckStepFourModel.observe(this, Observer { model ->
            model?.apply {
                bindPopupYesNo().forEach {
                    if (it.id == viewModel.skckStepFourModel.value?.criminals_7_answer) {
                        pernahPelanggaranHukumTextView.text = it.name
                    }
                }
                bindPopupYesNo().forEach {
                    if (it.id == viewModel.skckStepFourModel.value?.criminals_4_answer) {
                        sedangProsesPidanaTextView.text = it.name
                    }
                }
                bindPopupYesNo().forEach {
                    if (it.id == viewModel.skckStepFourModel.value?.criminals_1_answer) {
                        pernahPidanaTextView.text = it.name
                    }
                }
            }
        })
    }

    private fun setupPernahPelanggaranHukumPopup() {
        context?.apply {
            pernahPelanggaranHukumPopup = PopupWindow(this)
        }
        pernahPelanggaranHukumPopup?.addItems(bindPopupYesNo())
        pernahPelanggaranHukumPopup?.setEventListener {
            viewModel.skckStepFourModel.value?.criminals_7_answer = it.id
            pernahPelanggaranHukumTextView.text = it.name

            penaltyTextView.isVisible = isYes(it)
            penaltyEditText.isVisible = isYes(it)
            penaltyTimeTextView.isVisible = isYes(it)
            penaltyTimeEditText.isVisible = isYes(it)
        }
    }

    private fun setupSedangProsesPidanaPopup() {
        context?.apply {
            sedangProsesPidanaPopup = PopupWindow(this)
        }
        sedangProsesPidanaPopup?.addItems(bindPopupYesNo())
        sedangProsesPidanaPopup?.setEventListener {
            viewModel.skckStepFourModel.value?.criminals_4_answer = it.id
            sedangProsesPidanaTextView.text = it.name

            caseTextView.isVisible = isYes(it)
            caseEditText.isVisible = isYes(it)
            caseNameTextView.isVisible = isYes(it)
            caseNameEditText.isVisible = isYes(it)
        }
    }

    private fun setupPernahPidanaPopup() {
        context?.apply {
            pernahPidanaPopup = PopupWindow(this)
        }
        pernahPidanaPopup?.addItems(bindPopupYesNo())
        pernahPidanaPopup?.setEventListener {
            viewModel.skckStepFourModel.value?.criminals_1_answer = it.id
            pernahPidanaTextView.text = it.name

            perkaraTextView.isVisible = isYes(it)
            perkaraEditText.isVisible = isYes(it)
            vonisTextView.isVisible = isYes(it)
            vonisEditText.isVisible = isYes(it)
        }
    }

    private fun isYes(model : GeneralModel) : Boolean {
        return model.id == "1"
    }

    private fun bindPopupYesNo(): ArrayList<GeneralModel> {
        return arrayListOf(GeneralModel(id = "1", name = "Iya"), GeneralModel(id = "0", name = "Tidak"))
    }

    fun onPernahPidana() {
        context?.apply {
            hideKeyboard(this)
        }
        pernahPidanaPopup?.showPopup(pernahPidanaTextView)
    }

    fun onSedangProsesPidana() {
        context?.apply {
            hideKeyboard(this)
        }
        sedangProsesPidanaPopup?.showPopup(sedangProsesPidanaTextView)
    }

    fun onPernahPelanggaranHukum() {
        context?.apply {
            hideKeyboard(this)
        }
        pernahPelanggaranHukumPopup?.showPopup(pernahPelanggaranHukumTextView)
    }
}