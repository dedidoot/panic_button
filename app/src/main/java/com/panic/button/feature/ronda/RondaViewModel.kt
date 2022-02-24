package com.panic.button.feature.ronda

import androidx.lifecycle.MutableLiveData
import com.panic.button.core.api.*
import com.panic.button.core.api.response.MediaResponse
import com.panic.button.core.api.response.PatrolResponse
import com.panic.button.core.base.*
import com.panic.button.core.model.PatrolModel
import com.panic.button.core.model.Urls
import java.io.File

class RondaViewModel : BaseViewModel() {
    val isSuccess = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    val report = MutableLiveData<String>()
    val videoId = MutableLiveData<ArrayList<String>>()
    val videoName = MutableLiveData<ArrayList<String>>()

    val isPermitted = MutableLiveData<Boolean>()
    val messages = MutableLiveData<String>()

    init {
        videoId.value = arrayListOf()
        videoName.value = arrayListOf()
    }

    fun getCitizenPatrol() {
        isLoading.value = true
        GetRequest(Urls.GET_CITIZEN_PATROL).get({
            val response = GSONManager.fromJson(it, PatrolResponse::class.java)
            messages.value = response.message
            isPermitted.value = response.isSuccess
            isLoading.value = false
        })
    }

    fun uploadVideo(file: File) {
        isLoading.value = true
        UploadRequest(Urls.UPLOAD_VIDEO).upload(file, UploadRequest.PATROL_VIDEO, {
            val response = GSONManager.fromJson(it, MediaResponse::class.java)

            if (response.isSuccess()) {
                videoId.value?.add(response.mediaModel?.id ?: "")
                videoName.value?.add(file.name)
                videoName.value = videoName.value
            } else {
                showToast(response.message ?: "Gagal upload video")
            }
            isLoading.value = false
        }, isVideo = true)
    }

    fun onSave() {

        if (videoId.value.isNullOrEmpty()) {
            showToast("Mohon upload video ronda Anda")
            return
        }
        if (report.value.isNullOrEmpty()) {
            showToast("Mohon isi laporan ronda Anda")
            return
        }
        if (report.value?.length ?: 0 < 30) {
            showToast("Laporan harus lebih dari 30 karakter")
            return
        }

        isLoading.value = true
        val params = PatrolModel()
        params.report = report.value
        params.countVideo = videoId.value?.size ?: 0

        videoId.value?.forEachIndexed{index, videoId ->
            if (index == 0) {
                params.video0Id = videoId
            } else if (index == 1) {
                params.video1Id = videoId
            } else if (index == 2) {
                params.video2Id = videoId
            } else if (index == 3) {
                params.video3Id = videoId
            }
        }

        PostRequest<PatrolModel>(Urls.POST_PATROL_REPORT).post(params, {
            val response = GSONManager.fromJson(it, MediaResponse::class.java)
            if (response.isSuccess()) {
                isSuccess.value = true
            } else {
                showToast(response.message ?: "Gagal upload video")
            }
            isLoading.value = false
        })
    }

    fun getCountNameFiles() : Int {
        return videoName.value?.size ?: 0
    }

}