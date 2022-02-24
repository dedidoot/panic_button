package com.panic.button.feature.covid

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.webkit.*
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.lifecycle.Observer
import com.panic.button.BR
import com.panic.button.R
import com.panic.button.core.api.log
import com.panic.button.core.base.*
import com.panic.button.core.base.view.HospitalView
import com.panic.button.core.base.view.TextArrowView
import com.panic.button.core.model.HospitalModel
import com.panic.button.databinding.ActivityCovidBinding
import kotlinx.android.synthetic.main.activity_covid.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import kotlin.coroutines.CoroutineContext

class CovidActivity : MvvmActivity<ActivityCovidBinding, CovidViewModel>(CovidViewModel::class), CoroutineScope, EasyPermissions.PermissionCallbacks {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private lateinit var subDistrictPopup: PopupWindow

    override val layoutResource: Int = R.layout.activity_covid

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        registerObserve()
        setupSubDistrictPopup()
        setupMapJaktim()
        viewModel.getSubDistrictJaktim()
        viewModel.getHospitals()
    }

    private fun setupMapJaktim() {
        launch {
            webView.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                heightScreenSize() / 3
            )
            // val htmlJaktim =
            //     "<div class=\"mapouter\"><div class=\"gmap_canvas\"><iframe width=\"400\" height=\"300\" id=\"gmap_canvas\" src=\"https://maps.google.com/maps?q=jakarta%20timur%20&t=&z=11&ie=UTF8&iwloc=&output=embed\" frameborder=\"0\" scrolling=\"no\" marginheight=\"0\" marginwidth=\"0\"></iframe></div></div>"
            val htmlJaktim =
                "<div class=\"mapouter\"><div class=\"gmap_canvas\"><iframe width=\"400\" height=\"300\" id=\"gmap_canvas\" src=\"https://maps.google.com/maps?q=jakarta%20timur%20&t=&z=11&ie=UTF8&iwloc=&output=embed\" frameborder=\"0\" scrolling=\"no\" marginheight=\"0\" marginwidth=\"0\"></iframe> <style>iframe {width:100%;height:100%;}</style> </div></div>"
            val webSettings = webView.settings
            webSettings.javaScriptEnabled = true
            webSettings.domStorageEnabled = true
            webSettings.useWideViewPort = true
            webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
            webView.settings.setAppCacheEnabled(false)
            webView.loadData(htmlJaktim, "text/html", null)
            webView.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    loading.isVisible = true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    loading.isVisible = false
                    Handler().postDelayed({
                        val x = webView.getMaxWidthScroll() / 2
                        val y = webView.getMaxHeightScroll() / 4
                        webView.scrollTo(x, y)
                    }, 3000)
                }
            }
        }
    }

    private fun registerObserve() {
        viewModel.subDistrictResponse.observe(this, Observer { response ->
            subDistrictPopup.clearAll()
            response?.apply {
                subDistrictPopup.addItems(this)
            }
        })

        viewModel.villageResponse.observe(this, Observer { models ->
            launch {
                kelurahanView.removeAllViews()
                var totalPositive = 0
                models?.forEach { model ->
                    val view = TextArrowView(this@CovidActivity)
                    view.tag = model.id
                    view.setText(model.name)
                    totalPositive += model.totalCovidPositive ?: 0
                    view.setEventClickVillageName {
                        val mapStyle = if (totalPositive >= 20) { MAP_STYLE_SATELLITE } else  { "" }
                        if (mapStyle == MAP_STYLE_SATELLITE) { showRedZoneDialog() }
                        loadMapWebView(query = "${model.name} $it", zoom = 15, style = mapStyle)
                        log("${model.name} $it")
                    }
                    view.setEventClick {
                        resetCollapseView()
                        if (it == 0) {
                            viewModel.getRtRw(model.id)
                        }
                        showExpandView(view)
                        val mapStyle = if (totalPositive >= 20) { MAP_STYLE_SATELLITE } else  { "" }
                        if (mapStyle == MAP_STYLE_SATELLITE) { showRedZoneDialog() }
                        loadMapWebView(query = model.name, style = mapStyle)
                    }
                    kelurahanView.addView(view)
                }
            }
        })

        viewModel.rtRwResponse.observe(this, Observer {
            for (index in 0 until kelurahanView.childCount) {
                (kelurahanView.getChildAt(index) as TextArrowView).apply {
                    if (tag == viewModel.currentVillageId) {
                        bindStats(it)
                    }
                }
            }
        })

        viewModel.hospitalResponse.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                setupHospitalView(it)
            }
        })
    }

    private fun showRedZoneDialog() {
        BaseDialogView(this).setMessage("Wilayah yang Anda pilih adalah zona merah, mohon berhati-hati!").setPositiveString("Oke").setOnClickPositive {  }.show()
    }

    private fun setupHospitalView(items: ArrayList<HospitalModel>) {
        launch {
            hospitalView.removeAllViews()
            items.forEach {
                val itemView = HospitalView(this@CovidActivity)
                itemView.setTitle(it.name)
                itemView.setDesc(it.phone)
                itemView.setClickCall {
                    openCall(it.phone)
                }
                hospitalView.addView(itemView)
            }
        }
    }

    private fun openCall(phone: String?) {
        if (phone?.isNumber() == true) {
            val perms = arrayOf(Manifest.permission.CALL_PHONE)
            if (EasyPermissions.hasPermissions(this, *perms)) {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:$phone")
                startActivity(intent)
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    "Aplikasi membutuhkan akses device, mohon izinkan terlebih dahulu",
                    0,
                    *perms
                )
            }
        } else {
            showAlert(this, "Mohon segera hubungi $phone")
        }
    }

    private fun showExpandView(currentView: TextArrowView) {
        currentView.expandView()
    }

    private fun resetCollapseView() {
        val count = kelurahanView.childCount
        for (index in 0 until count) {
            val view = kelurahanView[index] as TextArrowView
            view.collapseView()
        }
    }

    fun onSubDistrict() {
        subDistrictPopup.showPopup(subDistrictTextView)
    }

    private fun setupSubDistrictPopup() {
        subDistrictPopup = PopupWindow(this)
        subDistrictPopup.setEventListener {
            headerStatisticView.isVisible = true
            subDistrictTextView.text = it.name
            loadMapWebView(it.name)
            viewModel.getVillage(it.id)
        }
    }

    private fun loadMapWebView(query: String?, zoom: Int = 13, style: String = "") {
        val embeedMap =
            "<div class=\"mapouter\"><div class=\"gmap_canvas\"><iframe width=\"400\" height=\"300\" id=\"gmap_canvas\" src=\"https://maps.google.com/maps?q=jakarta timur ${query}&t=$style&z=$zoom&ie=UTF8&iwloc=&output=embed\" frameborder=\"0\" scrolling=\"no\" marginheight=\"0\" marginwidth=\"0\"></iframe> <style>iframe {width:100%;height:100%;}</style> </div></div>"
        webView.loadData(embeedMap, "text/html", null)
    }

    fun onCaseDataTabView() {
        caseDataTextView.typeface = pickFont(R.font.font_bold)
        caseDataLineView.isVisible = true
        covidView.isVisible = true

        hospitalTextView.typeface = pickFont(R.font.font_regular)
        hospitalLineView.isVisible = false
        hospitalView.isVisible = false
    }

    fun onHospitalTabView() {
        caseDataTextView.typeface = pickFont(R.font.font_regular)
        caseDataLineView.isVisible = false
        covidView.isVisible = false

        hospitalTextView.typeface = pickFont(R.font.font_bold)
        hospitalLineView.isVisible = true
        hospitalView.isVisible = true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {
        showAlert(this, "Fitur telepon sudah diizinkan, silahkan tekan kembali tombol call", Toast.LENGTH_LONG)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        log("onPermissionsDenied:" + requestCode + ":" + perms.size)
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this)
                .setTitle(getString(R.string.title_settings_dialog))
                .setRationale(getString(R.string.rationale_ask_again))
                .setPositiveButton("Pengaturan")
                .setNegativeButton("Batal")
                .setRequestCode(0)
                .build()
                .show()
        }
    }

    companion object {

        private const val MAP_STYLE_SATELLITE = "k"

        fun onStartActivity(context: Context) {
            val intent = Intent(context, CovidActivity::class.java)
            context.startActivity(intent)
        }
    }
}