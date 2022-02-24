package com.panic.button.feature.login

import androidx.lifecycle.MutableLiveData
import com.panic.button.core.api.GSONManager
import com.panic.button.core.api.PostRequest
import com.panic.button.core.api.response.LoginResponse
import com.panic.button.core.base.BaseApplication
import com.panic.button.core.base.BaseViewModel
import com.panic.button.core.base.ChCrypto
import com.panic.button.core.base.isValidEmail
import com.panic.button.core.model.Urls
import com.panic.button.core.model.UserModel
import kotlin.random.Random

class LoginViewModel : BaseViewModel() {
    val isSuccess = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    private var DEVICE_MODEL = "${android.os.Build.MODEL} ${android.os.Build.MANUFACTURER} ${android.os.Build.BRAND} ${android.os.Build.FINGERPRINT}"

    fun onLogin(isOfficer : Boolean?) {
        if (email.value.isNullOrEmpty()) {
            showToast("Email/NIK tidak boleh kosong")
            return
        }
        if (password.value.isNullOrEmpty()) {
            showToast("Password tidak boleh kosong")
            return
        }

        isLoading.value = true
        var fcmToken = BaseApplication.sessionManager?.fcmToken
        if (fcmToken.isNullOrEmpty()) {
            fcmToken = "${Random.nextLong()}"
        }
        val userModel = UserModel(email = email.value, password = ChCrypto.aesEncrypt("${password.value}", Urls.underStood),
            fcmToken = fcmToken,
            imeiDevice = DEVICE_MODEL)

        BaseApplication.sessionManager?.loginParams = GSONManager.toJson(userModel)

        val url : String
        if (isOfficer == true) {
            url = Urls.LOGIN_OFICER
        } else {
            url = Urls.LOGIN_CITIZEN
        }

        PostRequest<UserModel>(url).post(
                params = userModel,
                response = {
                    onSuccess(it, isOfficer)
                    isLoading.value = false
                })
    }

    private fun onSuccess(response: String, isOfficer : Boolean?) {
        val loginResponse = GSONManager.fromJson(response, LoginResponse::class.java)
        if (loginResponse.isSuccess == true) {
            BaseApplication.sessionManager?.run {
                userId = loginResponse.loginModel?.userModel?.id
                accessToken = loginResponse.loginModel?.authModel?.accessToken
                isPolice = isOfficer == true
                email = loginResponse.loginModel?.userModel?.email
                fullName = loginResponse.loginModel?.userModel?.name
                role = loginResponse.loginModel?.userModel?.roles?.firstOrNull()?.roleSlug
            }
            isSuccess.value = true
        } else {
            loginResponse.errors?.apply {
                email?.getOrNull(0).takeIf { !it.isNullOrEmpty() }?.apply {
                    showToast(this)
                }
                password?.getOrNull(0).takeIf { !it.isNullOrEmpty() }?.apply {
                    showToast(this)
                }
            } ?: kotlin.run {
                showToast(loginResponse.message ?: "Gagal login!")
            }
        }
    }
}