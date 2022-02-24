package com.panic.button.core.base

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.Toast
import com.panic.button.R
import com.panic.button.core.api.log
import io.fotoapparat.Fotoapparat
import io.fotoapparat.characteristic.LensPosition
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.configuration.UpdateConfiguration
import io.fotoapparat.log.fileLogger
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.result.BitmapPhoto
import io.fotoapparat.selector.*
import kotlinx.android.synthetic.main.view_capture_camera.view.*
import java.io.File

class CaptureCameraView : FrameLayout {

    var takePictureId = TYPE_PHOTO_CARD_ID
    private var isFlash = true
    private var fotoApparat: Fotoapparat? = null
    private var eventCapture: ((File) -> Unit)? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes)

    init {
        inflate(context, R.layout.view_capture_camera, this)
        takePictureImageView.setOnClickListener {
            takePicture()
        }
        setupMainCamera()
    }

    private fun cameraConfiguration(): CameraConfiguration {
        return CameraConfiguration.default()
    }

    fun setupSelfieCamera() {
        if (fotoApparat?.isAvailable { LensPosition.Front } == true) {
            setIsFlashView(false)
            stopCamera()
            setupFotoApparat(front())
        } else {
            Toast.makeText(context, "Kamera depan tidak support, akan diarahkan kamera belakang", Toast.LENGTH_SHORT)
                    .show()

            setupMainCamera()
        }

        setMessageView("Foto Selfie dengan Kartu Identitas", "Pastikan wajah dan kartu identitas kamu berada pada area kotak, dan terlihat jelas dengan penerangan yang cukup")
    }

    fun setupMainCamera() {
        setIsFlashView(true)
        setupFotoApparat(back())
        setMessageView("Foto Kartu Identitas", "Pastikan kartu identitas kamu berada pada area kotak, dan terlihat jelas dengan penerangan yang cukup")
    }

    private fun setupFotoApparat(lensPositionSelector: LensPositionSelector) {
        fotoApparat = Fotoapparat(
                context = context,
                view = cameraView,
                lensPosition = lensPositionSelector,
                cameraConfiguration = cameraConfiguration(),
                logger = loggers(logcat(), fileLogger(context)),
                cameraErrorCallback = { error ->
                    log("error Camera ${error.message}")
                    log("error Camera ${error.cause}")
                })
        fotoApparat?.start()
    }

    fun setEventCapture(eventCapture: ((File) -> Unit)? = null) {
        this.eventCapture = eventCapture
    }

    fun setEventClose(eventListener: () -> Unit) {
        closeImageView.setOnClickListener {
            eventListener()
        }
    }

    fun setEventFlash(eventListener: () -> Unit) {
        flashImageView.setOnClickListener {
            eventListener()
        }
    }

    private fun setIsFlashView(value: Boolean) {
        if (value) {
            flashImageView.setImageResource(R.drawable.ic_on_flash)
        } else {
            flashImageView.setImageResource(R.drawable.ic_off_flash)
        }
        flashImageView.isEnabled = value
    }

    private fun takePicture() {
        val photoResult = fotoApparat?.takePicture()
        val file = getMediaFile()

        photoResult?.saveToFile(file)
        photoResult?.toBitmap()?.whenAvailable { bitmap ->
            rotateCapture(bitmap)
            setIsRetake(true)
            saveAndRetakeClickAction(file)
        }
    }

    private fun getMediaFile(): File {
        return MediaHelper.getOutputMediaFile(MediaHelper.MEDIA_TYPE_IMAGE, context.getString(R.string.default_notification_channel_id)) ?: createNewFile(context)
    }

    private fun rotateCapture(bitmap: BitmapPhoto?) {
        bitmap?.let {
            try {
                setImageBitmap(isNeedConvertBitmap = false, realBitmap = it.bitmap)
            } catch (exception : Exception) {
                setImageBitmap(isNeedConvertBitmap = true, realBitmap = it.bitmap)
                log("error $exception ${exception.message}")
            }
        }
    }

    private fun setImageBitmap(isNeedConvertBitmap: Boolean, realBitmap: Bitmap) {
        var bitmap = realBitmap

        if (isNeedConvertBitmap) {
            bitmap = convertBitmap(context, bitmap)
        }

        if (takePictureId == TYPE_PHOTO_CARD_ID || takePictureId == NO_FRAME) {
            resultPictureImageView.setImageBitmap(MediaHelper.rotateBitmap(bitmap, RIGHT_DIRECTION))
        } else {
            resultPictureImageView.setImageBitmap(MediaHelper.rotateBitmap(bitmap, LEFT_DIRECTION))
        }
    }

    private fun saveAndRetakeClickAction(file: File?) {
        retakeImageView.setOnClickListener {
            setIsRetake(false)
            startSetupCamera()
        }
        saveImageView.setOnClickListener {
            file?.apply {
                eventCapture?.let { it1 ->
                    setIsRetake(false)
                    it1(this)
                }
            }
        }
    }

    private fun startSetupCamera() {
        if (takePictureId == TYPE_PHOTO_SELFIE_CARD_ID) {
            setupSelfieCamera()
        } else {
            setupMainCamera()
        }
    }

    private fun setIsRetake(isRetake: Boolean) {
        retakeImageView.isVisible = isRetake
        saveImageView.isVisible = isRetake
        takePictureImageView.isVisible = !isRetake
        framePhotoView.isVisible = !isRetake
        resultPictureImageView.isVisible = isRetake
        fotoApparat?.stop()
        if (!isRetake) {
            setTypePhotoView(takePictureId)
        }
    }

    fun setTypePhotoView(typePhoto : Int) {
        when (typePhoto) {
            TYPE_PHOTO_CARD_ID -> framePhotoView.setImageResource(R.drawable.ic_frame_photo)
            TYPE_PHOTO_SELFIE_CARD_ID -> framePhotoView.setImageResource(R.drawable.ic_frame_photo_selfie)
        }
        takePictureId = typePhoto
    }

    fun setupFlash() {
        fotoApparat?.updateConfiguration(UpdateConfiguration(flashMode = if (isFlash) {
            isFlash = false
            flashImageView.setImageResource(R.drawable.ic_off_flash)
            firstAvailable(torch(), off())
        } else {
            isFlash = true
            flashImageView.setImageResource(R.drawable.ic_on_flash)
            off()
        }))
    }

    fun stopCamera() {
        fotoApparat?.stop()
        setIsRetake(false)
    }

    private fun setMessageView(title: String, message: String) {
        titleTextView.text = title
        messageTextView.text = message
    }

    fun hideBottomDescription() {
        titleTextView.isVisible = false
        messageTextView.isVisible = false
        middleGuideline.setGuidelinePercent(0.79.toFloat())
    }

    companion object {
        const val TYPE_PHOTO_CARD_ID = 0
        const val TYPE_PHOTO_SELFIE_CARD_ID = 1
        const val NO_FRAME = 2
        const val LEFT_DIRECTION = -90
        const val RIGHT_DIRECTION = 90
    }
}