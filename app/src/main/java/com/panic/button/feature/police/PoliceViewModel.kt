package com.panic.button.feature.police

import androidx.lifecycle.MutableLiveData
import com.panic.button.core.api.GSONManager
import com.panic.button.core.api.GetRequest
import com.panic.button.core.api.PostRequest
import com.panic.button.core.api.response.*
import com.panic.button.core.base.BaseApplication
import com.panic.button.core.base.BaseViewModel
import com.panic.button.core.base.mutableLiveDataOf
import com.panic.button.core.model.GeneralModel
import com.panic.button.core.model.Urls
import com.panic.button.core.model.UserModel

class PoliceViewModel : BaseViewModel() {

    val nik = MutableLiveData<String>()
    val address = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val status = MutableLiveData<String>()

    val province = MutableLiveData<String>()
    val city = MutableLiveData<String>()
    val subDistrictId = MutableLiveData<String>()
    val villageId = MutableLiveData<String>()
    val hamlet_id = MutableLiveData<String>()

    val isLoading = MutableLiveData<Boolean>()
    val isSuccessLogout = MutableLiveData<Boolean>()
    val checkCovidResponse = MutableLiveData<CheckCovidResponse>()

    val provinceResponse = MutableLiveData<ArrayList<GeneralModel>>()
    val cityResponse = MutableLiveData<ArrayList<GeneralModel>>()
    val subDistrictResponse = MutableLiveData<ArrayList<GeneralModel>>()
    val villageResponse = MutableLiveData<ArrayList<GeneralModel>>()
    val hamletResponse = MutableLiveData<ArrayList<GeneralModel>>()

    val isSuccessCovidRegister = MutableLiveData<Boolean>()
    var errorsMessage = mutableLiveDataOf<String>()

    fun postLogout() {
        BaseApplication.sessionManager?.loginParams.takeIf { !it.isNullOrEmpty() }?.apply {
            isLoading.value = true
            val userModel = GSONManager.fromJson(this, UserModel::class.java)
            PostRequest<UserModel>(Urls.POST_OFFICER_LOGOUT).post(
                params = userModel,
                response = {
                    isSuccessLogout.value = true
                })
        }
    }

    fun postCovidRegister() {
        if (nik.value.isNullOrBlank()) {
            showToast("Nomor KTP tidak boleh kosong")
            return
        }
        if (name.value.isNullOrBlank()) {
            showToast("Nama lengkap tidak boleh kosong")
            return
        }
        if (address.value.isNullOrBlank()) {
            showToast("Alamat lengkap tidak boleh kosong")
            return
        }
        if (phone.value.isNullOrBlank()) {
            showToast("Nomor HP tidak boleh kosong")
            return
        }
        if (hamlet_id.value.isNullOrBlank()) {
            showToast("RW tidak boleh kosong")
            return
        }
        isLoading.value = true
        val userModel = UserModel(name = name.value, address = address.value, phone = phone.value, citizen_card_id = nik.value, hamlet_id = hamlet_id.value)
        PostRequest<UserModel>(Urls.POST_OFFICER_COVID_REGISTER).post(
            params = userModel,
            response = {
                isLoading.value = false
                val response = GSONManager.fromJson(it, BaseResponse::class.java)
                isSuccessCovidRegister.value = response.status
                errorsMessage.value = getErrorMessage(it.split("\""))
            })
    }

    fun getCheckNik() {
        if (nik.value.isNullOrBlank()) {
            showToast("Nomor KTP tidak boleh kosong")
            return
        }

        isLoading.value = true
        val request = GetRequest(Urls.GET_OFFICER_COVID_CHECK)
        request.queries["citizen_card_id"] = nik.value ?: ""
        request.get({
            isLoading.value = false
            val response = GSONManager.fromJson(it, CheckCovidResponse::class.java)
            checkCovidResponse.value = response
            name.value = response.data?.name
            address.value = response.data?.address
            phone.value = response.data?.phone
            status.value = response.data?.status
        })
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

    fun getHamlet(hamletId: String?) {
        GetRequest(Urls.GET_HAMLETS + hamletId).get({
            hamletResponse.value = GSONManager.fromJson(it, VillageResponse::class.java).villages
        })
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
}