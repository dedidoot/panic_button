package com.panic.button.feature.skck

import android.view.View
import androidx.lifecycle.Observer
import com.panic.button.R
import com.panic.button.BR
import com.panic.button.core.base.MvvmFragment
import com.panic.button.core.base.makeHandler
import com.panic.button.databinding.FragmentOtherBinding
import kotlinx.android.synthetic.main.fragment_other.*

class OtherFragment : MvvmFragment<FragmentOtherBinding, SkckViewModel>(SkckViewModel::class){

    override val layoutResource: Int = R.layout.fragment_other

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.fragment = this
        viewModel.stepFiveErrorsMessage.observe(this, Observer {
            it?.apply {
                makeHandler(500) {
                    scrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        })
    }
}