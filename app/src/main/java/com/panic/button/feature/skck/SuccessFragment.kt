package com.panic.button.feature.skck

import com.panic.button.R
import com.panic.button.BR
import com.panic.button.core.base.MvvmFragment
import com.panic.button.databinding.FragmentSuccessBinding

class SuccessFragment : MvvmFragment<FragmentSuccessBinding, SkckViewModel>(SkckViewModel::class){

    override val layoutResource: Int = R.layout.fragment_success

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.fragment = this
    }

    fun onSuccess() {
        activity?.finish()
    }
}