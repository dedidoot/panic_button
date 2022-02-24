package com.panic.button.feature.skck

import android.view.View
import com.panic.button.R
import com.panic.button.BR
import com.panic.button.core.model.GeneralModel
import com.panic.button.core.model.LabelValueModel
import com.panic.button.databinding.FragmentPersonalBinding
import kotlinx.android.synthetic.main.fragment_personal.*
import kotlin.collections.ArrayList
import androidx.lifecycle.Observer
import com.panic.button.core.base.*
import java.util.*

class PersonalFragment : MvvmFragment<FragmentPersonalBinding, SkckViewModel>(SkckViewModel::class) {

    private var genderPopup: PopupWindow ?= null
    private var wniWnaPopup: PopupWindow ?= null
    private var statusMarriagePopup: PopupWindow ?= null
    private var religionPopup: PopupWindow ?= null
    private var provincePopup: PopupWindow ?= null
    private var cityPopup: PopupWindow ?= null
    private var subDistrictPopup: PopupWindow ?= null
    private var villagePopup: PopupWindow ?= null
    private var faceShapePopup: PopupWindow ?= null
    private var colorSkinPopup: PopupWindow ?= null
    private var hairSkinPopup: PopupWindow ?= null
    private var bloodTypePopup: PopupWindow ?= null
    private var bodyPopup: PopupWindow ?= null
    private var headPopup: PopupWindow ?= null
    private var hairPopup: PopupWindow ?= null
    private var foreheadTypePopup: PopupWindow ?= null
    private var eyeColorPopup: PopupWindow ?= null
    private var eyeDisorderPopup: PopupWindow ?= null
    private var noseShapePopup: PopupWindow ?= null
    private var lipShapePopup: PopupWindow ?= null
    private var teethShapePopup: PopupWindow ?= null
    private var chinShapePopup: PopupWindow ?= null
    private var earShapePopup: PopupWindow ?= null

