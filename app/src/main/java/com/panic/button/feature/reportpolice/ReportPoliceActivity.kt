package com.panic.button.feature.reportpolice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.panic.button.R
import com.panic.button.core.api.GSONManager
import com.panic.button.core.api.PostRequest
import com.panic.button.core.api.UploadRequest
import com.panic.button.core.api.log
import com.panic.button.core.api.response.BaseResponse
import com.panic.button.core.api.response.MediaResponse
import com.panic.button.core.base.*
import com.panic.button.core.base.BaseApplication.Companion.showToast
import com.panic.button.core.model.Urls
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.android.synthetic.main.activity_report_police.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class ReportPoliceActivity : AppCompatActivity() {

    private var uploadImageIdSuccess = ArrayList<String>()
    private var uploadVideoIdSuccess = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_police)
        descEditText.setRawInputType(InputType.TYPE_CLASS_TEXT)
        setupEventTakePicture()
        openCameraButton.setOnClickListener {
            hideKeyboard(this)
            showMainCamera()
        }
        openVideoButton.setOnClickListener {
            hideKeyboard(this)
            GalleryHelper(this).showVideo()
        }
    }

    fun onBackReport(view: View) {
        finish()
    }

    fun onSend(view: View) {
        if (titleEditText.text.isNullOrBlank()) {
            showAlert(this, "Judul laporan tidak boleh kosong!")
            return
        }
        if (descEditText.text.isNullOrBlank()) {
            showAlert(this, "Deskripsi laporan tidak boleh kosong!")
            return
        }

        hideKeyboard(this)
        showLoading(true)

        val params = HashMap<String, Any>()
        params["title"] = titleEditText.text.toString()
        params["report"] = descEditText.text.toString()
        params["images"] = uploadImageIdSuccess
        params["videos"] = uploadVideoIdSuccess

        PostRequest<HashMap<String, Any>>(Urls.POST_INCIDENT).post(params, {
            val response = GSONManager.fromJson(it, BaseResponse::class.java)
            showLoading(false)
            if (response.isSuccess()) {
                showSuccessDialog()
            } else {
                showAlert(this, getMessage(it))
            }
        })
    }

    private fun showSuccessDialog() {
        BaseDialogView(this).setMessage("Laporan berhasil dikirim!").setPositiveString("Oke").setOnClickPositive {
            startActivity(Intent(this, ReportPoliceActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }.show()
    }

    private fun showLoading(isLoading: Boolean) {
        loading.isVisible = isLoading
        button.isVisible = !isLoading
    }

    private fun getMessage(it: String): String {
        val regex = Regex("[^[A-Za-z ]]")
        return it.split("[").getOrNull(1)?.replace(regex,"") ?: "Gagal kirim laporan"
    }

    override fun onBackPressed() {
        if (capturePictureView.isVisible) {
            hideCamera()
        } else {
            super.onBackPressed()
        }
    }

    private fun hideCamera() {
        capturePictureView.stopCamera()
        capturePictureView.isVisible = false
    }

    private fun showMainCamera() {
        if (PermissionHelper.isAllPermissionForCameraGranted(this)) {
            capturePictureView.setupMainCamera()
            capturePictureView.isVisible = true
        } else {
            PermissionHelper.checkPermissionStorageAndCamera(this)
        }
    }

    private fun setupEventTakePicture() {
        capturePictureView.setTypePhotoView(CaptureCameraView.NO_FRAME)
        capturePictureView.hideBottomDescription()
        capturePictureView.setEventCapture {
            if (it.exists()) {
                hideCamera()
                uploadCard(it)
            } else {
                showToast("File not found")
            }
        }

        capturePictureView.setEventFlash {
            capturePictureView.setupFlash()
        }
        capturePictureView.setEventClose {
            hideCamera()
        }
    }

    private fun uploadCard(it: File) {
        showLoading(true)
        try {
            GlobalScope.launch {
                val compressedImageFile = Compressor.compress(this@ReportPoliceActivity, it) {
                    quality(RemoteConfigHelper.getLong(RemoteConfigKey.QUALITY_IMAGE_COMPRESS).toInt())
                }
                delay(1000)

                runOnUiThread {
                    UploadRequest(Urls.UPLOAD_MEDIA).upload(compressedImageFile, "incident_image", { realResponse ->
                        val response = GSONManager.fromJson(realResponse, MediaResponse::class.java)
                        if (response.isSuccess == true) {
                            setupUploadImageSuccess(response, compressedImageFile)
                        } else {
                            showToast(response.message ?: "Gagal upload")
                        }
                        showLoading(false)
                    })
                }
            }
        }  catch (exception : Exception) {
            showLoading(false)
            hideCamera()
            showToast("Camera capture file is corrupt, please try again!")
        }
    }

    private fun setupUploadImageSuccess(response: MediaResponse, file: File) {
        uploadImageIdSuccess.add("${response.mediaModel?.id}")
        val context = this@ReportPoliceActivity

        val image = AppCompatImageView(context)
        try {
            setImageBitmap(imageView = image, file = file)
        } catch (exception : Exception) {
            image.setImageResource(R.drawable.bg_grey_radius_4)
            log("error $exception ${exception.message}")
        }
        image.scaleType = ImageView.ScaleType.CENTER_CROP
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500)
        image.layoutParams = params
        rootLinerLayout.addView(image)

        val lineView = View(context)
        val lineViewParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 20)
        lineView.layoutParams = lineViewParams
        rootLinerLayout.addView(lineView)
    }

    private fun setImageBitmap(imageView : ImageView, file: File) {
        val bitmap = MediaHelper.decodeScaledBitmapFromSdCard(file.absolutePath, widthScreenSize(this), 500)
        val compressBitmap = convertBitmap(this, bitmap)
        imageView.setImageBitmap(compressBitmap)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GalleryHelper.ARG_VIDEO && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                uploadVideo(FileUtil.from(this, it))
            } ?: kotlin.run {
                showAlert(this, "Video tidak ditemukan")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun uploadVideo(file: File) {
        showLoading(true)
        UploadRequest(Urls.UPLOAD_VIDEO).upload(file, UploadRequest.INCIDENT_VIDEO, {
            val response = GSONManager.fromJson(it, MediaResponse::class.java)
            if (response.isSuccess()) {
                setupUploadVideoSuccess(file, response)
            } else {
                showToast(response.message ?: "Gagal upload video")
            }
            showLoading(false)
        }, isVideo = true)
    }

    private fun setupUploadVideoSuccess(file: File, response: MediaResponse) {
        uploadVideoIdSuccess.add("${response.mediaModel?.id}")
        val text = TextView(this)
        text.text = file.name
        text.setTextColor(ContextCompat.getColor(this, R.color.white))
        resources?.apply {
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, getDimensionPixelSize(R.dimen._6sdp).toFloat())
        }
        rootLinerLayout.addView(text)

        val lineView = View(this)
        val lineViewParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 20)
        lineView.layoutParams = lineViewParams
        rootLinerLayout.addView(lineView)
    }

    fun onHistory(view: View) {
        startActivity(Intent(this, HistoryReportActivity::class.java))
    }
}