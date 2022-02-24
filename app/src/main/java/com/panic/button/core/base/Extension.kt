package com.panic.button.core.base

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Vibrator
import android.text.Html
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.panic.button.BuildConfig
import com.panic.button.R
import com.panic.button.core.base.BaseApplication.Companion.showToast
import com.panic.button.core.model.Urls
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun hideKeyboard(context: Context) {
    val view = (context as Activity).currentFocus
    if (view != null) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun showAlert(context: Context?, message: String, duration : Int = Toast.LENGTH_SHORT) {
    context?.apply {
        val toast = Toast.makeText(this, message, duration)
        val view = View.inflate(this, R.layout.view_toast, null)
        view.findViewById<TextView>(R.id.textToast).text = message
        toast.view = view
        toast.show()
    }
}

fun String.isValidEmail(): Boolean = this.isNotEmpty() && this.isNotBlank() &&
        Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isNumber(): Boolean {
    return matches(Regex("^[0-9]+$"))
}

fun isDigitAndUpperLowerCaseWithSymbol(input: String): Boolean {
    val specialChars = "~`!@#$%^&*()-_=+\\|[{]};:'\",<.>/?"
    var currentCharacter: Char
    var numberPresent = false
    var upperCasePresent = false
    var lowerCasePresent = false
    var specialCharacterPresent = false
    for (i in 0 until input.length) {
        currentCharacter = input[i]
        if (Character.isDigit(currentCharacter)) {
            numberPresent = true
        } else if (Character.isUpperCase(currentCharacter)) {
            upperCasePresent = true
        } else if (Character.isLowerCase(currentCharacter)) {
            lowerCasePresent = true
        } else if (specialChars.contains(currentCharacter.toString())) {
            specialCharacterPresent = true
        }
    }
    return numberPresent && upperCasePresent && lowerCasePresent && specialCharacterPresent
}

fun isDigitAndUpperLowerCase(input: String): Boolean {
    var currentCharacter: Char
    var numberPresent = false
    var upperCasePresent = false
    var lowerCasePresent = false
    for (i in 0 until input.length) {
        currentCharacter = input[i]
        if (Character.isDigit(currentCharacter)) {
            numberPresent = true
        } else if (Character.isUpperCase(currentCharacter)) {
            upperCasePresent = true
        } else if (Character.isLowerCase(currentCharacter)) {
            lowerCasePresent = true
        }
    }
    return numberPresent && upperCasePresent && lowerCasePresent
}

fun ImageView.loadGlide(urlOri: String?, isSuccess: ((Boolean) -> Unit)? = null) {

    val url = urlOri ?: ""
    try {
        if (context != null && !(context as Activity).isFinishing) {
            Glide.with(this.context)
                    .load(url)
                    .apply(RequestOptions()/*.placeholder(R.color.red2)*/)
                    .addListener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            isSuccess?.let { it(false) }
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            isSuccess?.let { it(true) }
                            return false
                        }
                    }).into(this)
        }
    } catch ( e : Exception) { e.printStackTrace() }
}

@BindingAdapter("loadUrl")
fun ImageView.loadUrl(url: String?) {
    try {
        url?.let {
            Glide.with(context)
                .load(it)
                .apply(RequestOptions().placeholder(R.drawable.bg_grey_radius_4))
                .into(this)
        }
    } catch ( e : Exception) { e.printStackTrace() }
}

@BindingAdapter("loadUrlWithPlaceHolderJaktim")
fun ImageView.loadUrlWithPlaceHolderJaktim(url: String?) {
    url?.let {
        try {
            Glide.with(context)
                .load(it)
                .apply(RequestOptions().placeholder(R.drawable.bg_grey_radius_4))
                .into(this)
        } catch ( e : Exception) { e.printStackTrace() }
    }
}


@set:BindingAdapter("isVisible")
inline var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

fun replaceUrlJaktim(url: String?): String {
    var realUrl : String
    realUrl = url?.replace("http://jaktim.test", Urls.BASE_URL) ?: ""
    if (!realUrl.contains("http", ignoreCase = true)) {
        realUrl = "http:"+realUrl
    }
    return realUrl
}

fun String.isIndonesianMobilePhoneNumber(): Boolean {
    return this.matches(Regex("^[0][8][0-9]{6,13}\$")) || this.matches(Regex("^[6][2][0-9]{6,13}\$"))
}

