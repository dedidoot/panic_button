package com.panic.button.core.api

import com.panic.button.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import java.util.concurrent.TimeUnit

object RestClient {

    fun <S> createStringService(serviceClass: Class<S>, baseUrl: String): S {

        val logging = HttpLoggingInterceptor()

        logging.level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        httpClient.connectTimeout(5, TimeUnit.MINUTES)
        httpClient.readTimeout(5, TimeUnit.MINUTES)
        httpClient.writeTimeout(5, TimeUnit.MINUTES)
        val client = httpClient.build()

        val retrofit = Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(StringConverterFactory())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
            .client(client)
            .build()
        return retrofit.create(serviceClass)
    }

}