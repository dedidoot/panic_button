package com.panic.button.feature.register

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import com.panic.button.BR
import com.panic.button.R
import com.panic.button.core.base.MvvmActivity
import com.panic.button.core.base.hideKeyboard
import com.panic.button.core.base.rajawaliPermission
import com.panic.button.databinding.ActivityRegisterBinding
import com.panic.button.feature.login.LoginActivity
import com.panic.button.feature.termcondition.TermConditionActivity
import pub.devrel.easypermissions.EasyPermissions

class RegisterActivity : MvvmActivity<ActivityRegisterBinding, RegisterViewModel>(RegisterViewModel::class) {

    override val layoutResource: Int = R.layout.activity_register

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        viewModel.isSuccess.observe(this, Observer {
            startActivity(TermConditionActivity.onNewIntent(this))
        })

        if (!EasyPermissions.hasPermissions(this@RegisterActivity, *rajawaliPermission)) {
            EasyPermissions.requestPermissions(this, "Aplikasi membutuhkan akses device, mohon izinkan terlebih dahulu",
                0, *rajawaliPermission)
        }
    }

    fun onRegister() {
        viewModel.onSave()
        hideKeyboard(this)
    }

    fun onLogin() {
        startActivity(LoginActivity.onNewIntent(this, isOfficer = false))
        finish()
    }

    companion object {
        fun onNewIntent(context: Context): Intent {
            return Intent(context, RegisterActivity::class.java)
        }
    }
}