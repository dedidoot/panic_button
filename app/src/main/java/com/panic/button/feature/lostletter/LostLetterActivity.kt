package com.panic.button.feature.lostletter

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.panic.button.BR
import com.panic.button.R
import com.panic.button.core.base.*
import com.panic.button.core.base.view.MainPagerAdapter
import com.panic.button.databinding.ActivityLostLetterBinding
import kotlinx.android.synthetic.main.activity_lost_letter.*

class LostLetterActivity : MvvmActivity<ActivityLostLetterBinding, LostLetterViewModel>(LostLetterViewModel::class) {

    override val layoutResource: Int = R.layout.activity_lost_letter

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        setupViewPager()
        viewModel.getLostLetterType()
        viewModel.isNextPage.observe(this, Observer {
            if (it == true) {
                lostLetterViewPager.currentItem += 1
            }
        })
    }

    private fun setupViewPager() {
        lostLetterViewPager.offscreenPageLimit = viewModel.fragments.size
        lostLetterViewPager.adapter = MainPagerAdapter(supportFragmentManager, viewModel.fragments, arrayListOf("type_lost_letter","field_lost_letter"))
        lostLetterViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
            }
        })
    }

    override fun onBackPressed() {
        if (lostLetterViewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            BaseDialogView(this)
                .setMessage("Yakin akan kembali ke form sebelumnya? Data yang Anda upload akan hilang.")
                .showNegativeButton()
                .setOnClickNegative {  }
                .setOnClickPositive {
                    lostLetterViewPager.currentItem -= 1
            }.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragments = supportFragmentManager.fragments

        for (fragment in fragments) {
            fragment?.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        fun onNewIntent(context: Context): Intent {
            return Intent(context, LostLetterActivity::class.java)
        }
    }
}