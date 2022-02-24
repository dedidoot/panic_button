package com.panic.button.feature.ktpupload

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.panic.button.core.api.GSONManager
import com.panic.button.core.api.PostRequest
import com.panic.button.core.api.UploadRequest
import com.panic.button.core.api.response.BaseResponse
import com.panic.button.core.api.response.MediaResponse
import com.panic.button.core.base.BaseApplication
import com.panic.button.core.base.BaseViewModel
import com.panic.button.core.base.mutableLiveDataOf
import com.panic.button.core.model.RegisterModel
import com.panic.button.core.model.Urls
import java.io.File
import java.util.*

class IdentityCardViewModel : BaseViewModel() {
    val isSuccess = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    val isLoadingUpload = MutableLiveData<Boolean>()
    val isFromEditProfile = MutableLiveData<Boolean>()

    private val registerModel = MutableLiveData<RegisterModel>()
    val uploadCardIdSuccess = MutableLiveData<String>()
    val uploadCardSelfieIdSuccess = MutableLiveData<String>()
    var isUploadKtp = false

    var errorsMessage = mutableLiveDataOf<String>()

    init {
        registerModel.value = RegisterModel()
        BaseApplication.sessionManager?.registerCitizen.takeIf { !it.isNullOrEmpty() }?.apply {
            registerModel.value = GSONManager.fromJson(this, RegisterModel::class.java)
            uploadCardIdSuccess.value = registerModel.value?.citizenCardImageId
            uploadCardSelfieIdSuccess.value = registerModel.value?.citizenCardSelfieId
        }
    }

    fun processIntent(intent: Intent?) {
        isFromEditProfile.value = intent?.getBooleanExtra(EXTRA_IS_FROM_EDIT_PROFILE, false)
    }

    fun uploadCardSelfie(file: File) {
        isLoadingUpload.value = true
        UploadRequest(Urls.UPLOAD_MEDIA).upload(file, UploadRequest.CITIZEN_CARD_SELFIE_SECTION, {
            val response = GSONManager.fromJson(it, MediaResponse::class.java)
            if (response.isSuccess == true) {
                uploadCardSelfieIdSuccess.value = response.mediaModel?.id
                registerModel.value?.citizenCardSelfieId = response.mediaModel?.id
                BaseApplication.sessionManager?.registerCitizen = GSONManager.toJson(registerModel.value)
            } else {
                showToast(response.message ?: "Gagal upload")
            }
            isLoadingUpload.value = false
        })
    }

    fun uploadCard(file: File) {
        UploadRequest(Urls.UPLOAD_MEDIA).upload(file, UploadRequest.CITIZEN_CARD_SECTION, {
            val response = GSONManager.fromJson(it, MediaResponse::class.java)
            if (response.isSuccess == true) {
                uploadCardIdSuccess.value = response.mediaModel?.id
                registerModel.value?.citizenCardImageId = response.mediaModel?.id
                BaseApplication.sessionManager?.registerCitizen = GSONManager.toJson(registerModel.value)
            } else {
                showToast(response.message ?: "Gagal upload")
            }
            isLoadingUpload.value = false
        })
    }

    fun onCitizenRegister() {
        if (uploadCardIdSuccess.value.isNullOrEmpty()) {
            showToast("Mohon upload foto ktp Anda")
            return
        }
        if (uploadCardSelfieIdSuccess.value.isNullOrEmpty()) {
            showToast("Mohon upload foto selfie dengan ktp Anda")
            return
        }

        registerModel.value?.let {
            isLoading.value = true
            it.agreement = 1
            it.latitude = BaseApplication.sessionManager?.latitude?.toDoubleOrNull() ?: 0.0
            it.longitude = BaseApplication.sessionManager?.longitude?.toDoubleOrNull() ?: 0.0
            it.gender = it.gender?.toLowerCase(Locale.getDefault())
            it.emergencyContactRelationship = it.emergencyContactRelationship?.toLowerCase(Locale.getDefault())
            PostRequest<RegisterModel>(Urls.POST_CITIZEN_REGISTER).post(it, {
                isLoading.value = true
                val response = GSONManager.fromJson(it, BaseResponse::class.java)
                if (response.isSuccess()) {
                    isSuccess.value = true
                } else {
                    isLoading.value = false
                    errorsMessage.value = getErrorMessage(it.split("\""))
                    showToast(response.message
                            ?: "Gagal membuat akun, mohon hubungi customer services kami!")
                }
            })
        } ?: kotlin.run {
            isLoading.value = false
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

    companion object {
        const val EXTRA_IS_FROM_EDIT_PROFILE = "is_from_edit_profile"
    }
}