    override val layoutResource: Int = R.layout.fragment_personal

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.fragment = this
        registerObserver()
        viewModel.getGeneralData()
    }

    private fun registerObserver() {
        viewModel.stepOneErrorsMessage.observe(this, Observer {
            it?.apply {
                makeHandler(500) {
                    scrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        })
        viewModel.generalDataResponse.observe(this, Observer {
            it?.apply {
                setupPopup()
                viewModel.getProvince()

                checkSessionAlreadyInput()
            }
        })
        viewModel.provinceResponse.observe(this, Observer { response ->
            provincePopup?.clearAll()
            provincePopup?.addItems(response)

            response.forEach {
                if (it.id?.equals(viewModel.skckStepOneModel.value?.province) == true) {
                    provinceTextView.text = it.name
                    viewModel.getCity(it.id)
                }
            }
        })
        viewModel.cityResponse.observe(this, Observer { response ->
            cityPopup?.clearAll()
            cityPopup?.addItems(response)

            response.forEach {
                if (it.id?.equals(viewModel.skckStepOneModel.value?.city) == true) {
                    cityTextView.text = it.name
                    viewModel.getSubDistrict(it.id)
                }
            }
        })
        viewModel.subDistrictResponse.observe(this, Observer { response ->
            subDistrictPopup?.clearAll()
            subDistrictPopup?.addItems(response)

            response.forEach {
                if (it.id?.equals(viewModel.skckStepOneModel.value?.subDistrictId) == true) {
                    subDistrictTextView.text = it.name
                    viewModel.getVillage(it.id)
                }
            }
        })
        viewModel.villageResponse.observe(this, Observer { response ->
            villagePopup?.clearAll()
            villagePopup?.addItems(response)

            response.forEach {
                if (it.id?.equals(viewModel.skckStepOneModel.value?.villageId) == true) {
                    villageTextView.text = it.name
                }
            }
        })
    }

    private fun checkSessionAlreadyInput() {
        viewModel.getGeneralDataModel()?.gender?.forEach {
            if (it.value == viewModel.skckStepOneModel.value?.gender) {
                genderTextView.text = it.label
            }
        }
        viewModel.getGeneralDataModel()?.bloodType?.forEach {
            if (it.value == viewModel.skckStepOneModel.value?.bloodType) {
                bloodTextView.text = it.label
            }
        }
        viewModel.getGeneralDataModel()?.marriageStatus?.forEach {
            if (it.value == viewModel.skckStepOneModel.value?.marriageStatus) {
                statusMarriageTextView.text = it.label
            }
        }
        viewModel.getGeneralDataModel()?.religion?.forEach {
            if (it.value == viewModel.skckStepOneModel.value?.religion) {
                religionTextView.text = it.label
            }
        }
        viewModel.getGeneralDataModel()?.bodyShape?.forEach {
            if (it.value == viewModel.skckStepOneModel.value?.bodyShape) {
                bodyTextView.text = it.label
            }
        }
        viewModel.getGeneralDataModel()?.chinShape?.forEach {
            if (it.value == viewModel.skckStepOneModel.value?.chinShape) {
                chinShapeTextView.text = it.label
            }
        }
        viewModel.getGeneralDataModel()?.colorSkin?.forEach {
            if (it.value == viewModel.skckStepOneModel.value?.colorSkin) {
                colorSkinTextView.text = it.label
            }
        }
        viewModel.getGeneralDataModel()?.earShape?.forEach {
            if (it.value == viewModel.skckStepOneModel.value?.earShape) {
                earShapeTextView.text = it.label
            }
        }
        viewModel.getGeneralDataModel()?.eyeColor?.forEach {
            if (it.value == viewModel.skckStepOneModel.value?.eyeColor) {
                eyeColorTextView.text = it.label
            }
        }
        viewModel.getGeneralDataModel()?.eyeDisorder?.forEach {
            if (it.value == viewModel.skckStepOneModel.value?.eyeDisorder) {
                eyeDisorderTextView.text = it.label
            }
        }
        viewModel.getGeneralDataModel()?.faceShape?.forEach {
            if (it.value == viewModel.skckStepOneModel.value?.faceShape) {
                faceShapeTextView.text = it.label
            }
        }
        viewModel.getGeneralDataModel()?.foreheadType?.forEach {
            if (it.value == viewModel.skckStepOneModel.value?.foreheadType) {
                foreheadTypeTextView.text = it.label
            }
        }
        viewModel.getGeneralDataModel()?.hairSkin?.forEach {
            if (it.value == viewModel.skckStepOneModel.value?.hairSkin) {
                hairSkinTextView.text = it.label
            }
        }
        viewModel.getGeneralDataModel()?.hairType?.forEach {
            if (it.value == viewModel.skckStepOneModel.value?.hairType) {
                hairTypeTextView.text = it.label
            }
        }
        viewModel.getGeneralDataModel()?.headShape?.forEach {
            if (it.value == viewModel.skckStepOneModel.value?.headShape) {
                headTextView.text = it.label
            }
        }
        viewModel.getGeneralDataModel()?.lipShape?.forEach {
            if (it.value == viewModel.skckStepOneModel.value?.lipShape) {
                lipShapeTextView.text = it.label
            }
        }
        viewModel.getGeneralDataModel()?.noseShape?.forEach {
            if (it.value == viewModel.skckStepOneModel.value?.noseShape) {
                noseShapeTextView.text = it.label
            }
        }
        viewModel.getGeneralDataModel()?.teethShape?.forEach {
            if (it.value == viewModel.skckStepOneModel.value?.teethShape) {
                teethShapeTextView.text = it.label
            }
        }

        viewModel.skckStepOneModel.value?.nationality.takeIf {!it.isNullOrEmpty()}?.apply {
            wniWnaTextView.text = this.toUpperCase(Locale.getDefault())
        }

    }

    fun onDate() {
        context?.apply {
            hideKeyboard(this)
            val calendar = DialogCalender(this)
            calendar.setIsStartYear1980()
            calendar.setupDialog({
                viewModel.skckStepOneModel.value?.dateOfBirth = it
                viewModel.refreshSkckModel()
            }, DialogCalender.year_day_month2)
            calendar.showDialog()
        }
    }

    private fun bindPopup(labelValueModel : ArrayList<LabelValueModel>?) : ArrayList<GeneralModel> {
        val items = ArrayList<GeneralModel>()
        labelValueModel?.forEach {
            items.add(GeneralModel(id = it.value, name = it.label))
        }
        return items
    }

    private fun setupPopup() {
        setupWniWnaPopupPopup()
        setupGenderPopup()
        setupStatusMarriagePopup()
        setupReligionPopup()
        setupProvincePopup()
        setupCityPopup()
        setupSubDistrictPopup()
        setupVillagePopup()
        setupFaceShapePopup()
        setupColorSkinPopup()
        setupHairSkinPopup()
        setupBloodTypePopup()
        setupBodyPopup()
        setupHeadPopup()
        setupHairPopup()
        setupForeheadTypePopup()
        setupEyeColorPopup()
        setupEyeDisorderPopup()
        setupNoseShapePopup()
        setupLipShapePopup()
        setupTeethShapePopup()
        setupChinShapePopup()
        setupEarShapePopup()
    }

    private fun setupEarShapePopup() {
        context?.apply {
            earShapePopup = PopupWindow(this)
        }
        earShapePopup?.addItems(bindPopup(viewModel.getGeneralDataModel()?.earShape))
        earShapePopup?.setEventListener {
            viewModel.skckStepOneModel.value?.earShape = it.id
            earShapeTextView.text = it.name
        }
    }

    private fun setupChinShapePopup() {
        context?.apply {
            chinShapePopup = PopupWindow(this)
        }
        chinShapePopup?.addItems(bindPopup(viewModel.getGeneralDataModel()?.chinShape))
        chinShapePopup?.setEventListener {
            viewModel.skckStepOneModel.value?.chinShape = it.id
            chinShapeTextView.text = it.name
        }
    }

    private fun setupTeethShapePopup() {
        context?.apply {
            teethShapePopup = PopupWindow(this)
        }
        teethShapePopup?.addItems(bindPopup(viewModel.getGeneralDataModel()?.teethShape))
        teethShapePopup?.setEventListener {
            viewModel.skckStepOneModel.value?.teethShape = it.id
            teethShapeTextView.text = it.name
        }
    }

    private fun setupLipShapePopup() {
        context?.apply {
            lipShapePopup = PopupWindow(this)
        }
        lipShapePopup?.addItems(bindPopup(viewModel.getGeneralDataModel()?.lipShape))
        lipShapePopup?.setEventListener {
            viewModel.skckStepOneModel.value?.lipShape = it.id
            lipShapeTextView.text = it.name
        }
    }

    private fun setupNoseShapePopup() {
        context?.apply {
            noseShapePopup = PopupWindow(this)
        }
        noseShapePopup?.addItems(bindPopup(viewModel.getGeneralDataModel()?.noseShape))
        noseShapePopup?.setEventListener {
            viewModel.skckStepOneModel.value?.noseShape = it.id
            noseShapeTextView.text = it.name
        }
    }

    private fun setupEyeDisorderPopup() {
        context?.apply {
            eyeDisorderPopup = PopupWindow(this)
        }
        eyeDisorderPopup?.addItems(bindPopup(viewModel.getGeneralDataModel()?.eyeDisorder))
        eyeDisorderPopup?.setEventListener {
            viewModel.skckStepOneModel.value?.eyeDisorder = it.id
            eyeDisorderTextView.text = it.name
        }
    }

    private fun setupEyeColorPopup() {
        context?.apply {
            eyeColorPopup = PopupWindow(this)
        }
        eyeColorPopup?.addItems(bindPopup(viewModel.getGeneralDataModel()?.eyeColor))
        eyeColorPopup?.setEventListener {
            viewModel.skckStepOneModel.value?.eyeColor = it.id
            eyeColorTextView.text = it.name
        }
    }

    private fun setupForeheadTypePopup() {
        context?.apply {
            foreheadTypePopup = PopupWindow(this)
        }
        foreheadTypePopup?.addItems(bindPopup(viewModel.getGeneralDataModel()?.foreheadType))
        foreheadTypePopup?.setEventListener {
            viewModel.skckStepOneModel.value?.foreheadType = it.id
            foreheadTypeTextView.text = it.name
        }
    }

    private fun setupHairPopup() {
        context?.apply {
            hairPopup = PopupWindow(this)
        }
        hairPopup?.addItems(bindPopup(viewModel.getGeneralDataModel()?.hairType))
        hairPopup?.setEventListener {
            viewModel.skckStepOneModel.value?.hairType = it.id
            hairTypeTextView.text = it.name
        }
    }

    private fun setupHeadPopup() {
        context?.apply {
            headPopup = PopupWindow(this)
        }
        headPopup?.addItems(bindPopup(viewModel.getGeneralDataModel()?.headShape))
        headPopup?.setEventListener {
            viewModel.skckStepOneModel.value?.headShape = it.id
            headTextView.text = it.name
        }
    }

    private fun setupBodyPopup() {
        context?.apply {
            bodyPopup = PopupWindow(this)
        }
        bodyPopup?.addItems(bindPopup(viewModel.getGeneralDataModel()?.bodyShape))
        bodyPopup?.setEventListener {
            viewModel.skckStepOneModel.value?.bodyShape = it.id
            bodyTextView.text = it.name
        }
    }

    private fun setupBloodTypePopup() {
        context?.apply {
            bloodTypePopup = PopupWindow(this)
        }
        bloodTypePopup?.addItems(bindPopup(viewModel.getGeneralDataModel()?.bloodType))
        bloodTypePopup?.setEventListener {
            viewModel.skckStepOneModel.value?.bloodType = it.id
            bloodTextView.text = it.name
        }
    }

    private fun setupHairSkinPopup() {
        context?.apply {
            hairSkinPopup = PopupWindow(this)
        }
        hairSkinPopup?.addItems(bindPopup(viewModel.getGeneralDataModel()?.hairSkin))
        hairSkinPopup?.setEventListener {
            viewModel.skckStepOneModel.value?.hairSkin = it.id
            hairSkinTextView.text = it.name
        }
    }

    private fun setupColorSkinPopup() {
        context?.apply {
            colorSkinPopup = PopupWindow(this)
        }
        colorSkinPopup?.addItems(bindPopup(viewModel.getGeneralDataModel()?.colorSkin))
        colorSkinPopup?.setEventListener {
            viewModel.skckStepOneModel.value?.colorSkin = it.id
            colorSkinTextView.text = it.name
        }
    }

    private fun setupFaceShapePopup() {
        context?.apply {
            faceShapePopup = PopupWindow(this)
        }
        faceShapePopup?.addItems(bindPopup(viewModel.getGeneralDataModel()?.faceShape))
        faceShapePopup?.setEventListener {
            viewModel.skckStepOneModel.value?.faceShape = it.id
            faceShapeTextView.text = it.name
        }
    }

    private fun setupVillagePopup() {
        context?.apply {
            villagePopup = PopupWindow(this)
        }
        villagePopup?.setEventListener {
            viewModel.skckStepOneModel.value?.villageId = it.id
            villageTextView.text = it.name
        }
    }

    private fun setupSubDistrictPopup() {
        context?.apply {
            subDistrictPopup = PopupWindow(this)
        }
        subDistrictPopup?.setEventListener {
            viewModel.skckStepOneModel.value?.subDistrictId = it.id
            subDistrictTextView.text = it.name
            viewModel.getVillage(it.id)

            villageTextView.text = ""
            viewModel.skckStepOneModel.value?.villageId = ""
        }
    }

    private fun setupCityPopup() {
        context?.apply {
            cityPopup = PopupWindow(this)
        }
        cityPopup?.setEventListener {
            viewModel.skckStepOneModel.value?.city = it.id
            cityTextView.text = it.name
            viewModel.getSubDistrict(it.id)

            subDistrictTextView.text = ""
            viewModel.skckStepOneModel.value?.subDistrictId = ""

            villageTextView.text = ""
            viewModel.skckStepOneModel.value?.villageId = ""
        }
    }

    private fun setupProvincePopup() {
        context?.apply {
            provincePopup = PopupWindow(this)
        }
        provincePopup?.setEventListener {
            viewModel.skckStepOneModel.value?.province = it.id
            provinceTextView.text = it.name
            viewModel.getCity(it.id)

            cityTextView.text = ""
            viewModel.skckStepOneModel.value?.city = ""

            subDistrictTextView.text = ""
            viewModel.skckStepOneModel.value?.subDistrictId = ""

            villageTextView.text = ""
            viewModel.skckStepOneModel.value?.villageId = ""
        }
    }

    private fun setupReligionPopup() {
        context?.apply {
            religionPopup = PopupWindow(this)
        }
        religionPopup?.addItems(bindPopup(viewModel.getGeneralDataModel()?.religion))
        religionPopup?.setEventListener {
            viewModel.skckStepOneModel.value?.religion = it.id
            religionTextView.text = it.name
        }
    }

    private fun setupStatusMarriagePopup() {
        context?.apply {
            statusMarriagePopup = PopupWindow(this)
        }
        statusMarriagePopup?.addItems(bindPopup(viewModel.getGeneralDataModel()?.marriageStatus))
        statusMarriagePopup?.setEventListener {
            viewModel.skckStepOneModel.value?.marriageStatus = it.id
            statusMarriageTextView.text = it.name
        }
    }

    private fun setupGenderPopup() {
        context?.apply {
            genderPopup = PopupWindow(this)
        }
        genderPopup?.addItems(bindPopup(viewModel.getGeneralDataModel()?.gender))
        genderPopup?.setEventListener {
            viewModel.skckStepOneModel.value?.gender = it.id
            genderTextView.text = it.name
        }
    }

    private fun setupWniWnaPopupPopup() {
        context?.apply {
            wniWnaPopup = PopupWindow(this)
        }
        wniWnaPopup?.addItems(arrayListOf(GeneralModel(id = "wni", name = "WNI"), GeneralModel(id = "wna", name = "WNA")))
        wniWnaPopup?.setEventListener {
            viewModel.skckStepOneModel.value?.nationality = it.id
            wniWnaTextView.text = it.name
        }
    }

    fun onWniWna() {
        hideKeyboard(wniWnaTextView.context)
        wniWnaPopup?.showPopup(wniWnaTextView)
    }

    fun onReligion() {
        hideKeyboard(religionTextView.context)
        religionPopup?.showPopup(religionTextView)
    }

    fun onProvince() {
        hideKeyboard(provinceTextView.context)
        provincePopup?.showPopup(provinceTextView)
    }

    fun onCity() {
        hideKeyboard(cityTextView.context)
        cityPopup?.showPopup(provinceTextView)
    }

    fun onSubDistrict() {
        hideKeyboard(cityTextView.context)
        subDistrictPopup?.showPopup(subDistrictTextView)
    }

    fun onFaceShape() {
        hideKeyboard(faceShapeTextView.context)
        faceShapePopup?.showPopup(faceShapeTextView)
    }

    fun onVillage() {
        hideKeyboard(villageTextView.context)
        villagePopup?.showPopup(villageTextView)
    }

    fun onStatusMarriage() {
        hideKeyboard(genderTextView.context)
        statusMarriagePopup?.showPopup(statusMarriageTextView)
    }

    fun onGender() {
        hideKeyboard(genderTextView.context)
        genderPopup?.showPopup(genderTextView)
    }

    fun onColorSkin() {
        hideKeyboard(colorSkinTextView.context)
        colorSkinPopup?.showPopup(colorSkinTextView)
    }

    fun onHairSkin() {
        hideKeyboard(hairSkinTextView.context)
        hairSkinPopup?.showPopup(hairSkinTextView)
    }

    fun onBloodType() {
        hideKeyboard(bloodTextView.context)
        bloodTypePopup?.showPopup(bloodTextView)
    }

    fun onBodyShape() {
        hideKeyboard(bodyTextView.context)
        bodyPopup?.showPopup(bodyTextView)
    }

    fun onHead() {
        hideKeyboard(headTextView.context)
        headPopup?.showPopup(headTextView)
    }

    fun onHairType() {
        hideKeyboard(hairTypeTextView.context)
        hairPopup?.showPopup(hairTypeTextView)
    }

    fun onForeheadType() {
        hideKeyboard(foreheadTypeTextView.context)
        foreheadTypePopup?.showPopup(foreheadTypeTextView)
    }

    fun onEyeColor() {
        hideKeyboard(eyeColorTextView.context)
        eyeColorPopup?.showPopup(eyeColorTextView)
    }

    fun onEyeDisorder() {
        hideKeyboard(eyeDisorderTextView.context)
        eyeDisorderPopup?.showPopup(eyeDisorderTextView)
    }

    fun onNoseShape() {
        hideKeyboard(noseShapeTextView.context)
        noseShapePopup?.showPopup(noseShapeTextView)
    }

    fun onLipShape() {
        hideKeyboard(lipShapeTextView.context)
        lipShapePopup?.showPopup(lipShapeTextView)
    }

    fun onTeethShape() {
        hideKeyboard(teethShapeTextView.context)
        teethShapePopup?.showPopup(teethShapeTextView)
    }

    fun onChinShape() {
        hideKeyboard(chinShapeTextView.context)
        chinShapePopup?.showPopup(chinShapeTextView)
    }

    fun onEarShape() {
        hideKeyboard(earShapeTextView.context)
        if (errorMessageTextView.isVisible) {
            earShapePopup?.showPopup(earShapeTextView)
        } else {
            earShapeTextView.post {
                scrollView.fullScroll(View.FOCUS_DOWN)
                earShapePopup?.showPopup(earShapeTextView)
            }
        }
    }
}