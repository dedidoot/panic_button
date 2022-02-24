package com.panic.button.core.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import org.koin.android.viewmodel.ext.android.viewModelByClass
import kotlin.reflect.KClass

abstract class MvvmActivity<T : ViewDataBinding, V : BaseViewModel>(clazz: KClass<V>) : AppCompatActivity() {

    lateinit var binding: T
    val viewModel: V by viewModelByClass(clazz)
    private var tokenExpiredDialog : BaseDialogView? = null

    protected abstract val layoutResource: Int

    protected abstract fun viewLoaded()

    protected abstract val bindingVariable: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutResource)
        binding.setVariable(bindingVariable, viewModel)
        binding.executePendingBindings()
        binding.lifecycleOwner = this
        observeTokenExpiredDialog()
        viewLoaded()
    }

    private fun observeTokenExpiredDialog() {
        BaseApplication.baseApplication?.isShowTokenExpiredDialog?.observe(this, Observer {
            if (it == true) {
                showTokenExpiredDialog()
            }
        })
    }

    private fun showTokenExpiredDialog() {
        if (tokenExpiredDialog == null) {
            tokenExpiredDialog = BaseDialogView(this)
        }
        
        tokenExpiredDialog?.setMessage("Token kedaluwarsa, mohon login kembali!")?.setOnClickPositive {
                BaseApplication.baseApplication?.isShowTokenExpiredDialog?.value = false
                BaseApplication.baseApplication?.logout()
            }

        if (tokenExpiredDialog?.dialog?.isShowing == false) {
            tokenExpiredDialog?.show()
        }
    }
}