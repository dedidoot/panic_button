package com.panic.button.core.api

import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import retrofit2.http.POST
import retrofit2.http.Multipart

interface UrlApi {

    @GET
    fun getRequestApi(@Url url: String,
                      @HeaderMap headers: Map<String, String>,
                      @QueryMap (encoded = true) queries: Map<String, String>): Observable<String>

    @POST
    fun postRequestApi(@Url url: String,
                       @HeaderMap headers: Map<String, String>,
                       @QueryMap queries: Map<String, String>,
                       @Body params: RequestBody?): Observable<String>

    @Multipart
    @POST
    fun uploadMedia(@Url url: String,
                    @HeaderMap headers: Map<String, String>,
                    @QueryMap queries: Map<String, String>,
                    @Part image: ArrayList<MultipartBody.Part>): Observable<String>

    @POST
    suspend fun postRequestSuspendApi(@Url url: String,
                       @HeaderMap headers: Map<String, String>,
                       @QueryMap queries: Map<String, String>,
                       @Body params: RequestBody?): String
}