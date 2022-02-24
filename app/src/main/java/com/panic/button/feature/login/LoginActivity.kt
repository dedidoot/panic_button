package com.panic.button.feature.login

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import com.panic.button.BR
import com.panic.button.R
import com.panic.button.core.base.*
import com.panic.button.core.base.location.RequestLocation
import com.panic.button.databinding.ActivityLoginBinding
import com.panic.button.feature.home.HomeActivity
import com.panic.button.feature.police.CovidOfficerActivity
import com.panic.button.feature.police.PoliceActivity
import com.panic.button.feature.register.RegisterActivity
import kotlinx.android.synthetic.main.activity_login.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class LoginActivity : MvvmActivity<ActivityLoginBinding, LoginViewModel>(LoginViewModel::class), EasyPermissions.PermissionCallbacks {

    private var isSignInOfficer: Boolean? = null

    override val layoutResource: Int = R.layout.activity_login

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        isSignInOfficer = intent?.getBooleanExtra(EXTRA_IS_OFFICER, false)
        registerButton.isVisible = isSignInOfficer == false
        if (isSignInOfficer == true) {
            signAsTextView.text = "Sign in as Citizen"
        } else {
            signAsTextView.text = "Sign in as Police"
        }

        observeLoginSuccess()
        BaseApplication.baseApplication?.registerFcmToken()
    }

    private fun observeLoginSuccess() {
        viewModel.isSuccess.observe(this, Observer {
            val intent: Intent

            if (isCovidOfficer()) {
                intent = CovidOfficerActivity.onNewIntent(this)
            } else if (BaseApplication.sessionManager?.isPolice == true) {
                intent = PoliceActivity.onNewIntent(this)
            } else {
                intent = Intent(this, HomeActivity::class.java)
            }
            startActivity(intent)
            finish()
        })
    }

    fun onRegister() {
        if (!EasyPermissions.hasPermissions(this@LoginActivity, *rajawaliPermission)) {
            EasyPermissions.requestPermissions(this, "Aplikasi membutuhkan akses device, mohon izinkan terlebih dahulu", 0, *rajawaliPermission)
        } else {
            startActivity(RegisterActivity.onNewIntent(this))
            finish()
        }
    }

    fun onSignInAs() {
        if (signAsTextView.text?.contains("police", ignoreCase = true) == true) {
            startActivity(onNewIntent(this, isOfficer = true))
        } else {
            startActivity(onNewIntent(this, isOfficer = false))
        }
        finish()
    }

    fun onLogin() {
        hideKeyboard(this)
        if (BaseApplication.sessionManager?.isLastLocationNotEmpty() == true) {
            onProcessLogin()
        } else {
            checkFeaturePermissionGranted()
        }
    }

    private fun showPermissionDialog() {
        EasyPermissions.requestPermissions(this, "Aplikasi membutuhkan akses device, mohon izinkan terlebih dahulu", EASY_PERMISSION_CODE, *rajawaliPermission)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPermissionsGranted(requestCode: Int, permissions: MutableList<String>?) {
        permissions?.find { it.equals(Manifest.permission.ACCESS_FINE_LOCATION, ignoreCase = true) }?.apply {
            RequestLocation(this@LoginActivity)
        }
    }

    @AfterPermissionGranted(EASY_PERMISSION_CODE)
    private fun checkFeaturePermissionGranted() {
        if (!EasyPermissions.hasPermissions(this@LoginActivity, *rajawaliPermission)) {
            showPermissionDialog()
        } else {
            onProcessLogin()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, permissions: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, permissions)) {
            AppSettingsDialog.Builder(this)
                .setTitle(getString(R.string.title_settings_dialog))
                .setRationale(getString(R.string.rationale_ask_again))
                .setPositiveButton("Pengaturan")
                .setNegativeButton("Batal")
                .setRequestCode(EASY_PERMISSION_CODE)
                .build()
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestLocation.LOCATION_CODE && resultCode == Activity.RESULT_OK) {
            onProcessLogin()
        } else if (requestCode == RequestLocation.LOCATION_CODE && resultCode == Activity.RESULT_CANCELED) {
            RequestLocation(this@LoginActivity)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun onProcessLogin() {
        if (EasyPermissions.hasPermissions(this@LoginActivity, *rajawaliPermission)) {
            viewModel.onLogin(isSignInOfficer)
        } else {
            showPermissionDialog()
        }
    }

    companion object {

        const val EXTRA_IS_OFFICER = "extra_is_officer"
        const val EASY_PERMISSION_CODE = 342

        fun onNewIntent(context: Context, isOfficer: Boolean): Intent {
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra(EXTRA_IS_OFFICER, isOfficer)
            return intent
        }
    }
}