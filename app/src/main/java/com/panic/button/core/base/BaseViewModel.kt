package com.panic.button.core.base

import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    fun showToast(message: String) {
        BaseApplication.showToast(message)
    }
}