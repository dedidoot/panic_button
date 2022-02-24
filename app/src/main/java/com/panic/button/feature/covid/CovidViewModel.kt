package com.panic.button.feature.covid

import androidx.lifecycle.MutableLiveData
import com.panic.button.core.api.*
import com.panic.button.core.api.response.HospitalResponse
import com.panic.button.core.api.response.SubDistrictResponse
import com.panic.button.core.api.response.VillageResponse
import com.panic.button.core.base.BaseViewModel
import com.panic.button.core.model.GeneralModel
import com.panic.button.core.model.HospitalModel
import com.panic.button.core.model.Urls
import java.util.*

class CovidViewModel : BaseViewModel() {
    val isSuccess = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()

    val subDistrictResponse = MutableLiveData<ArrayList<GeneralModel>>()
    val villageResponse = MutableLiveData<ArrayList<GeneralModel>>()
    val rtRwResponse = MutableLiveData<ArrayList<GeneralModel>>()
    val hospitalResponse = MutableLiveData<ArrayList<HospitalModel>>()

    fun getSubDistrictJaktim() {
        GetRequest(Urls.GET_SUB_DISTRICT + "3172").get({
            subDistrictResponse.value =
                GSONManager.fromJson(it, SubDistrictResponse::class.java).subDistricts
        })
    }

    fun getVillage(subDistrictId: String?) {
        GetRequest(Urls.GET_VILAGE + subDistrictId).get({
            villageResponse.value = GSONManager.fromJson(it, VillageResponse::class.java).villages
        })
    }

    var currentVillageId: String? = ""

    fun getRtRw(villageId: String?) {
        currentVillageId = villageId
        GetRequest(Urls.GET_HAMLETS + villageId).get({
            rtRwResponse.value = GSONManager.fromJson(it, VillageResponse::class.java).villages
        })
    }

    fun getHospitals() {
        GetRequest(Urls.GENERAL_HOTLINES).get({
            val response = GSONManager.fromJson(it, HospitalResponse::class.java)
            hospitalResponse.value = response.data
        })
    }
}