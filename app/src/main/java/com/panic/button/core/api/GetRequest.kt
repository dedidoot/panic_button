package com.panic.button.core.api

import com.panic.button.core.api.response.BaseResponse
import com.panic.button.core.base.BaseApplication
import com.panic.button.core.model.Urls
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

class GetRequest(private val url: String) {

    private val request = RestClient.createStringService(UrlApi::class.java, Urls.BASE_URL)
    private var headers = HashMap<String, String>()
    var queries = HashMap<String, String>()

    init {
        BaseApplication.sessionManager?.accessToken.takeIf { !it.isNullOrEmpty() }?.apply {
            headers["Authorization"] = "Bearer $this"
        }
        headers["Accept"] = "application/json"
    }

    fun get(response: (String) -> Unit, throwable: ((Throwable) -> Unit?)? = null, disposable: ((Disposable) -> Unit)? = null) {
        request.getRequestApi(Urls.BASE_URL + url, headers, queries)
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
                            val baseResponse  = GSONManager.fromJson(result, BaseResponse::class.java)
                            if (!baseResponse.isSuccess() && baseResponse.message?.contains("Unauthenticated", ignoreCase = false) == true) {
                                BaseApplication.baseApplication?.showTokenExpiredDialog()
                            } else {
                                response(result)
                            }
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
}