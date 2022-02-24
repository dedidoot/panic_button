package com.panic.button.feature.home

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.snackbar.Snackbar
import com.panic.button.BR
import com.panic.button.R
import com.panic.button.core.api.*
import com.panic.button.core.base.*
import com.panic.button.core.base.isVisible
import com.panic.button.core.base.location.LocationHelper
import com.panic.button.core.base.location.RequestLocation
import com.panic.button.core.model.*
import com.panic.button.databinding.ActivityHomeBinding
import com.panic.button.feature.covid.CovidActivity
import com.panic.button.feature.location_interval.LocationIntervalReceiver
import com.panic.button.feature.lostletter.LostLetterActivity
import com.panic.button.feature.panic.PanicButtonActivity
import com.panic.button.feature.prayer.PrayerScheduleActivity
import com.panic.button.feature.registerprofile.RegisterProfileActivity
import com.panic.button.feature.reportpolice.ReportPoliceActivity
import com.panic.button.feature.ronda.RondaActivity
import com.panic.button.feature.shortcutbutton.CommandEventListener
import com.panic.button.feature.shortcutbutton.LockService
import com.panic.button.feature.shortcutbutton.ScreenEventListener
import com.panic.button.feature.shortcutbutton.ScreenReceiver
import com.panic.button.feature.skck.SkckActivity
import com.panic.button.feature.splash.SplashActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.bottom_sheet_menu.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

class HomeActivity : MvvmActivity<ActivityHomeBinding, HomeViewModel>(HomeViewModel::class), EasyPermissions.PermissionCallbacks {

    private var countClickPanicButton = 1
    private var pager: Pager? = null
    private var bullets: ArrayList<ImageView> = arrayListOf()
    private var ovalUnselectedNews: Drawable? = null
    private var ovalSelectedNews: Drawable? = null
    private val mReceiver = ScreenReceiver()
    private var shortcutButtonCount = 1
    private var isRunningVoiceRecord = false
    private var isPressPowerButton = false
    private var callId = ""
    private var attentionPanicButtonDialog : BaseDialogView? = null

    override val layoutResource: Int = R.layout.activity_home

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this

        setHeightBottomMenuDefault()
        ovalUnselectedNews = ContextCompat.getDrawable(this, R.drawable.bg_oval_unselected_news)
        ovalSelectedNews = ContextCompat.getDrawable(this, R.drawable.bg_oval_selected_news)
        observeNews()
        setupNewsViewPager()

        logoutView.setOnClickListener {
            victimLogout()
        }

        covidView.setOnClickListener {
            CovidActivity.onStartActivity(this)
        }

        settingView.setOnClickListener {
            startActivity(RegisterProfileActivity.onNewIntent(this, isFromEditProfile = true))
        }

        prayerScheduleView.setOnClickListener {
            startActivity(PrayerScheduleActivity.onNewIntent(this))
        }

        skckView.setOnClickListener(SingleClickListener().setSingleClickListener {
            checkSkckStatus()
        })

        rondaView.setOnClickListener {
            startActivity(RondaActivity.onNewIntent(this))
        }

        suratKehilangan.setOnClickListener(SingleClickListener().setSingleClickListener {
            checkLostLetter()
        })

        reportPolice.setOnClickListener {
            openCallPhone()
        }

        lineView.setOnClickListener {
            actionClickBottomMenu()
        }

        LocationHelper.showRequestPermission(this)

        sirineImage.setOnClickListener(openPanic())
        callPolice.setOnClickListener(openPanic())
        infoTapTextView.setOnClickListener(openPanic())
        viewModel.getNews()

