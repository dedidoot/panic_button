package com.panic.button.feature.skck

import android.view.View
import androidx.lifecycle.Observer
import com.panic.button.R
import com.panic.button.BR
import com.panic.button.core.base.MvvmFragment
import com.panic.button.core.base.PopupWindow
import com.panic.button.core.base.hideKeyboard
import com.panic.button.core.base.makeHandler
import com.panic.button.core.model.GeneralModel
import com.panic.button.databinding.FragmentEducationBinding
import kotlinx.android.synthetic.main.fragment_education.*

class EducationFragment : MvvmFragment<FragmentEducationBinding, SkckViewModel>(SkckViewModel::class) {

    private var education1Popup: PopupWindow? = null
    private var education2Popup: PopupWindow? = null
    private var education3Popup: PopupWindow? = null

    override val layoutResource: Int = R.layout.fragment_education

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.fragment = this
        setupEducation1Popup()
        setupEducation2Popup()
        setupEducation3Popup()
        viewModel.skckStepThreeModel.observe(this, Observer { model ->
            model?.apply {
                bindPopup().forEach {
                    if (it.name == viewModel.skckStepThreeModel.value?.education_1_grade) {
                        educationType1TextView.text = it.name
                    }
                }
                bindPopup().forEach {
                    if (it.name == viewModel.skckStepThreeModel.value?.education_2_grade) {
                        educationType2TextView.text = it.name
                    }
                }
                bindPopup().forEach {
                    if (it.name == viewModel.skckStepThreeModel.value?.education_3_grade) {
                        educationType3TextView.text = it.name
                    }
                }
            }
        })
        viewModel.stepThreeErrorsMessage.observe(this, Observer {
            it?.apply {
                makeHandler(500) {
                    scrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        })
    }

    private fun setupEducation3Popup() {
        context?.apply {
            education3Popup = PopupWindow(this)
        }
        education3Popup?.addItems(bindPopup())
        education3Popup?.setEventListener {
            viewModel.skckStepThreeModel.value?.education_3_grade = it.name
            educationType3TextView.text = it.name
        }
    }

    private fun setupEducation2Popup() {
        context?.apply {
            education2Popup = PopupWindow(this)
        }
        education2Popup?.addItems(bindPopup())
        education2Popup?.setEventListener {
            viewModel.skckStepThreeModel.value?.education_2_grade = it.name
            educationType2TextView.text = it.name
        }
    }

    private fun setupEducation1Popup() {
        context?.apply {
            education1Popup = PopupWindow(this)
        }
        education1Popup?.addItems(bindPopup())
        education1Popup?.setEventListener {
            viewModel.skckStepThreeModel.value?.education_1_grade = it.name
            educationType1TextView.text = it.name
        }
    }

    private fun bindPopup(): ArrayList<GeneralModel> {
        val items = ArrayList<GeneralModel>()
        items.add(GeneralModel(name = "SD"))
        items.add(GeneralModel(name = "SMP"))
        items.add(GeneralModel(name = "SMA"))
        items.add(GeneralModel(name = "S1"))
        items.add(GeneralModel(name = "S2"))
        items.add(GeneralModel(name = "S3"))
        items.add(GeneralModel(name = "Lainnya"))
        return items
    }

    fun onEducationType3() {
        context?.apply {
            hideKeyboard(this)
        }
        education3Popup?.showPopup(educationType3TextView)
    }

    fun onEducationType2() {
        context?.apply {
            hideKeyboard(this)
        }
        education2Popup?.showPopup(educationType2TextView)
    }

    fun onEducationType1() {
        context?.apply {
            hideKeyboard(this)
        }
        education1Popup?.showPopup(educationType1TextView)
    }
}