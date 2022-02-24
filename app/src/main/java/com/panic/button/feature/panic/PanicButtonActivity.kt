package com.panic.button.feature.panic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.panic.button.R
import com.panic.button.core.api.*
import com.panic.button.core.base.*
import com.panic.button.core.base.RemoteConfigHelper.getLong
import com.panic.button.core.base.RemoteConfigKey.INTERVAL_VIDEO
import com.panic.button.core.base.camera.CameraPreview
import com.panic.button.core.base.camera.RecorderCameraHelper
import com.panic.button.core.base.camera.getPathVideoFileName
import com.panic.button.core.base.location.RequestLocation
import com.panic.button.core.model.CallModel
import com.panic.button.core.model.CallResponse
import com.panic.button.core.model.RegisterModel
import com.panic.button.core.model.Urls
import kotlinx.android.synthetic.main.activity_panic_button.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class PanicButtonActivity : AppCompatActivity(), CoroutineScope {

    private val timer = TimerHelper
    private var countTime = 10
    private var callId = ""
    private var isConfigurationFinish = false
    private var callCompleteDialog: BaseDialogView? = null
    private var intervalRemoteConfig: Long = 20000

    private var voiceHandler = Handler(Looper.getMainLooper())
    private var voiceRunnable = voiceRecorderRunnable()

    private var locationHandler = Handler(Looper.getMainLooper())
    private var locationHandlerRunnable = locationRunnable()

    private var videoRecordingHandler = Handler(Looper.getMainLooper())
    private var videoRecordingHandlerRunnable = videoRecordingRunnable()

    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var isFrontCamera = false
    private var isCameraRecordSupport = true

    private var videoRecordingFile : File? = null

    override val coroutineContext get() = Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panic_button)
        initializeVideoRecording()

        try { Glide.with(this).load(R.drawable.ic_sirine).into(ovalImageView) } catch (e: Exception) { e.printStackTrace() }

        if (getLong(INTERVAL_VIDEO) > 0) {
            intervalRemoteConfig = getLong(INTERVAL_VIDEO)
        }

        if (!BaseApplication.sessionManager?.roomsId.isNullOrBlank()) {
            callId = BaseApplication.sessionManager?.roomsId ?: ""
            setDrawableTextView(geoTextView, R.drawable.ic_verified)
            setDrawableTextView(frontCamTextView, R.drawable.ic_verified)
            setDrawableTextView(backCamTextView, R.drawable.ic_verified)
            setDrawableTextView(microphoneTextView, R.drawable.ic_verified)

            setConfigurationFinish()
            cancelButton.setTextColor(getColorText())
            cancelButton.isEnabled = false
            val secondReset = "00"
            secondTextView.text = secondReset

            startRecordLocation()
            makeHandler(3000) {
                captureVideoOrStopRecording()
            }
        } else {
            initStartStreamingView()
        }
    }

    private fun initializeVideoRecording() {
        // if the front facing camera does not exist
        if (findFrontFacingCamera() < 0) {
            showAlert(this, "No front facing camera found.")
        }

        try {
            if (hasCamera()) {
                mCamera = Camera.open(findBackFacingCamera())
                mPreview = CameraPreview(this, mCamera)
                mPreview?.refreshCamera(mCamera)
                cameraPreview.addView(mPreview)
            } else {
                isCameraRecordSupport = false
            }
        } catch (exception : Exception) {
            isCameraRecordSupport = false
            log("exception $exception ${exception.cause} ${exception.message}")
        }
    }

    private fun uploadRecordCamera() {
        videoRecordingFile?.apply {
            UploadRequest(Urls.UPLOAD_PANIC_MANUAL).uploadFilePanicButton(this, callId, "video-manual", {
                try { videoRecordingFile?.deleteRecursively() } catch (e : Exception) { log("error deleteRecursively $e") }
                captureVideoOrStopRecording()
            })
        } ?: kotlin.run { captureVideoOrStopRecording() }
    }

    private fun initStartStreamingView() {
        hideTimer()
        setupCountDown()
        callStreaming()

        cancelButton.setOnClickListener {
            PostRequest<CallModel>(Urls.CANCEL_CONNECT).post(
                params = CallModel(callId = callId),
                response = {
                    finishCall()
                })
        }
    }

    private fun setupCountDown() {
        timer.timerListener = object : TimerHelper.TimerListener {
            override fun onTimerTick(millis: Long) {
                countTime -= 1
                val time = "0$countTime"
                secondTextView.text = time
            }

            override fun onTimerFinished() {
                cancelButton.setTextColor(getColorText())
                cancelButton.isEnabled = false
                val secondReset = "00"
                secondTextView.text = secondReset
            }
        }
    }

    private fun getColorText(): Int {
        return ContextCompat.getColor(this@PanicButtonActivity, R.color.grey5_disable)
    }

    private fun showTimer() {
        cancelButton.visibility = View.VISIBLE
        stopwatchImageView.visibility = View.VISIBLE
        secondTextView.visibility = View.VISIBLE
        milliSecondTextView.visibility = View.VISIBLE
    }

    private fun hideTimer() {
        cancelButton.visibility = View.GONE
        stopwatchImageView.visibility = View.GONE
        secondTextView.visibility = View.GONE
        milliSecondTextView.visibility = View.GONE
    }

    private fun callStreaming() {
        timer.startTimer(countTime)
        showTimer()
        setWaiting()

        PostRequest<RegisterModel>(Urls.MAKE_CALL).post(
            params = RegisterModel(
                latitude = BaseApplication.sessionManager?.latitude?.toDoubleOrNull() ?: 0.0,
                longitude = BaseApplication.sessionManager?.longitude?.toDoubleOrNull() ?: 0.0
            ),
            response = {
                val response = GSONManager.fromJson(it, CallResponse::class.java)
                callId = response.data?.id ?: ""
                BaseApplication.sessionManager?.roomsId = callId
                if (isConfigurationFinish) {
                    startCall()
                }
            })

        setDrawableTextView(geoTextView, R.drawable.ic_dot_three)

        makeHandler(1500) {
            setDrawableTextView(geoTextView, R.drawable.ic_verified)

            setDrawableTextView(frontCamTextView, R.drawable.ic_dot_three)
            makeHandler(1500) {
                setDrawableTextView(frontCamTextView, R.drawable.ic_verified)

                setDrawableTextView(backCamTextView, R.drawable.ic_dot_three)
                makeHandler(1500) {
                    setDrawableTextView(backCamTextView, R.drawable.ic_verified)
                    setDrawableTextView(microphoneTextView, R.drawable.ic_dot_three)
                    makeHandler(1500) {
                        setConfigurationFinish()
                        if (callId.isNotBlank()) {
                            startCall()
                        }
                    }
                }
            }
        }
    }

    private fun startCall() {
        PostRequest<CallModel>(Urls.REQUEST_CONNECT).post(
            params = CallModel(
                callId = callId,
                latitude = BaseApplication.sessionManager?.latitude,
                longitude = BaseApplication.sessionManager?.longitude
            ),
            response = {
                val response = GSONManager.fromJson(it, CallResponse::class.java)
                callId = response.data?.id ?: ""
                BaseApplication.sessionManager?.roomsId = callId
                startRecordLocation()
                captureVideoOrStopRecording()
            })
    }

    private fun startRecordLocation() {
        if (!isFinishing) {
            RequestLocation(this)
            locationHandler.postDelayed(locationHandlerRunnable, 90000)
        }
    }

    private fun voiceRecorderRunnable(): Runnable {
        return Runnable {
            BaseApplication.recorderVoice?.stopRecord()
            uploadRecordVoice()
        }
    }

    private fun locationRunnable(): Runnable {
        return Runnable {
            updateLocation()
        }
    }

    private fun videoRecordingRunnable(): Runnable {
        return Runnable {
            captureVideoOrStopRecording()
            setupSwitchCamera()
            uploadRecordCamera()
        }
    }

    private fun startRecordVoice() {
        /**
         * We focus on camera video recording, basically camera video recording have voice
         * **/
        if (!isFinishing) {
            BaseApplication.recorderVoice = RecorderVoiceHelper(this)
            BaseApplication.recorderVoice?.startRecord()

            voiceHandler.removeCallbacks(voiceRunnable)
            voiceHandler.postDelayed(
                voiceRunnable,
                RecorderVoiceHelper.RECORD_VOICE_DURATION.toLong()
            )
        }
    }

    private fun updateLocation() {
        if (!isFinishing) {
            val model = CallModel(
                latitude = BaseApplication.sessionManager?.latitude,
                longitude = BaseApplication.sessionManager?.longitude
            )
            PostRequest<CallModel>(Urls.UPDATE_POSITION_CITIZEN).post(
                params = model,
                response = {
                })
        }
    }

    private fun uploadRecordVoice() {
        if (!isFinishing) {
            BaseApplication.recorderVoice?.file?.apply {
                UploadRequest(Urls.UPLOAD_PANIC_MANUAL).uploadFilePanicButton(
                    this,
                    callId,
                    "audio-manual",
                    { setRecorderVoiceSuccess() })
            }
        }
    }

    private fun setRecorderVoiceSuccess() {
        BaseApplication.recorderVoice?.delete()
        BaseApplication.recorderVoice?.resetMediaRecorder()
        if (!isFinishing) {
            startRecordVoice()
        }
    }

    private fun setWaiting() {
        val message = "Otentifikasi laporan, tunggu 10 detik.."
        infoTextView.text = message
    }

    private fun setConfigurationFinish() {
        isConfigurationFinish = true
        val message = "Anggota sedang dalam perjalanan..."
        val second = " Detik"
        infoTextView.text = message
        milliSecondTextView.text = second
        setDrawableTextView(microphoneTextView, R.drawable.ic_verified)
    }

    private fun setDrawableTextView(textView: AppCompatTextView, value: Int) {
        textView.setCompoundDrawablesWithIntrinsicBounds(pickDrawable(value), null, null, null)
    }

    override fun onBackPressed() {
    }

    override fun onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver, IntentFilter(
                VICTIM_DATA_RECEIVER
            )
        )
        super.onStart()
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        super.onStop()
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            log("victim_data_receiver")
            BaseApplication.sessionManager?.roomsId = ""
            callCompleteDialog()
        }
    }

    private fun callCompleteDialog() {
        if (callCompleteDialog == null) {
            callCompleteDialog = BaseDialogView(this)
        }

        callCompleteDialog?.setMessage("Panic button sudah berakhir, terima kasih!")
            ?.setPositiveString(
                "Oke"
            )?.setOnClickPositive {
                finishCall()
            }

        if (callCompleteDialog?.dialog?.isShowing == false) {
            callCompleteDialog?.show()
        }
    }

    private fun finishCall() {
        finish()
    }

    override fun onDestroy() {
        BaseApplication.recorderVoice?.stopRecord()
        uploadRecordVoice()
        super.onDestroy()
    }

    private fun findFrontFacingCamera(): Int {
        var cameraId = -1
        // Search for the front facing camera
        val numberOfCameras = Camera.getNumberOfCameras()
        for (i in 0 until numberOfCameras) {
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(i, info)
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i
                break
            }
        }
        return cameraId
    }

    private fun findBackFacingCamera(): Int {
        var cameraId = -1
        // Search for the back facing camera
        // get the number of cameras
        val numberOfCameras = Camera.getNumberOfCameras()
        // for every camera check
        for (i in 0 until numberOfCameras) {
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(i, info)
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i
                break
            }
        }
        return cameraId
    }

    private fun hasCamera(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    private fun releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder?.reset() // clear recorder configuration
            mediaRecorder?.release() // release the recorder object
            mediaRecorder = null
            mCamera?.lock() // lock camera for later use
        }
    }

    private fun prepareMediaRecorder(): Boolean {
        mediaRecorder = MediaRecorder()
        mCamera?.unlock()
        mediaRecorder?.setCamera(mCamera)
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
        mediaRecorder?.setVideoSource(MediaRecorder.VideoSource.CAMERA)
        mediaRecorder?.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW))
        videoRecordingFile = getPathVideoFileName(this)
        log("videoRecordingFile $videoRecordingFile")
        mediaRecorder?.setOutputFile(videoRecordingFile?.path)

        try {
            mediaRecorder?.prepare()
        } catch (e: IllegalStateException) {
            releaseMediaRecorder()
            launch { ReportRequest.reportPostWithInformationDevice("$e ======= ${e.message}") }
            return false
        } catch (e: IOException) {
            releaseMediaRecorder()
            launch { ReportRequest.reportPostWithInformationDevice("$e ======= ${e.message}") }
            return false
        }
        return true
    }

    private fun releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera?.release()
            mCamera = null
        }
    }

    private fun captureVideoOrStopRecording() {
        if (isCameraRecordSupport.not()) {
            startRecordVoice()
            tryRecorderCameraHelper()
            return
        }

        if (isRecording) {
            // stop recording and release camera

            try {
                mediaRecorder?.stop() // stop the recording
            } catch (ex : Exception) {
                log("mediaRecorder?.stop() error $ex ======= ${ex.message} ===== ${ex.cause}")
            }

            releaseMediaRecorder() // release the MediaRecorder object
            log("Video captured!")
            isRecording = false
        } else {
            if (!prepareMediaRecorder()) {
                showAlert(this, "Fail in prepareMediaRecorder()!\n - Ended -")
                finish()
            }
            // work on UiThread for better performance
            runOnUiThread {
                // If there are stories, add them to the table
                try {
                    mediaRecorder?.start()
                    videoRecordingHandler.postDelayed(videoRecordingHandlerRunnable, intervalRemoteConfig)
                } catch (ex: java.lang.Exception) {
                    log("mediaRecorder?.start() error $ex ======= ${ex.message} ===== ${ex.cause}")
                }
            }
            isRecording = true
        }
    }

    private fun tryRecorderCameraHelper() {
        try {
            mPreview?.holder?.apply {
                var isBackCamera = true
                val recorderCameraHelper  = RecorderCameraHelper(this@PanicButtonActivity)
                recorderCameraHelper.setupSurface(this)
                recorderCameraHelper.setupBackCamera()
                recorderCameraHelper.startRecord()
                makeHandler(RecorderCameraHelper.RECORD_VIDEO_DURATION.toLong()) {
                    recorderCameraHelper.stopRecord()
                    UploadRequest(Urls.UPLOAD_PANIC_MANUAL).uploadFilePanicButton(recorderCameraHelper.file, callId, "video-manual", {
                        try { recorderCameraHelper.delete() } catch (e : Exception) { log("error deleteRecursively $e") }
                        if (isBackCamera) {
                            isBackCamera = false
                            recorderCameraHelper.setupFrontCamera()
                            recorderCameraHelper.startRecord()
                        } else {
                            recorderCameraHelper.setupBackCamera()
                            recorderCameraHelper.startRecord()
                        }
                    })
                }
            }
        } catch (exception : Exception ) {
            log("exception $exception ${exception.cause} ${exception.message}")
        }
    }

    private fun setupSwitchCamera() {
        if (!isRecording) {
            val camerasNumber = Camera.getNumberOfCameras()
            if (camerasNumber > 1) {
                // release the old camera instance
                // switch camera, from the front and the back and vice versa
                releaseCamera()
                chooseCamera()
            } else {
                showAlert(this, "Sorry, your phone has only one camera!")
            }
        }
    }

    private fun chooseCamera() {
        // if the camera preview is the front
        if (!isFrontCamera) {
            val cameraId = findBackFacingCamera()
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview
                mCamera = Camera.open(cameraId)
                // mPicture = getPictureCallback();
                mPreview?.refreshCamera(mCamera)
            }
        } else {
            val cameraId = findFrontFacingCamera()
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview
                mCamera = Camera.open(cameraId)
                // mPicture = getPictureCallback();
                mPreview?.refreshCamera(mCamera)
            }
        }

        isFrontCamera = !isFrontCamera
    }

    companion object {

        const val VICTIM_DATA_RECEIVER = "victim_data_receiver"

        fun onNewIntent(context: Context): Intent {
            return Intent(context, PanicButtonActivity::class.java)
        }
    }
}