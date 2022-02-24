package com.panic.button.core.base.camera

import android.content.Context
import com.panic.button.R
import com.panic.button.core.base.BaseApplication
import com.panic.button.core.base.RemoteConfigHelper
import com.panic.button.core.base.RemoteConfigKey
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun createNewFile(folder: File, format: String, fileExtension: String) =
    File(folder, SimpleDateFormat(format, Locale.US).format(System.currentTimeMillis()) + fileExtension)

fun getOutputDirectory(context: Context): File {
  val appContext = context.applicationContext
  val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
    File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
  }
  return if (mediaDir != null && mediaDir.exists())
    mediaDir
  else
    appContext.filesDir
}

fun getPathVideoFileName(context: Context): File {
  val formatter = SimpleDateFormat("dd_MMMM_yyyy_HH_mm_ss", Locale("id"))
  formatter.calendar = Calendar.getInstance()
  val username = BaseApplication.sessionManager?.fullName?.replace(" ", "_")
  val fileName = username + "_" + formatter.format(Date()) + RemoteConfigHelper.get(
    RemoteConfigKey.EXTENISON_VIDEO_RECORD
  )
  val directory = context.getDir("recordku", Context.MODE_PRIVATE)
  return File(directory, fileName)
}