fun Context.inflate(layoutId: Int, viewGroup: ViewGroup?): View {
    return LayoutInflater.from(this).inflate(layoutId, viewGroup, false)
}

fun Activity.getScreenInches(): Double {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    val widthPixels = displayMetrics.widthPixels
    val heightPixels = displayMetrics.heightPixels
    val calculateXdpi = widthPixels.toDouble() / displayMetrics.xdpi.toDouble()
    val calculateYdpi = heightPixels.toDouble() / displayMetrics.ydpi.toDouble()
    val xdpi = Math.pow(calculateXdpi, 2.0)
    val ydpi = Math.pow(calculateYdpi, 2.0)
    return Math.sqrt(xdpi + ydpi)
}


fun Activity.widthScreenSize(context: Context): Int {
    val metrics = DisplayMetrics()
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    wm.defaultDisplay.getMetrics(metrics)
    return metrics.widthPixels
}

fun Activity.heightScreenSize(): Int {
    val metrics = DisplayMetrics()
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    wm.defaultDisplay.getMetrics(metrics)
    return metrics.heightPixels
}

fun setVibrator(context: Context) {
    val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (v.hasVibrator()) {
        v.vibrate(1000)
    }
}

fun makeHandler(duration: Long, runnable: () -> Unit) {
    val handler = Handler()
    handler.postDelayed(runnable, duration)
}


fun Activity.pickDrawable(value: Int): Drawable? {
    return AppCompatResources.getDrawable(this, value)
}

fun getTodayDateString(format: String = "EEEE, dd MMMM yyyy", setupCalendar: Calendar? = null, locale : Locale = Locale("id")): String {
    val formatter = SimpleDateFormat(format, locale)

    var calendar = setupCalendar
    if (calendar == null) {
        calendar = Calendar.getInstance()
    }

    return formatter.format(calendar?.time ?: Date(System.currentTimeMillis()))
}

fun <T> ViewModel.mutableLiveDataOf() = MutableLiveData<T>()

object RemoteConfigHelper {
    @JvmStatic
    fun get(key: String): String {
        return BaseApplication.baseApplication?.remoteConfig?.getString(key) ?: ""
    }
    @JvmStatic
    fun getLong(key: String): Long {
        return BaseApplication.baseApplication?.remoteConfig?.getLong(key) ?: 0
    }
    @JvmStatic
    fun getBoolean(key: String): Boolean {
        return BaseApplication.baseApplication?.remoteConfig?.getBoolean(key) ?: false
    }
}

fun TextView.setHtmlText(text: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
    } else {
        this.text = Html.fromHtml(text)
    }
}

fun checkBatteryOptimization(context: Activity) { // sementara di komen
    /*val powerManager = context.getSystemService(AppCompatActivity.POWER_SERVICE) as PowerManager
    if (powerManager.isPowerSaveMode || !powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
        BaseDialogView(context).setMessage("Mohon matikan fitur optimasi baterai anda, untuk mendapatkan notifikasi dengan baik").setOnClickPositive {
            val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            context.startActivityForResult(intent, 9)
        }.showNegativeButton().show()
    }*/
}



@set:BindingAdapter("isInvisible")
inline var View.isInvisible: Boolean
    get() = visibility == View.INVISIBLE
    set(value) {
        visibility = if (value) View.INVISIBLE else View.VISIBLE
    }

val rajawaliPermission = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.ACCESS_FINE_LOCATION)

fun setRefreshRemoteConfig(callback: () -> Unit) {
    BaseApplication.baseApplication?.setupFirebaseRemoteConfig {
        BaseApplication.baseApplication?.setupFirebaseRemoteConfig {
            callback()
        }
    }
}

fun checkVersion(context : Context, callback: () -> Unit) {
    setRefreshRemoteConfig {
        val versionApps = RemoteConfigHelper.getLong(RemoteConfigKey.VERSION_APPS).toInt()
        val isForceUpdate = RemoteConfigHelper.getBoolean(RemoteConfigKey.IS_FORCE_UPDATE)
        if (versionApps > 0 && (BuildConfig.VERSION_CODE != versionApps)) {
            showUpdateAppsDialog(context, isForceUpdate)
        } else {
            callback()
        }
    }
}

