package com.panic.button.core.base.camera

import com.panic.button.core.base.BaseApplication
import com.panic.button.core.base.RemoteConfigHelper
import com.panic.button.core.base.RemoteConfigKey
import com.panic.button.core.base.showAlert

import android.content.Context
import android.media.CamcorderProfile
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.view.SurfaceHolder
import com.panic.button.core.api.log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RecorderCameraHelper(val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    lateinit var file: File
    private var mediaPlayer = MediaPlayer()
    private var audioSavePathInDevice = ""

    init {
        audioSavePathInDevice = getPathFileName()
        mediaRecorder = MediaRecorder()

        mediaRecorder?.setVideoSource(MediaRecorder.VideoSource.CAMERA)
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
    }

    fun setupFrontCamera() {
        val camcorderProfile : CamcorderProfile

        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
            camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P)
            log("QUALITY_480P")
        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_HIGH_SPEED_480P)) {
            camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH_SPEED_480P)
            log("QUALITY_HIGH_SPEED_480P")
        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_TIME_LAPSE_480P)) {
            camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_TIME_LAPSE_480P)
            log("QUALITY_TIME_LAPSE_480P")
        } else {
            log("No CamcorderProfile")
            camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW)
        }
        mediaRecorder?.setOrientationHint(90)
        mediaRecorder?.setProfile(camcorderProfile)

        mediaRecorder?.setOutputFile(audioSavePathInDevice)
        mediaRecorder?.setOnInfoListener { mediaRecorder, i, i2 ->
            log("$i $i2")
        }
    }

    fun setupBackCamera() {
        mediaRecorder?.setOrientationHint(90)
        val camcorderProfile : CamcorderProfile

        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
            camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P)
            log("QUALITY_480P")
        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_HIGH_SPEED_480P)) {
            camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH_SPEED_480P)
            log("QUALITY_HIGH_SPEED_480P")
        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_TIME_LAPSE_480P)) {
            camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_TIME_LAPSE_480P)
            log("QUALITY_TIME_LAPSE_480P")
        } else {
            log("No CamcorderProfile")
            camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW)
        }

        mediaRecorder?.setProfile(camcorderProfile)

        mediaRecorder?.setOutputFile(audioSavePathInDevice)
        mediaRecorder?.setOnInfoListener { mediaRecorder, i, i2 ->
            log("$i $i2")
        }
    }

    fun setupSurface(surfaceView: SurfaceHolder) {
        mediaRecorder?.setPreviewDisplay(surfaceView.surface)
    }

    private fun getPathFileName(): String {
        val formatter = SimpleDateFormat("dd_MMMM_yyyy_HH_mm_ss", Locale("id"))
        formatter.calendar = Calendar.getInstance()
        val username = BaseApplication.sessionManager?.fullName?.replace(" ", "_")
        val fileName = username + "_" + formatter.format(Date()) + RemoteConfigHelper.get(
            RemoteConfigKey.EXTENISON_VIDEO_RECORD
        )
        val directory = context.getDir("recordku", Context.MODE_PRIVATE)
        file = File(directory, fileName)
        return file.absolutePath
    }

    fun startRecord() {
        try {
            mediaRecorder?.setMaxDuration(RECORD_VIDEO_DURATION)
            mediaRecorder?.prepare()
            mediaRecorder?.start()
        } catch (e: IllegalStateException) {
            log("IllegalStateException $e ${e.message}")
        } catch (e: IOException) {
            log("IOException $e ${e.message}")
        }

        log("Recording started")
        //showAlert(context, "Recording started")
    }

    fun stopRecord() {
        try {
            mediaRecorder?.stop()
        } catch (e: Exception) {
            log("error stopRecord $e")
        }
        //showAlert(context, "Recording Completed")
        log("Recording Completed")
    }

    fun playRecord() {
        try {
            mediaPlayer.setDataSource(audioSavePathInDevice)
            log("path $audioSavePathInDevice")
            mediaPlayer.prepare()
        } catch (e: IOException) {
            log("IOException $e ${e.message}")
        }

        mediaPlayer.start()
        showAlert(context, "Recording Playing")
    }

    fun stopPlayRecord() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    fun delete() {
        file.deleteRecursively()
    }

    fun resetMediaRecorder() {
        mediaRecorder?.release()
        mediaRecorder = null
    }

    companion object {
        const val RECORD_VIDEO_DURATION = 30000
    }
}