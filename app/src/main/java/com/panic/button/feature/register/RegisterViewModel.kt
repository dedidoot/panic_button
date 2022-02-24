package com.panic.button.feature.register

import androidx.lifecycle.MutableLiveData
import com.panic.button.core.api.GSONManager
import com.panic.button.core.base.*
import com.panic.button.core.model.RegisterModel
import com.panic.button.core.model.Urls

class RegisterViewModel : BaseViewModel() {
    val isSuccess = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()

    val registerModel = MutableLiveData<RegisterModel>()

    init {
        registerModel.value = RegisterModel()
        BaseApplication.sessionManager?.registerCitizen.takeIf { !it.isNullOrEmpty() }?.apply {
            val tempRegisterModel = GSONManager.fromJson(this, RegisterModel::class.java)
            tempRegisterModel.password = ChCrypto.aesDecrypt(tempRegisterModel.password ?: "", Urls.underStood)
            tempRegisterModel.passwordConfirmation = ChCrypto.aesDecrypt(tempRegisterModel.passwordConfirmation ?: "", Urls.underStood)

            registerModel.value = tempRegisterModel
        }
    }

    fun onSave() {
        if (registerModel.value?.email.isNullOrEmpty() || registerModel.value?.email?.isValidEmail() == false) {
            showToast("Email tidak benar")
            return
        }
        if (!isDigitAndUpperLowerCase(registerModel.value?.password ?: "")) {
            showToast("Password harus mengandung huruf besar, kecil dan angka")
            return
        }
        if (registerModel.value?.password?.equals(registerModel.value?.passwordConfirmation, ignoreCase = true) == true) {

            registerModel.value?.password = ChCrypto.aesEncrypt(registerModel.value?.password ?: "", Urls.underStood)
            registerModel.value?.passwordConfirmation = ChCrypto.aesEncrypt(registerModel.value?.passwordConfirmation ?: "", Urls.underStood)

            BaseApplication.sessionManager?.registerCitizen = GSONManager.toJson(registerModel.value)
            isSuccess.value = true
        } else {
            showToast("Password tidak cocok")
        }
    }
}