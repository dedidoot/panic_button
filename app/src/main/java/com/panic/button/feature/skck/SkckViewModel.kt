package com.panic.button.feature.skck

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.panic.button.core.api.GSONManager
import com.panic.button.core.api.GetRequest
import com.panic.button.core.api.PostRequest
import com.panic.button.core.api.log
import com.panic.button.core.api.response.*
import com.panic.button.core.base.BaseApplication
import com.panic.button.core.base.BaseViewModel
import com.panic.button.core.base.mutableLiveDataOf
import com.panic.button.core.model.*
import kotlinx.android.synthetic.main.activity_skck.*

class SkckViewModel : BaseViewModel() {

    var fragments: ArrayList<Fragment> = arrayListOf(
        PersonalFragment(),
        RelationshipFragment(),
        EducationFragment(),
        ViolationFragment(),
        OtherFragment(),
        SuccessFragment()
    )

    val skckStepOneModel = MutableLiveData<SkckStepOneModel>()
    val skckStepTwoModel = MutableLiveData<SkckStepTwoModel>()
    val skckStepThreeModel = MutableLiveData<SkckStepThreeModel>()
    val skckStepFourModel = MutableLiveData<SkckStepFourModel>()
    val skckStepFiveModel = MutableLiveData<SkckStepFiveModel>()
    val isShowButton = MutableLiveData<Boolean>()
    val buttonText = MutableLiveData<String>()
    val generalDataResponse = MutableLiveData<GeneralDataResponse>()
    val provinceResponse = MutableLiveData<ArrayList<GeneralModel>>()
    val cityResponse = MutableLiveData<ArrayList<GeneralModel>>()
    val subDistrictResponse = MutableLiveData<ArrayList<GeneralModel>>()
    val villageResponse = MutableLiveData<ArrayList<GeneralModel>>()
    val isNextStep = MutableLiveData<Boolean>()
    val isLoading = mutableLiveDataOf<Boolean>()
    var stepOneErrorsMessage = mutableLiveDataOf<String>()
    var stepTwoErrorsMessage = mutableLiveDataOf<String>()
    var stepThreeErrorsMessage = mutableLiveDataOf<String>()
    var stepFourErrorsMessage = mutableLiveDataOf<String>()
    var stepFiveErrorsMessage = mutableLiveDataOf<String>()

    init {
        skckStepOneModel.value = SkckStepOneModel()
        skckStepTwoModel.value = SkckStepTwoModel()
        skckStepThreeModel.value = SkckStepThreeModel()
        skckStepFourModel.value = SkckStepFourModel()
        skckStepFiveModel.value = SkckStepFiveModel()
        BaseApplication.sessionManager?.skckStepOne.takeIf { !it.isNullOrEmpty() }?.apply {
            skckStepOneModel.value = GSONManager.fromJson(this, SkckStepOneModel::class.java)
        }
        BaseApplication.sessionManager?.skckStepTwo.takeIf { !it.isNullOrEmpty() }?.apply {
            skckStepTwoModel.value = GSONManager.fromJson(this, SkckStepTwoModel::class.java)
        }
        BaseApplication.sessionManager?.skckStepThree.takeIf { !it.isNullOrEmpty() }?.apply {
            skckStepThreeModel.value = GSONManager.fromJson(this, SkckStepThreeModel::class.java)
        }
        BaseApplication.sessionManager?.skckStepFour.takeIf { !it.isNullOrEmpty() }?.apply {
            skckStepFourModel.value = GSONManager.fromJson(this, SkckStepFourModel::class.java)
        }
        BaseApplication.sessionManager?.skckStepFive.takeIf { !it.isNullOrEmpty() }?.apply {
            skckStepFiveModel.value = GSONManager.fromJson(this, SkckStepFiveModel::class.java)
        }
        buttonText.value = "Lanjutkan"
        isShowButton.value = true
    }

    fun getGeneralData() {
        GetRequest(Urls.GET_GENERAL_DATA).get({
            generalDataResponse.value = GSONManager.fromJson(it, GeneralDataResponse::class.java)
        })
    }

    fun onChangeButtonText(position: Int) {
        if (isLastTwoFormFragment(position)) {
            buttonText.value = "Simpan"
        } else {
            buttonText.value = "Lanjutkan"
        }
    }

    private fun isLastTwoFormFragment(position: Int): Boolean {
        return position == (fragments.size - LAST_TWO_FORM_FRAGMENT_POSITION)
    }

    fun refreshSkckModel() {
        skckStepOneModel.value = skckStepOneModel.value
    }

    fun getGeneralDataModel(): GeneraDataModel? {
        return generalDataResponse.value?.data
    }

    fun getProvince() {
        GetRequest(Urls.GET_PROVINCE).get({
            provinceResponse.value =
                GSONManager.fromJson(it, ProvinceResponse::class.java).provinces
        })
    }

    fun getCity(provinceId: String?) {
        GetRequest(Urls.GET_CITY + provinceId).get({
            cityResponse.value = GSONManager.fromJson(it, CityResponse::class.java).cities
        })
    }

    fun getSubDistrict(cityId: String?) {
        GetRequest(Urls.GET_SUB_DISTRICT + cityId).get({
            subDistrictResponse.value =
                GSONManager.fromJson(it, SubDistrictResponse::class.java).subDistricts
        })
    }

