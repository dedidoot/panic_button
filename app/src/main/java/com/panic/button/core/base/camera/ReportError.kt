package com.panic.button.core.base.camera

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Process
import android.widget.Toast
import com.panic.button.core.api.ReportRequest
import com.panic.button.core.api.log
import com.panic.button.core.base.SessionManager
import com.panic.button.core.base.showAlert
import com.panic.button.feature.splash.SplashActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.PrintWriter
import java.io.StringWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class ReportError(private val myContext: Activity) : Thread.UncaughtExceptionHandler ,
    CoroutineScope {

    override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main

    private val LINE_SEPARATOR = "\n"
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        val sessionManager = SessionManager(myContext)

        showAlert(myContext, "Saving panic button", Toast.LENGTH_SHORT)
        val stackTrace = StringWriter()
        ex.printStackTrace(PrintWriter(stackTrace))
        val errorReport = StringBuilder()
        errorReport.append("************ WHAT THE HELL ************\n\n")
        errorReport.append(stackTrace.toString())
        errorReport.append(LINE_SEPARATOR)
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

        //Jika Aplikasi sudah siap untuk LIVE, Intent dibawah ini di Uncomment yak.
        val intent = Intent(myContext, SplashActivity::class.java)
        intent.putExtra(EXTRA_ERROR, errorReport.toString())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        log("nah $errorReport")
        launch { ReportRequest.post("$errorReport") }
        myContext.startActivity(intent)
        Process.killProcess(Process.myPid())
        System.exit(10)
    }

    companion object {
        const val EXTRA_ERROR = "error"
    }
}