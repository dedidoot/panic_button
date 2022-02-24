package com.panic.button.feature.lostletter

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.panic.button.core.api.*
import com.panic.button.core.api.response.*
import com.panic.button.core.base.*
import com.panic.button.core.model.Urls
import java.io.File

class LostLetterViewModel : BaseViewModel() {

    var fragments: ArrayList<Fragment> = arrayListOf(TypeFragment(), FieldFragment())

    val lostLetterTypeResponse = mutableLiveDataOf<LostLetterTypeResponse>()
    val lostLetterFieldResponse = mutableLiveDataOf<LostLetterFieldResponse>()
    val isLoading = mutableLiveDataOf<Boolean>()
    val isNextPage = mutableLiveDataOf<Boolean>()
    val isSuccess = mutableLiveDataOf<Boolean>()
    val fileId = MutableLiveData<String>()
    var typesId = ArrayList<String>()
    var additionalInfo = mutableLiveDataOf<String>()
    var lostDate = mutableLiveDataOf<String>()
    var lostTime = mutableLiveDataOf<String>()
    var location = mutableLiveDataOf<String>()
    var errorsMessage = mutableLiveDataOf<String>()

    fun getLostLetterType() {
        isLoading.value = true
        val request = GetRequest(Urls.GET_LOST_LETTER_TYPE)
        request.get({
            lostLetterTypeResponse.value = GSONManager.fromJson(it, LostLetterTypeResponse::class.java)
            isLoading.value = false
        })
    }

    fun getLostLetterField(typesId : ArrayList<String>) {
        this.typesId = typesId
        isLoading.value = true
        val request = GetRequest(Urls.GET_LOST_LETTER_FIELD)
        typesId.forEachIndexed { index, it ->
            request.queries["id[$index]"] = it
        }
        request.get({
            lostLetterFieldResponse.value = GSONManager.fromJson(it, LostLetterFieldResponse::class.java)
            isLoading.value = false
        })
    }

    fun uploadImage(file: File?, slug : String) {
        file?.let {
            UploadRequest(Urls.UPLOAD_MEDIA).upload(it, slug, {
                val response = GSONManager.fromJson(it, MediaResponse::class.java)
                if (response.isSuccess()) {
                    fileId.value = response.mediaModel?.id
                    showToast(response.message ?: "Berhasil upload!")
                } else {
                    showToast(response.message ?: "Gagal upload gambar!")
                }

                isLoading.value = false
            })
        } ?: kotlin.run {
            showToast("File corrupt, please try again!")
            isLoading.value = false
        }
    }

    fun nextPage() {
        isNextPage.value = true
    }

    fun sendLostLetter(params: HashMap<String, Any>) {
        location.value?.apply {
            params["location"] =  this
        }
        additionalInfo.value?.apply {
            params["additional_information"] =  this
        }

        val lostTime = "${lostDate.value} ${lostTime.value}"
        params["lost_time"] = lostTime

        params["police_lost_letter_type_id"] = typesId

        isLoading.value = true
        PostRequest<HashMap<String, Any>>(Urls.CREATE_LOST_LETTER).post(params, {
            val response = GSONManager.fromJson(it, MediaResponse::class.java)
            if (response.isSuccess()) {
                isSuccess.value = true
            } else {
                showToast("Gagal kirim data")
                errorsMessage.value = getErrorMessage(it.split("\""))
            }
            isLoading.value = false
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