package com.panic.button.core.api

import com.panic.button.core.base.BaseApplication
import com.panic.button.core.model.Urls
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

class PrayerRequest(val url : String = Urls.URL_PRAYER) {

    private val request = RestClient.createStringService(UrlApi::class.java, Urls.URL_PRAYER)

    fun get(response: (String) -> Unit, throwable: ((Throwable) -> Unit?)? = null, disposable: ((Disposable) -> Unit)? = null) {
        val queries = HashMap<String, String>()
        queries["address"] = "Jakarta Timur"
        queries["method"] = "11"
        queries["tune"] = "2,2,2,2,2,2,2,2,2"
        request.getRequestApi(Urls.PRAYER_TIME, HashMap(), queries)
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
                        showError(e )
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

    companion object{
        const val tune = ""
    }
}