fun showUpdateAppsDialog(context: Context, isForceUpdate : Boolean) {
    if (BaseApplication.updateAppsDialog?.dialog?.isShowing == false) {
        BaseApplication.updateAppsDialog?.dismiss()
    }

    if (BaseApplication.updateAppsDialog == null) {
        BaseApplication.updateAppsDialog = BaseDialogView(context)
            .setTitle("New version available")
            .setMessage("Please, update Rajawali apps to new version!")
            .setPositiveString("Yes")
            .setNegativeString("No")
            .setOnClickNegative {}
            .setOnClickPositive { openPlayStore(context) }
    }

    if (!isForceUpdate) {
        BaseApplication.updateAppsDialog?.showNegativeButton()
    }

    BaseApplication.updateAppsDialog?.show()
}

fun openPlayStore(context: Context) {
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=${context.packageName}")))
}

fun Context.pickFont(font : Int) : Typeface {
    return ResourcesCompat.getFont(this, font) ?: Typeface.DEFAULT
}

fun getDetailReportDevice() : String {
    val LINE_SEPARATOR = "\n"
    val errorReport = StringBuilder()
    errorReport.append("\n************ DEVICE INFORMATION ***********\n")
    errorReport.append("Brand: ")
    errorReport.append(Build.BRAND)
    errorReport.append(LINE_SEPARATOR)
    errorReport.append("Device: ")
    errorReport.append(Build.DEVICE)
    errorReport.append(LINE_SEPARATOR)
    errorReport.append("Model: ")
    errorReport.append(Build.MODEL)
    errorReport.append(LINE_SEPARATOR)
    errorReport.append("Id: ")
    errorReport.append(Build.ID)
    errorReport.append(LINE_SEPARATOR)
    errorReport.append("Product: ")
    errorReport.append(Build.PRODUCT)
    errorReport.append(LINE_SEPARATOR)
    errorReport.append("\n************ FIRMWARE ************\n")
    errorReport.append("SDK: ")
    errorReport.append(Build.VERSION.SDK)
    errorReport.append(LINE_SEPARATOR)
    errorReport.append("Release: ")
    errorReport.append(Build.VERSION.RELEASE)
    errorReport.append(LINE_SEPARATOR)
    errorReport.append("Incremental: ")
    errorReport.append(Build.VERSION.INCREMENTAL)
    errorReport.append(LINE_SEPARATOR)
    errorReport.append("Time: ")
    val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
    val cal = Calendar.getInstance()
    errorReport.append(dateFormat.format(cal.time))
    errorReport.append(LINE_SEPARATOR)
    errorReport.append("Version Application : ${BuildConfig.VERSION_CODE} ${BuildConfig.VERSION_NAME}")
    errorReport.append(LINE_SEPARATOR)
    return errorReport.toString()
}

fun isCovidOfficer() : Boolean {
    return BaseApplication.sessionManager?.role?.equals("covid-officer", ignoreCase = true) == true
}

fun createNewFile(context: Context, extension: String = ".jpg"): File {
    val formatter = SimpleDateFormat("dd_MMMM_yyyy_HH_mm_ss", Locale("id"))
    formatter.calendar = Calendar.getInstance()
    val fileName = formatter.format(Date()) + extension
    val directory = context.getDir("files", Context.MODE_PRIVATE)
    return File(directory, fileName)
}

fun convertBitmap(context: Context, bitmap: Bitmap, quality : Int = 50): Bitmap {
    val outStream = FileOutputStream(createNewFile(context))
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outStream)
    return bitmap
}


fun Context.openBrowser(url: String?) {
    val uri = Uri.parse(url ?: "")
    if (uri?.isAbsolute == true) {
        val builder = CustomTabsIntent.Builder()
        val colorBuilder = CustomTabColorSchemeParams.Builder()
        colorBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
        colorBuilder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
        colorBuilder.setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary))
        colorBuilder.setNavigationBarDividerColor(ContextCompat.getColor(this, R.color.colorPrimary))
        builder.setDefaultColorSchemeParams(colorBuilder.build())
        builder.setShowTitle(false)
        builder.setUrlBarHidingEnabled(false)
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, uri)
    } else {
        showToast("Url not valid!")
    }
}