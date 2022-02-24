package com.panic.button.core.base

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import com.panic.button.core.api.log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RecorderVoiceHelper(val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    lateinit var file: File
    private var mediaPlayer = MediaPlayer()
    private var audioSavePathInDevice = ""

    init {
        audioSavePathInDevice = getPathFileName()
        setupMediaRecorder()
    }

    private fun setupMediaRecorder() {
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder?.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        mediaRecorder?.setOutputFile(audioSavePathInDevice)
        mediaRecorder?.setOnInfoListener { mediaRecorder, i, i2 ->
            log("$i $i2")
        }
    }

    private fun getPathFileName(): String {
        val formatter = SimpleDateFormat("dd_MMMM_yyyy_HH_mm_ss", Locale("id"))
        formatter.calendar = Calendar.getInstance()
        val username = BaseApplication.sessionManager?.fullName?.replace(" ","_")
        val fileName = username + "_" + formatter.format(Date()) + RemoteConfigHelper.get(RemoteConfigKey.EXTENISON_VOICE_RECORD)
        val directory = context.getDir("recordku", Context.MODE_PRIVATE)
        file = File(directory, fileName)
        return file.absolutePath
    }

    fun startRecord() {
        try {
            mediaRecorder?.setMaxDuration(RECORD_VOICE_DURATION)
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
        } catch (e : Exception) {
            log("error stopRecord ${e.message}")
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
        setupMediaRecorder()
    }

    fun delete() {
        file.deleteRecursively()
    }

    fun resetMediaRecorder() {
        mediaRecorder?.release()
        mediaRecorder = null
    }

    companion object {
        const val RECORD_VOICE_DURATION = 60000
    }
}