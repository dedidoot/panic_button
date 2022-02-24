package com.panic.button.feature.skck

import android.content.Context
import android.content.Intent
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.panic.button.BR
import com.panic.button.R
import com.panic.button.core.base.BaseDialogView
import com.panic.button.core.base.MvvmActivity
import com.panic.button.core.base.hideKeyboard
import com.panic.button.core.base.view.MainPagerAdapter
import com.panic.button.databinding.ActivitySkckBinding
import kotlinx.android.synthetic.main.activity_skck.*

class SkckActivity : MvvmActivity<ActivitySkckBinding, SkckViewModel>(SkckViewModel::class) {

    override val layoutResource: Int = R.layout.activity_skck

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        registerObserve()
        setupViewPager()
    }

    private fun registerObserve() {
        viewModel.isNextStep.observe(this, Observer {
            if (it == true) {
                viewPager.currentItem += 1
            }
        })
    }

    fun onNext() {
        viewModel.nextStep(viewPager.currentItem)
        hideKeyboard(this)
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == (viewModel.fragments.size - 1)) {
            super.onBackPressed()
        } else if (viewPager.currentItem > 0) {
            viewPager.currentItem -= 1
        } else {
            BaseDialogView(this).setMessage("Apakah anda yakin mau keluar dari form skck?")
                .showNegativeButton()
                .setOnClickNegative {  }
                .setOnClickPositive {
                    super.onBackPressed()
                }
                .show()
        }
    }

    private fun setupViewPager() {
        viewPager.offscreenPageLimit = viewModel.fragments.size
        val titles = resources.getStringArray(R.array.array_skck)
        viewPager.adapter = MainPagerAdapter(supportFragmentManager, viewModel.fragments, titles.toList())
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                viewModel.onChangeButtonText(position)
                onChangeStepView(position)
            }
        })
    }

    private fun onChangeStepView(position: Int) {
        if (position == 0) {
            setStepDashView(dataPribadiOval, dataPribadiLine)
        } else if (position == 1) {
            setStepFillView(dataPribadiOval, dataPribadiLine)
            setStepDashView(dataPasanganOval, dataPasanganLine)
        } else if (position == 2) {
            setStepFillView(dataPasanganOval, dataPasanganLine)
            setStepDashView(dataPendidikanOval, dataPendidikanLine)
        } else if (position == 3) {
            setStepFillView(dataPendidikanOval, dataPendidikanLine)
            setStepDashView(dataPelanggaranOval, dataPelanggaranLine)
        } else if (position == 4) {
            setStepFillView(dataPelanggaranOval, dataPelanggaranLine)
            setStepDashView(dataLainnyaOval, null)
        } else {
            setStepFillView(dataLainnyaOval, null)
        }
    }

    private fun setStepDashView(ovalImageView: AppCompatImageView?, lineImageView: AppCompatImageView?) {
        ovalImageView?.setImageResource(R.drawable.ic_border_red_dash)
        lineImageView?.setImageResource(R.drawable.ic_red_dash_line)
    }

    private fun setStepFillView(ovalImageView: AppCompatImageView?, lineImageView: AppCompatImageView?) {
        ovalImageView?.setImageResource(R.drawable.ic_circle)
        lineImageView?.setImageResource(R.drawable.bg_red_line)
    }

    companion object {
        fun onNewIntent(context: Context): Intent {
            return Intent(context, SkckActivity::class.java)
        }
    }
}