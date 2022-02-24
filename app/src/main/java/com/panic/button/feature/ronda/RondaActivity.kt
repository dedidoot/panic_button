package com.panic.button.feature.ronda

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import com.panic.button.BR
import com.panic.button.R
import com.panic.button.core.api.isVisible
import com.panic.button.core.base.*
import com.panic.button.databinding.ActivityRondaBinding
import kotlinx.android.synthetic.main.activity_ronda.*

class RondaActivity : MvvmActivity<ActivityRondaBinding, RondaViewModel>(RondaViewModel::class) {

    override val layoutResource: Int = R.layout.activity_ronda

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        viewModel.isSuccess.observe(this, Observer {
            BaseDialogView(this)
                .setMessage("Laporan ronda anda berhasil terkirim")
                .setPositiveString("Oke")
                .setOnClickPositive {
                    finish()
                }
                .show()
        })

        viewModel.getCitizenPatrol()

        viewModel.isPermitted.observe(this, Observer {
            if (it == true) {

            } else {
                BaseDialogView(this)
                    .setPositiveString("Ok")
                    .setMessage(viewModel.messages.value ?: "")
                    .setOnClickPositive {
                        finish()
                    }
                    .show()
            }
            scrollView.isVisible = it == true
            button.isVisible = it == true
        })

        viewModel.videoName.observe(this, Observer {
            videoNameTextView.text = ""
            videoNameTextView.isVisible = true
            it?.forEachIndexed { index, videoName ->
                videoNameTextView.append("${index + 1} . $videoName\n")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GalleryHelper.ARG_VIDEO && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                viewModel.uploadVideo(FileUtil.from(this, it))
            } ?: kotlin.run {
                showAlert(this, "Video tidak ditemukan")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun onVideo() {
        if (viewModel.getCountNameFiles() < 4) {
            GalleryHelper(this).showVideo()
        } else {
            showAlert(this, "Maksimal upload 4 video")
        }
    }

    fun onSave() {
        viewModel.onSave()
    }

    companion object {
        fun onNewIntent(context: Context): Intent {
            return Intent(context, RondaActivity::class.java)
        }
    }
}