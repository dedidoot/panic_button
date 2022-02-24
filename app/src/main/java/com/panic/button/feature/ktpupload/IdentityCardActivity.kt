package com.panic.button.feature.ktpupload

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import android.widget.Button
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.panic.button.BR
import com.panic.button.R
import com.panic.button.core.api.isVisible
import com.panic.button.core.base.*
import com.panic.button.core.base.BaseApplication.Companion.showToast
import com.panic.button.core.base.location.LocationHelper
import com.panic.button.core.base.location.RequestLocation
import com.panic.button.databinding.ActivityIdentityCardBinding
import com.panic.button.feature.registerprofile.RegisterProfileActivity
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.android.synthetic.main.activity_identity_card.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class IdentityCardActivity : MvvmActivity<ActivityIdentityCardBinding, IdentityCardViewModel>(IdentityCardViewModel::class) {

    override val layoutResource: Int = R.layout.activity_identity_card

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        viewModel.processIntent(intent)
        setupEventTakePicture()
        viewModel.isSuccess.observe(this, Observer {
            if (it == true) {
                BaseApplication.sessionManager?.registerCitizen = ""
                SuccessActivity.onStartActivity(this)
                finish()
            }
        })
        viewModel.uploadCardIdSuccess.observe(this, Observer {
            it.takeIf { !it.isNullOrEmpty() }?.apply {
                setButtonSuccess(ktpButton)
            }
        })
        viewModel.uploadCardSelfieIdSuccess.observe(this, Observer {
            it.takeIf { !it.isNullOrEmpty() }?.apply {
                setButtonSuccess(selfieButton)
            }
        })

        viewModel.errorsMessage.observe(this, Observer {
            it?.apply {
                makeHandler(500) {
                    scrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        })

        LocationHelper.showRequestPermission(this)
    }

    private fun setButtonSuccess(button: Button) {
        button.setTextColor(ContextCompat.getColor(this, R.color.white))
        button.setBackgroundResource(R.drawable.bg_red_radius_4)
        button.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(this, R.drawable.ic_white_check_list), null, null, null)
    }

    fun onSelfie() {
        viewModel.isUploadKtp = false
        showSelfieCamera()
    }

    fun onKtp() {
        viewModel.isUploadKtp = true
        showMainCamera()
    }

    fun onContinue() {
        if (viewModel.isFromEditProfile.value == true) {
            if (viewModel.uploadCardIdSuccess.value.isNullOrEmpty()) {
                showToast("Mohon upload foto ktp Anda")
                return
            }
            if (viewModel.uploadCardSelfieIdSuccess.value.isNullOrEmpty()) {
                showToast("Mohon upload foto selfie dengan ktp Anda")
                return
            }
            startActivity(RegisterProfileActivity.onNewIntent(this, isFromEditProfile = true))
            finish()
        } else {
            viewModel.onCitizenRegister()
        }
    }

    override fun onBackPressed() {
        if (capturePictureView.isVisible) {
            hideCamera()
        } else {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        hideCamera()
        super.onPause()
    }

    private fun hideCamera() {
        capturePictureView.stopCamera()
        capturePictureView.isVisible = false
        button.isVisible = true
    }

    private fun showSelfieCamera() {
        if (PermissionHelper.isAllPermissionForCameraGranted(this)) {
            capturePictureView.setTypePhotoView(CaptureCameraView.TYPE_PHOTO_SELFIE_CARD_ID)
            capturePictureView.setupSelfieCamera()
            capturePictureView.isVisible = true
            button.isVisible = false
        } else {
            PermissionHelper.checkPermissionStorageAndCamera(this)
        }
    }

    private fun showMainCamera() {
        if (PermissionHelper.isAllPermissionForCameraGranted(this)) {
            capturePictureView.setTypePhotoView(CaptureCameraView.TYPE_PHOTO_CARD_ID)
            capturePictureView.setupMainCamera()
            capturePictureView.isVisible = true
            button.isVisible = false
        } else {
            PermissionHelper.checkPermissionStorageAndCamera(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PermissionHelper.PERMISSION_STORAGE_AND_CAMERA -> {
                for (i in permissions.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        showToast("Fitur tidak diizinkan")
                        finish()
                        break
                    }
                }
            }
            LocationHelper.KEY_REQUEST_FINE_LOCATION -> {
                for (i in permissions.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        showAlert(this, "Mohon izinkan aplikasi mengakses fitur gps!")
                        finish()
                        break
                    }
                }
                RequestLocation(this)
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun setupEventTakePicture() {
        capturePictureView.setEventCapture {
            if (it.exists()) {
                hideCamera()
                checkUpload(it)
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

    private fun checkUpload(it: File) {
        viewModel.isLoadingUpload.value = true
        try {
            GlobalScope.launch {
                val compressedImageFile = Compressor.compress(this@IdentityCardActivity, it) {
                    quality(
                        RemoteConfigHelper.getLong(RemoteConfigKey.QUALITY_IMAGE_COMPRESS).toInt()
                    )
                }
                delay(1000)

                runOnUiThread {
                    if (viewModel.isUploadKtp) {
                        viewModel.uploadCard(compressedImageFile)
                    } else {
                        viewModel.uploadCardSelfie(compressedImageFile)
                    }
                }
            }
        }  catch (exception : Exception) {
            viewModel.isLoadingUpload.value = false
            hideCamera()
            showToast("Camera capture file is corrupt, please try again!")
        }
    }

    companion object {

        fun onNewIntent(context: Context, isFromEditProfile : Boolean = false): Intent {
            val intent = Intent(context, IdentityCardActivity::class.java)
            intent.putExtra(IdentityCardViewModel.EXTRA_IS_FROM_EDIT_PROFILE, isFromEditProfile)
            return intent
        }
    }
}