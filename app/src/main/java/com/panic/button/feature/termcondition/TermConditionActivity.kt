package com.panic.button.feature.termcondition

import android.content.Context
import android.content.Intent
import com.panic.button.BR
import com.panic.button.R
import com.panic.button.core.base.*
import com.panic.button.databinding.ActivityTermConditionBinding
import com.panic.button.feature.login.LoginViewModel
import com.panic.button.feature.registerprofile.RegisterProfileActivity
import kotlinx.android.synthetic.main.activity_term_condition.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TermConditionActivity : MvvmActivity<ActivityTermConditionBinding, LoginViewModel>(LoginViewModel::class) {

    override val layoutResource: Int = R.layout.activity_term_condition

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        getTermConditionText().takeIf { it.isNotEmpty() }?.let {
            termConditionTextView.setHtmlText(it)
            loading.isVisible = false
        } ?: kotlin.run {
            GlobalScope.launch {
                setTerm()
            }
        }
    }

    private suspend fun setTerm() {
        delay(1000)
        BaseApplication.baseApplication?.setupFirebaseRemoteConfig {
            loading.isVisible = false
            termConditionTextView.setHtmlText(getTermConditionText())
        }
    }

    private fun getTermConditionText(): String {
        return RemoteConfigHelper.get(RemoteConfigKey.TERM_CONDITION_REGISTER)
    }

    fun onContinue() {
        if (checkBox.isChecked) {
            startActivity(RegisterProfileActivity.onNewIntent(this))
        } else {
            showAlert(this, "Anda tidak menyetujui syarat dan ketentuan yang berlaku")
        }
    }

    companion object {
        fun onNewIntent(context: Context): Intent {
            return Intent(context, TermConditionActivity::class.java)
        }
    }
}