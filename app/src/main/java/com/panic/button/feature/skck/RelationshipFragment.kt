package com.panic.button.feature.skck

import android.view.View
import androidx.lifecycle.Observer
import com.panic.button.R
import com.panic.button.BR
import com.panic.button.core.base.MvvmFragment
import com.panic.button.core.base.makeHandler
import com.panic.button.core.model.SkckStepTwoModel
import com.panic.button.databinding.FragmentRelathionshipBinding
import kotlinx.android.synthetic.main.fragment_relathionship.*
import java.util.*

class RelationshipFragment : MvvmFragment<FragmentRelathionshipBinding, SkckViewModel>(SkckViewModel::class){

    override val layoutResource: Int = R.layout.fragment_relathionship

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.fragment = this
        registerObserver()
    }

    private fun setupBrotherView() {
        brotherView.titleRelationship = "Data Saudara Kandung/Tiri"
        brotherView.setReligionPopup(viewModel.getGeneralDataModel()?.religion)
        brotherView.setNationalityPopupEventListener {
            viewModel.skckStepTwoModel.value?.sibling_1_nationality = it.id
        }
        brotherView.setNameRelationshipTextChangedListener {
            viewModel.skckStepTwoModel.value?.sibling_1_full_name = it
        }
        brotherView.setYearBirthRelationshipTextChangedListener {
            viewModel.skckStepTwoModel.value?.sibling_1_year_of_birth = it
        }
        brotherView.setReligionPopupEventListener {
            viewModel.skckStepTwoModel.value?.sibling_1_religion = it.id
        }
        brotherView.setJobRelationshipTextChangedListener {
            viewModel.skckStepTwoModel.value?.sibling_1_job = it
        }
        brotherView.setAddressRelationshipTextChangedListener {
            viewModel.skckStepTwoModel.value?.sibling_1_address = it
        }
    }

    private fun setupMotherView() {
        motherView.titleRelationship = "Data Ibu Kandung"
        motherView.setReligionPopup(viewModel.getGeneralDataModel()?.religion)
        motherView.setNationalityPopupEventListener {
            viewModel.skckStepTwoModel.value?.mother_nationality = it.id
        }
        motherView.setNameRelationshipTextChangedListener {
            viewModel.skckStepTwoModel.value?.mother_full_name = it
        }
        motherView.setYearBirthRelationshipTextChangedListener {
            viewModel.skckStepTwoModel.value?.mother_year_of_birth = it
        }
        motherView.setReligionPopupEventListener {
            viewModel.skckStepTwoModel.value?.mother_religion = it.id
        }
        motherView.setJobRelationshipTextChangedListener {
            viewModel.skckStepTwoModel.value?.mother_job = it
        }
        motherView.setAddressRelationshipTextChangedListener {
            viewModel.skckStepTwoModel.value?.mother_address = it
        }
    }

    private fun setupFatherView() {
        fatherView.titleRelationship = "Data Ayah Kandung"
        fatherView.setReligionPopup(viewModel.getGeneralDataModel()?.religion)
        fatherView.setNationalityPopupEventListener {
            viewModel.skckStepTwoModel.value?.father_nationality = it.id
        }
        fatherView.setNameRelationshipTextChangedListener {
            viewModel.skckStepTwoModel.value?.father_full_name = it
        }
        fatherView.setYearBirthRelationshipTextChangedListener {
            viewModel.skckStepTwoModel.value?.father_year_of_birth = it
        }
        fatherView.setReligionPopupEventListener {
            viewModel.skckStepTwoModel.value?.father_religion = it.id
        }
        fatherView.setJobRelationshipTextChangedListener {
            viewModel.skckStepTwoModel.value?.father_job = it
        }
        fatherView.setAddressRelationshipTextChangedListener {
            viewModel.skckStepTwoModel.value?.father_address = it
        }
    }

    private fun setupHusbandAndWifeView() {
        husbandAndWifeView.titleRelationship = "Data Pasangan (Suami/Istri)"
        husbandAndWifeView.setReligionPopup(viewModel.getGeneralDataModel()?.religion)
        husbandAndWifeView.setNationalityPopupEventListener {
            viewModel.skckStepTwoModel.value?.spouse_nationality = it.id
        }
        husbandAndWifeView.setNameRelationshipTextChangedListener {
            viewModel.skckStepTwoModel.value?.spouse_full_name = it
        }
        husbandAndWifeView.setYearBirthRelationshipTextChangedListener {
            viewModel.skckStepTwoModel.value?.spouse_year_of_birth = it
        }
        husbandAndWifeView.setReligionPopupEventListener {
            viewModel.skckStepTwoModel.value?.spouse_religion = it.id
        }
        husbandAndWifeView.setJobRelationshipTextChangedListener {
            viewModel.skckStepTwoModel.value?.spouse_job = it
        }
        husbandAndWifeView.setAddressRelationshipTextChangedListener {
            viewModel.skckStepTwoModel.value?.spouse_address = it
        }
    }

    private fun registerObserver() {
        viewModel.generalDataResponse.observe(this, Observer {
            it?.apply {
                setupHusbandAndWifeView()
                setupFatherView()
                setupMotherView()
                setupBrotherView()
                bindSessionHusbandAndWifeView(viewModel.skckStepTwoModel.value)
                bindSessionFatherView(viewModel.skckStepTwoModel.value)
                bindSessionMotherView(viewModel.skckStepTwoModel.value)
                bindSessionBrotherView(viewModel.skckStepTwoModel.value)
            }
        })

        viewModel.stepTwoErrorsMessage.observe(this, Observer {
            it?.apply {
                makeHandler(500) {
                    scrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        })
    }

    private fun bindSessionBrotherView(model: SkckStepTwoModel?) {
        model?.apply {
            brotherView.nameRelationship = sibling_1_full_name
            brotherView.jobRelationship = sibling_1_job
            brotherView.yearBirthRelationship = sibling_1_year_of_birth
            brotherView.addressRelationship = sibling_1_address

            viewModel.getGeneralDataModel()?.religion?.forEach {
                if (it.value == sibling_1_religion) {
                    brotherView.religionRelationship = it.label
                }
            }
            brotherView.nationalityRelationship = sibling_1_nationality?.toUpperCase(Locale.getDefault())
        }
    }

    private fun bindSessionMotherView(model: SkckStepTwoModel?) {
        model?.apply {
            motherView.nameRelationship = mother_full_name
            motherView.jobRelationship = mother_job
            motherView.yearBirthRelationship = mother_year_of_birth
            motherView.addressRelationship = mother_address

            viewModel.getGeneralDataModel()?.religion?.forEach {
                if (it.value == mother_religion) {
                    motherView.religionRelationship = it.label
                }
            }
            motherView.nationalityRelationship = mother_nationality?.toUpperCase(Locale.getDefault())
        }
    }

    private fun bindSessionFatherView(model: SkckStepTwoModel?) {
        model?.apply {
            fatherView.nameRelationship = father_full_name
            fatherView.jobRelationship = father_job
            fatherView.yearBirthRelationship = father_year_of_birth
            fatherView.addressRelationship = father_address

            viewModel.getGeneralDataModel()?.religion?.forEach {
                if (it.value == father_religion) {
                    fatherView.religionRelationship = it.label
                }
            }
            fatherView.nationalityRelationship = father_nationality?.toUpperCase(Locale.getDefault())
        }
    }

    private fun bindSessionHusbandAndWifeView(model: SkckStepTwoModel?) {
        model?.apply {
            husbandAndWifeView.nameRelationship = spouse_full_name
            husbandAndWifeView.jobRelationship = spouse_job
            husbandAndWifeView.yearBirthRelationship = spouse_year_of_birth
            husbandAndWifeView.addressRelationship = spouse_address

            viewModel.getGeneralDataModel()?.religion?.forEach {
                if (it.value == spouse_religion) {
                    husbandAndWifeView.religionRelationship = it.label
                }
            }
            husbandAndWifeView.nationalityRelationship = spouse_nationality?.toUpperCase(Locale.getDefault())
        }
    }
}