    fun getVillage(subDistrictId: String?) {
        GetRequest(Urls.GET_VILAGE + subDistrictId).get({
            villageResponse.value = GSONManager.fromJson(it, VillageResponse::class.java).villages
        })
    }

    fun nextStep(currentItem: Int) {
        if (currentItem == 0) {
            postStepOne()
        } else if (currentItem == 1) {
            postStepTwo()
        } else if (currentItem == 2) {
            postStepThree()
        } else if (currentItem == 3) {
            postStepFour()
        } else if (currentItem == 4) {
            postStepFive()
        }
    }

    private fun postStepFive() {
        showLoading()
        skckStepFiveModel.value?.apply {

            policeCertificateId = BaseApplication.sessionManager?.policeCertificateId
            BaseApplication.sessionManager?.skckStepFive = GSONManager.toJson(this)

            PostRequest<SkckStepFiveModel>(Urls.POST_SKCK_STEP_FIVE).post(this, {
                val response = GSONManager.fromJson(it, SkckPostResponse::class.java)
                stepFiveErrorsMessage.value = ""
                if (response.isSuccess()) {
                    isNextStep.value = true
                    isShowButton.value = false
                    savePoliceCertificateId(response.policeCertificate?.id)
                } else {
                    stepFiveErrorsMessage.value = getErrorMessage(it.split("\""))
                    isShowButton.value = true
                }
                isLoading.value = false
            })
        }
    }

    private fun postStepFour() {
        showLoading()
        skckStepFourModel.value?.apply {

            policeCertificateId = BaseApplication.sessionManager?.policeCertificateId
            BaseApplication.sessionManager?.skckStepFour = GSONManager.toJson(this)

            PostRequest<SkckStepFourModel>(Urls.POST_SKCK_STEP_FOUR).post(this, {
                val response = GSONManager.fromJson(it, SkckPostResponse::class.java)
                stepFourErrorsMessage.value = ""
                if (response.isSuccess()) {
                    isNextStep.value = true
                    savePoliceCertificateId(response.policeCertificate?.id)
                } else {
                    stepFourErrorsMessage.value = getErrorMessage(it.split("\""))
                }
                hideLoading()
            })
        }
    }

    private fun postStepThree() {
        showLoading()
        skckStepThreeModel.value?.apply {

            policeCertificateId = BaseApplication.sessionManager?.policeCertificateId
            BaseApplication.sessionManager?.skckStepThree = GSONManager.toJson(this)

            PostRequest<SkckStepThreeModel>(Urls.POST_SKCK_STEP_THREE).post(this, {
                val response = GSONManager.fromJson(it, SkckPostResponse::class.java)
                stepThreeErrorsMessage.value = ""
                if (response.isSuccess()) {
                    isNextStep.value = true
                    savePoliceCertificateId(response.policeCertificate?.id)
                } else {
                    stepThreeErrorsMessage.value = getErrorMessage(it.split("\""))
                }
                hideLoading()
            })
        }
    }

    private fun savePoliceCertificateId(policeCertificate: String?) {
        policeCertificate.takeIf { !it.isNullOrEmpty() }?.apply {
            BaseApplication.sessionManager?.policeCertificateId = this
        }
    }

    private fun postStepTwo() {
        showLoading()
        skckStepTwoModel.value?.apply {

            policeCertificateId = BaseApplication.sessionManager?.policeCertificateId
            BaseApplication.sessionManager?.skckStepTwo = GSONManager.toJson(this)

            PostRequest<SkckStepTwoModel>(Urls.POST_SKCK_STEP_TWO).post(this, {
                val response = GSONManager.fromJson(it, SkckPostResponse::class.java)
                stepTwoErrorsMessage.value = ""
                if (response.isSuccess()) {
                    isNextStep.value = true
                    savePoliceCertificateId(response.policeCertificate?.id)
                } else {
                    stepTwoErrorsMessage.value = getErrorMessage(it.split("\""))
                }
                hideLoading()
            })
        }
    }

    private fun postStepOne() {
        showLoading()
        skckStepOneModel.value?.apply {
            PostRequest<SkckStepOneModel>(Urls.POST_SKCK_STEP_ONE).post(this, {
                val response = GSONManager.fromJson(it, SkckPostResponse::class.java)
                stepOneErrorsMessage.value = ""
                if (response.isSuccess()) {
                    isNextStep.value = true
                    savePoliceCertificateId(response.policeCertificate?.id)
                } else {
                    stepOneErrorsMessage.value = getErrorMessage(it.split("\""))
                }
                hideLoading()
            })
        }
    }

    private fun showLoading() {
        isLoading.value = true
        isShowButton.value = false
    }

    private fun hideLoading() {
        isLoading.value = false
        isShowButton.value = true
    }

    private fun getErrorMessage(messages : List<String>) : String {
        var message = ""
        messages.forEach { error ->
            if (!error.equals("Invalid Data", ignoreCase = true) && error.contains(" ")) {
                message += "*$error\n\n"
            }
        }
        return message
    }

    companion object {
        const val LAST_TWO_FORM_FRAGMENT_POSITION = 2
    }
}