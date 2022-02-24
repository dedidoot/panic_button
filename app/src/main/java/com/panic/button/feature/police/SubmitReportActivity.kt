package com.panic.button.feature.police

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.panic.button.R
import com.panic.button.core.api.GSONManager
import com.panic.button.core.api.PostRequest
import com.panic.button.core.api.response.BaseResponse
import com.panic.button.core.base.BaseApplication
import com.panic.button.core.base.hideKeyboard
import com.panic.button.core.base.isVisible
import com.panic.button.core.base.showAlert
import com.panic.button.core.model.SubmitReportModel
import com.panic.button.core.model.Urls
import kotlinx.android.synthetic.main.activity_submit_report.*

class SubmitReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_report)
        descEditText.setRawInputType(InputType.TYPE_CLASS_TEXT)
    }

    fun onBackReport(view: View) {
        finish()
    }

    fun onSend(view: View) {
        if (titleEditText.text.isNullOrBlank()) {
            showAlert(this, "Judul laporan tidak boleh kosong!")
            return
        }
        if (descEditText.text.isNullOrBlank()) {
            showAlert(this, "Deskripsi laporan tidak boleh kosong!")
            return
        }

        hideKeyboard(this)
        showLoading(true)

        PostRequest<SubmitReportModel>(Urls.SUBMIT_REPORT_FOR_PANIC).post(
            params = SubmitReportModel(BaseApplication.sessionManager?.roomsId, titleEditText.text.toString(), descEditText.text.toString()),
            response = {
                val response = GSONManager.fromJson(it, BaseResponse::class.java)
                showLoading(false)
                if (response.isSuccess()) {
                    startActivity(Intent(this, ReportSuccessActivity::class.java))
                    finish()
                } else {
                    showAlert(this, getMessage(it))
                }
            })
    }

    private fun showLoading(isLoading: Boolean) {
        loading.isVisible = isLoading
        button.isVisible = !isLoading
    }

    private fun getMessage(it: String): String {
        val regex = Regex("[^[A-Za-z ]]")
        return it.split("[").getOrNull(1)?.replace(regex,"") ?: "Gagal kirim laporan"
    }
}