        getProfile()
        startSchedule()
        setupSwipeRefreshLayout()
        startLocationInterval()
    }

    private fun victimLogout() {
        swipeRefreshLayout.isRefreshing = true
        BaseApplication.sessionManager?.loginParams.takeIf { !it.isNullOrEmpty() }?.apply {
            val userModel = GSONManager.fromJson(this, UserModel::class.java)
            PostRequest<UserModel>(Urls.POST_CITIZEN_LOGOUT).post(
                params = userModel,
                response = {
                    unregisterReceiver(mReceiver)
                    BaseApplication.baseApplication?.logout()
                    finish()
                })
        }
    }

    private fun checkLostLetter() {
        setIsSwipeRefreshLoading(true)
        setRefreshRemoteConfig {
            setIsSwipeRefreshLoading(false)
            val isLostLetterWebVersion = RemoteConfigHelper.getBoolean(RemoteConfigKey.IS_LOST_LETTER_WEB_VERSION)
            val isLostLetterAppsVersion = RemoteConfigHelper.getBoolean(RemoteConfigKey.IS_LOST_LETTER_APPS_VERSION)
            if (isLostLetterWebVersion) {
                RajawaliWebView.startActivity(
                    this,
                    RemoteConfigHelper.get(RemoteConfigKey.LOST_LETTER_URL),
                    "Surat Kehilangan"
                )
            } else if (isLostLetterAppsVersion) {
                startActivity(LostLetterActivity.onNewIntent(this))
            } else {
                showMaintenaceDialog()
            }
        }
    }

    private fun setupSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(
            R.color.red,
            R.color.red,
            R.color.red,
            R.color.red
        )

        swipeRefreshLayout.setOnRefreshListener {
            setRefreshRemoteConfig {
                setIsSwipeRefreshLoading(false)
            }
        }
    }

    private fun setIsSwipeRefreshLoading(isSwipeRefresh: Boolean) {
        swipeRefreshLayout.isRefreshing = isSwipeRefresh
    }

    private fun checkSkckStatus() {
        setIsSwipeRefreshLoading(true)
        setRefreshRemoteConfig {
            setIsSwipeRefreshLoading(false)
            val isSkckWebVersion = RemoteConfigHelper.getBoolean(RemoteConfigKey.IS_SKCK_WEB_VERSION)
            val isSkckAppsVersion = RemoteConfigHelper.getBoolean(RemoteConfigKey.IS_SKCK_APPS_VERSION)
            if (isSkckWebVersion) {
                RajawaliWebView.startActivity(
                    this,
                    RemoteConfigHelper.get(RemoteConfigKey.SKCK_URL),
                    "SKCK"
                )
            } else if (isSkckAppsVersion) {
                startActivity(SkckActivity.onNewIntent(this))
            } else {
                showMaintenaceDialog()
            }
        }
    }

    private fun setRefreshRemoteConfig(callback: () -> Unit) {
        BaseApplication.baseApplication?.setupFirebaseRemoteConfig {
            BaseApplication.baseApplication?.setupFirebaseRemoteConfig {
                callback()
            }
        }
    }

    private fun showMaintenaceDialog() {
        BaseDialogView(this)
            .setMessage("Fitur ini sedang dalam perbaikan, mohon maaf!")
            .setPositiveString("Oke")
            .setOnClickPositive {  }
            .show()
    }

    private fun openCallPhone() {
        startActivity(Intent(this, ReportPoliceActivity::class.java))
    }

    private fun observeNews() {
        viewModel.newsResponse.observe(this, Observer {
            it?.apply {
                setUpBullet()
            }
        })
    }

    private fun setupNewsViewPager() {
        pager = Pager(this)
        newsViewPager.adapter = pager

        newsViewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                bullets.forEach {
                    it.setImageDrawable(ovalUnselectedNews)
                }
                bullets.getOrNull(position)?.setImageDrawable(ovalSelectedNews)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun setUpBullet() {
        newsLinearLayout.removeAllViews()
        bullets.clear()

        val items = viewModel.getNewsModel()
        items.forEachIndexed { index, _ ->
            newsLinearLayout.post {
                val newBullet = ImageView(this)
                if (index == 0) {
                    newBullet.setImageDrawable(ovalSelectedNews)
                } else {
                    newBullet.setImageDrawable(ovalUnselectedNews)
                }
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(resources.getDimensionPixelSize(R.dimen._4sdp), 0, 0, 0)
                newsLinearLayout.addView(newBullet, params)
                bullets.add(newBullet)
            }
        }
        newsViewPager.isVisible = items.size > 1
        bullets.firstOrNull()?.setImageDrawable(ovalSelectedNews)
        pager?.setItems(items)
    }

    private fun openPanic(): View.OnClickListener? {
        return View.OnClickListener {
            checkPermission()
        }
    }

    private fun actionClickBottomMenu() {
        if (menuItemView.layoutParams?.height == FrameLayout.LayoutParams.WRAP_CONTENT) {
            setHeightBottomMenuDefault()
        } else {
            menuItemView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            newsViewPager.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(
                    R.dimen._45sdp
                )
            )
            newsLinearLayout.isVisible = false
            setParsingNewsModelForHideOrShowImageView(false)
        }
    }

    private fun setParsingNewsModelForHideOrShowImageView(isShow: Boolean) {
        val parseNewsForHideImageView = viewModel.getNewsModel()
        parseNewsForHideImageView.forEach {
            it.isShowImageView = isShow
        }
        pager = Pager(this)
        newsViewPager.adapter = pager
        pager?.setItems(parseNewsForHideImageView)
    }

    private fun setHeightBottomMenuDefault() {
        val metrics = resources.displayMetrics
        val calculate = metrics.heightPixels / getScreenInches().toInt()
        menuItemView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, calculate + resources.getDimensionPixelSize(
                R.dimen._20sdp
            )
        )
        newsViewPager.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(
                R.dimen._150sdp
            )
        )
        newsLinearLayout.isVisible = true
        setParsingNewsModelForHideOrShowImageView(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == RequestLocation.LOCATION_CODE) {
            PermissionHelper.checkPermissionStorageAndCamera(this)
        } else if (resultCode == Activity.RESULT_CANCELED && (requestCode == RequestLocation.LOCATION_CODE || requestCode == CHECK_PERMISSION_CODE)) {
            showAlert(this, "Akses perangkat tidak diizinkan")
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LocationHelper.KEY_REQUEST_FINE_LOCATION -> {
                for (i in permissions.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        showAlert(this, "Mohon izinkan aplikasi mengakses fitur gps!")
                        finish()
                        break
                    }
                }
                RequestLocation(this)
                PermissionHelper.checkPermissionStorageAndCamera(this)
            }
            PermissionHelper.PERMISSION_STORAGE_AND_CAMERA -> {
                for (i in permissions.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        BaseApplication.showToast("Fitur tidak diizinkan")
                        finish()
                        break
                    }
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        log("onPermissionsDenied:" + requestCode + ":" + perms.size)
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this)
                .setTitle(getString(R.string.title_settings_dialog))
                .setRationale(getString(R.string.rationale_ask_again))
                .setPositiveButton("Pengaturan")
                .setNegativeButton("Batal")
                .setRequestCode(requestCallPhoneCode)
                .build()
                .show()
        }
    }

    @AfterPermissionGranted(CHECK_PERMISSION_CODE)
    private fun checkPermission() {
        val perms = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(
                this, "Aplikasi membutuhkan akses device, mohon izinkan terlebih dahulu",
                CHECK_PERMISSION_CODE, *perms
            )
        } else {
            if (countClickPanicButton == 3) {
                startActivity(PanicButtonActivity.onNewIntent(this))
                countClickPanicButton = 1
            } else {
                Snackbar.make(
                    rootView,
                    "Panic button click $countClickPanicButton",
                    Snackbar.LENGTH_SHORT
                ).show()
                countClickPanicButton += 1
            }
        }
    }

    private fun getProfile() {
        GetRequest(Urls.CITIZEN_PROFILE).get({
        })
    }

    private fun startSchedule() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            return
        }
        stopSchedule()
        startService(Intent(this, LockService::class.java))
    }

    private fun stopSchedule() {
        stopService(Intent(this, LockService::class.java))
    }

    override fun onResume() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

        checkAttentionPanicButtonDialog()

        checkVersion(this) {}
        startRecordLocation()
        super.onResume()
    }

    private fun checkAttentionPanicButtonDialog() {
        if (attentionPanicButtonDialog?.dialog?.isShowing == true) {
            attentionPanicButtonDialog?.dismiss()
        }

        if (BaseApplication.sessionManager?.roomsId.isNullOrBlank()) {
            isRunningVoiceRecord = false
            callId = ""
        }

        if (isRunningVoiceRecord || callId.isNotBlank() || !BaseApplication.sessionManager?.roomsId.isNullOrBlank()) {
            if (attentionPanicButtonDialog == null) {
                attentionPanicButtonDialog = BaseDialogView(this)
            }
            attentionPanicButtonDialog?.setMessage("Panic button masih berjalan, Apakah anda mau melanjutkan?")
                ?.showNegativeButton()?.setNegativeString("Tidak")?.setOnClickNegative {
                    BaseApplication.sessionManager?.roomsId = ""
                    stopSchedule()
                    unregisterReceiver(mReceiver)
                    BaseApplication.recorderVoice?.stopRecord()
                    val intent = Intent(this, SplashActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
                ?.setPositiveString("Lanjutkan")?.setOnClickPositive {
                    startActivity(PanicButtonActivity.onNewIntent(this))
                }
            if (attentionPanicButtonDialog?.dialog?.isShowing == false) {
                attentionPanicButtonDialog?.show()
            }
        }
    }

    @Subscribe
    fun screenEventListener(model: ScreenEventListener?) {
        log("Subscribe event bus ScreenEventListener $shortcutButtonCount")

        if (!isPressPowerButton) {
            isPressPowerButton = true
            makeHandler(10000) {
                isPressPowerButton = false
                shortcutButtonCount = 1
            }
        }

        if (shortcutButtonCount == 5 && isPressPowerButton && !isRunningVoiceRecord) {
            isRunningVoiceRecord = true
            makeCall()
            log("start...")
        }

        shortcutButtonCount += 1
    }

    private fun makeCall() {
        PostRequest<RegisterModel>(Urls.MAKE_CALL).post(
            params = RegisterModel(
                latitude = BaseApplication.sessionManager?.latitude?.toDoubleOrNull() ?: 0.0,
                longitude = BaseApplication.sessionManager?.longitude?.toDoubleOrNull() ?: 0.0
            ),
            response = {
                val response = GSONManager.fromJson(it, CallResponse::class.java)
                callId = response.data?.id ?: ""
                startCall()
            })
    }

    private fun startCall() {
        PostRequest<CallModel>(Urls.REQUEST_CONNECT).post(
            params = CallModel(
                callId = callId,
                latitude = BaseApplication.sessionManager?.latitude,
                longitude = BaseApplication.sessionManager?.longitude
            ),
            response = {
                val response = GSONManager.fromJson(it, CallResponse::class.java)
                callId = response.data?.id ?: ""
                BaseApplication.sessionManager?.roomsId = callId
                setupVoiceRecord()
                startRecordLocation()
            })
    }

    private fun startRecordLocation() {
        RequestLocation(this)
        makeHandler(90000) {
            updateLocation()
        }
    }

    private fun updateLocation() {
        val model = CallModel(
            latitude = BaseApplication.sessionManager?.latitude,
            longitude = BaseApplication.sessionManager?.longitude
        )
        PostRequest<CallModel>(Urls.UPDATE_POSITION_CITIZEN).post(
            params = model,
            response = {
            })
    }

    private fun setupVoiceRecord() {
        BaseApplication.recorderVoice = RecorderVoiceHelper(this)
        BaseApplication.recorderVoice?.startRecord()
        makeHandler(RecorderVoiceHelper.RECORD_VOICE_DURATION.toLong()) {
            BaseApplication.recorderVoice?.stopRecord()
            UploadRequest(Urls.UPLOAD_PANIC_MANUAL).uploadFilePanicButton(
                BaseApplication.recorderVoice?.file!!,
                callId,
                "audio-manual",
                {
                    log("response upload $it")
                    setupVoiceRecord()
                })
        }
    }

    @Subscribe
    fun commandEventListener(model: CommandEventListener?) {
        log("Subscribe event bus CommandEventListener")
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        //filter.addAction(Intent.ACTION_SCREEN_OFF);
        //filter.addAction(Intent.ACTION_USER_PRESENT);

        try {
            try {
                registerReceiver(mReceiver, filter)
            } catch (e: Exception) {
                log("error x1 $e")
            }
        } catch (e: Exception) {
            log("error x2 $e")
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private fun startLocationInterval() {
        cancelLocationInterval()
        val intent = Intent(this, LocationIntervalReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, LOCATION_INTERVAL, intent, 0)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
            3600000, 216000000, pendingIntent) //1 jam //1jam
    }

    private fun cancelLocationInterval() {
        val intent = Intent(this, LocationIntervalReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, LOCATION_INTERVAL, intent, 0)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    companion object {
        private const val requestCallPhoneCode = 103
        private const val LOCATION_INTERVAL = 392
        const val CHECK_PERMISSION_CODE = 2
    }
}