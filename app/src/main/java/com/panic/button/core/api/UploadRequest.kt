package com.panic.button.core.api

import com.panic.button.core.base.BaseApplication
import com.panic.button.core.model.Urls
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class UploadRequest(private val url: String) {

    private val request = RestClient.createStringService(UrlApi::class.java, Urls.BASE_URL)
    private var headers = HashMap<String, String>()
    private var queries = HashMap<String, String>()

    init {
        BaseApplication.sessionManager?.accessToken.takeIf { !it.isNullOrEmpty() }?.apply {
            headers["Authorization"] = "Bearer $this"
        }
        headers["Accept"] = "application/json"
    }

    fun upload(file: File, section: String, response: (String) -> Unit, isVideo: Boolean = false, throwable: ((Throwable) -> Unit?)? = null, disposable: ((Disposable) -> Unit)? = null) {
        val typeFile = file.absolutePath.split(".").last()
        val requestBodyFile = RequestBody.create(typeFile.toMediaTypeOrNull(), file)

        val fileType = if (isVideo) {
            "video"
        } else {
            "image"
        }

        val bodyFile = MultipartBody.Part.createFormData(fileType, file.name, requestBodyFile)

        val bodySection = MultipartBody.Part.createFormData("section", section)

        val params = ArrayList<MultipartBody.Part>()
        params.add(bodyFile)
        params.add(bodySection)

        uploading(params, response, throwable, disposable)
    }

    fun uploadFilePanicButton(file: File, id: String, section: String, response: (String) -> Unit, throwable: ((Throwable) -> Unit?)? = null, disposable: ((Disposable) -> Unit)? = null) {
        val typeFile = file.absolutePath.split(".").last()
        val requestBodyFile = RequestBody.create(typeFile.toMediaTypeOrNull(), file)

        val bodyFile = MultipartBody.Part.createFormData("file", file.name, requestBodyFile)
        val bodyId = MultipartBody.Part.createFormData("id", id)
        val bodySection = MultipartBody.Part.createFormData("section", section)

        val params = ArrayList<MultipartBody.Part>()
        params.add(bodyFile)
        params.add(bodyId)
        params.add(bodySection)

        uploading(params, response, throwable, disposable)
    }

    private fun uploading(params: ArrayList<MultipartBody.Part>, response: (String) -> Unit, throwable: ((Throwable) -> Unit?)?, disposable: ((Disposable) -> Unit)?) {
        request.uploadMedia(Urls.BASE_URL + url, headers, queries, params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .retry(3)
            .subscribe(object : Observer<String> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {
                    disposable?.run { this(d) }
                }

                override fun onNext(result: String) {
                    try {
                        response(result)
                    } catch (e: Throwable) {
                        log("catch Throwable $url $e")
                        showError(e)
                        throwable?.run { this(e) }
                    } catch (e: Exception) {
                        log("catch error $url $e")
                        throwable?.run { this(e) }
                        showError(Throwable(""))
                    }
                }

                override fun onError(e: Throwable) {
                    log("onError $url $e")
                    throwable?.run { this(e) }
                    showError(e)
                }
            })
    }

    private fun showError(throwable: Throwable) {
        when (throwable) {
            is IOException -> {
                showToast("Network Error")
            }
            is HttpException -> {
                val code = throwable.code()
                val errorResponse = throwable.message()
                showToast("Error $code $errorResponse")
            }
            else -> {
                showToast("Unknown Error")
            }
        }
    }

    private fun showToast(message: String) {
        BaseApplication.showToast(message)
    }

    companion object {
        const val AVATAR_SECTION = "avatar"
        const val CITIZEN_CARD_SECTION = "citizen_card_image"
        const val CITIZEN_CARD_SELFIE_SECTION = "citizen_card_selfie"
        const val PATROL_VIDEO = "patrol-video"
        const val INCIDENT_VIDEO = "incident-video"
    }
}