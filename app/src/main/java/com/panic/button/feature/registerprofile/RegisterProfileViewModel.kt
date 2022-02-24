package com.panic.button.feature.registerprofile

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.panic.button.core.api.GSONManager
import com.panic.button.core.api.GetRequest
import com.panic.button.core.api.PostRequest
import com.panic.button.core.api.UploadRequest
import com.panic.button.core.api.response.*
import com.panic.button.core.base.*
import com.panic.button.core.model.GeneralModel
import com.panic.button.core.model.RegisterModel
import com.panic.button.core.model.Urls
import java.io.File

class RegisterProfileViewModel : BaseViewModel() {
    val isSuccess = MutableLiveData<Boolean>()
    val isEdited = MutableLiveData<Boolean>()
    val urlUploadSuccess = MutableLiveData<String>()
    val isFromEditProfile = MutableLiveData<Boolean>()

    val registerModel = MutableLiveData<RegisterModel>()
    val provinceResponse = MutableLiveData<ArrayList<GeneralModel>>()
    val cityResponse = MutableLiveData<ArrayList<GeneralModel>>()
    val subDistrictResponse = MutableLiveData<ArrayList<GeneralModel>>()
    val villageResponse = MutableLiveData<ArrayList<GeneralModel>>()
    val hamletResponse = MutableLiveData<ArrayList<GeneralModel>>()
    val profileResponse = MutableLiveData<ProfileResponse>()
    var errorsMessage = mutableLiveDataOf<String>()

    init {
        registerModel.value = RegisterModel()
        BaseApplication.sessionManager?.registerCitizen.takeIf { !it.isNullOrEmpty() }?.apply {
            val tempRegisterModel = GSONManager.fromJson(this, RegisterModel::class.java)
            tempRegisterModel.citizenCardId.takeIf { !it.isNullOrEmpty() }?.apply {
                tempRegisterModel.citizenCardId = ChCrypto.aesDecrypt(this, Urls.underStood)
            }

            registerModel.value = tempRegisterModel
        }
    }

    fun processIntent(intent: Intent?) {
        isFromEditProfile.value = intent?.getBooleanExtra(EXTRA_IS_FROM_EDIT_PROFILE, false)
    }

    fun onSave(isEdit : Boolean) {
        if (registerModel.value?.avatarId.isNullOrEmpty()) {
            showToast("Foto tidak boleh kosong")
            return
        }
        if (registerModel.value?.name.isNullOrEmpty()) {
            showToast("Nama tidak boleh kosong")
            return
        }
        if (registerModel.value?.birthPlace.isNullOrEmpty()) {
            showToast("Tempat lahir tidak boleh kosong")
            return
        }
        if (registerModel.value?.birthday.isNullOrEmpty()) {
            showToast("Tanggal lahir tidak boleh kosong")
            return
        }
        if (registerModel.value?.gender.isNullOrEmpty()) {
            showToast("Jenis kelamin tidak boleh kosong")
            return
        }
        if (registerModel.value?.citizenCardId.isNullOrEmpty()) {
            showToast("Nomor ktp tidak boleh kosong")
            return
        }
        if (registerModel.value?.phone.isNullOrEmpty()) {
            showToast("Nomor hp tidak boleh kosong")
            return
        }
        if (registerModel.value?.phone?.isIndonesianMobilePhoneNumber() == false) {
            showToast("Nomor hp tidak benar")
            return
        }
        if (registerModel.value?.address.isNullOrEmpty()) {
            showToast("Alamat tidak boleh kosong")
            return
        }
        if (registerModel.value?.address?.length ?: 0 <= 10) {
            showToast("Alamat harus lebih dari 10 karakter")
            return
        }
        if (registerModel.value?.houseNumber.isNullOrEmpty()) {
            showToast("Nomor rumah tidak boleh kosong")
            return
        }
        if (registerModel.value?.rt.isNullOrEmpty()) {
            showToast("RT tidak boleh kosong")
            return
        }
        if (registerModel.value?.province.isNullOrEmpty()) {
            showToast("Provinsi tidak boleh kosong")
            return
        }
        if (registerModel.value?.city.isNullOrEmpty()) {
            showToast("Kota tidak boleh kosong")
            return
        }
        if (registerModel.value?.subDistrictId.isNullOrEmpty()) {
            showToast("Kecamatan tidak boleh kosong")
            return
        }
        if (registerModel.value?.villageId.isNullOrEmpty()) {
            showToast("Kelurahan tidak boleh kosong")
            return
        }
        if (registerModel.value?.postalCode.isNullOrEmpty()) {
            showToast("Kode pos tidak boleh kosong")
            return
        }
        if (registerModel.value?.postalCode?.length ?: 0 <= 4) {
            showToast("Kode pos harus 5 angka")
            return
        }
        if (registerModel.value?.emergencyContactFullName.isNullOrEmpty()) {
            showToast("Nama kontak darurat tidak boleh kosong")
            return
        }
        if (registerModel.value?.emergencyContactRelationship.isNullOrEmpty()) {
            showToast("Hubungan kontak darurat tidak boleh kosong")
            return
        }
        if (registerModel.value?.emergencyContactPhoneNumber.isNullOrEmpty()) {
            showToast("Nomor hp kontak darurat tidak boleh kosong")
            return
        }
        if (registerModel.value?.emergencyContactPhoneNumber?.isIndonesianMobilePhoneNumber() == false) {
            showToast("Nomor hp kontak darurat tidak benar")
            return
        }

        registerModel.value?.citizenCardId = ChCrypto.aesEncrypt(registerModel.value?.citizenCardId ?: "", Urls.underStood)

        if (isEdit) {
            onEdit()
        } else {
            BaseApplication.sessionManager?.registerCitizen = GSONManager.toJson(registerModel.value)
            isSuccess.value = true
        }
    }

    private fun onEdit() {
        registerModel.value?.apply {
            PostRequest<RegisterModel>(Urls.CITIZEN_PROFILE).post(this, {
                val response = GSONManager.fromJson(it, BaseResponse::class.java)
                if (response.isSuccess()) {
                    isEdited.value = true
                } else {
                    errorsMessage.value = getErrorMessage(it.split("\""))
                    showToast("Gagal kirim data")
                }
            })
        }
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

    fun refreshRegisterModel() {
        registerModel.value = registerModel.value
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

    fun getProfile() {
        GetRequest(Urls.CITIZEN_PROFILE).get({
            profileResponse.value = GSONManager.fromJson(it, ProfileResponse::class.java)
        })
    }

    fun uploadImage(file: File) {
        UploadRequest(Urls.UPLOAD_MEDIA).upload(file, UploadRequest.AVATAR_SECTION, {
            val response = GSONManager.fromJson(it, MediaResponse::class.java)
            urlUploadSuccess.value = replaceUrlJaktim(response.mediaModel?.url)
            if (response.isSuccess()) {
                registerModel.value?.avatarId = response.mediaModel?.id
                registerModel.value?.avatarUrlSession = urlUploadSuccess.value
            } else {
                showToast(response.message ?: "Gagal upload gambar")
            }
        })
    }

    companion object {
        const val EXTRA_IS_FROM_EDIT_PROFILE = "is_from_edit_profile"
    }
}