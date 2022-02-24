package com.panic.button.feature.prayer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.hassanjamil.hqibla.CompassActivity
import com.hassanjamil.hqibla.Constants
import com.panic.button.BR
import com.panic.button.R
import com.panic.button.core.api.log
import com.panic.button.core.base.BaseApplication.Companion.sessionManager
import com.panic.button.core.base.MvvmActivity
import com.panic.button.core.base.PrayerScheduleReceiver
import com.panic.button.databinding.ActivityPrayerScheduleBinding
import kotlinx.android.synthetic.main.activity_prayer_schedule.*

class PrayerScheduleActivity : MvvmActivity<ActivityPrayerScheduleBinding, PrayerScheduleViewModel>(PrayerScheduleViewModel::class) {

    override val layoutResource: Int = R.layout.activity_prayer_schedule

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        viewModel.getSchedule()
        viewModel.lastScheduleName.observe(this, Observer {
            val messageSchedule = "${viewModel.remainingHours} Jam ${viewModel.remainingMinutes} Menit Lagi Masuk Waktu $it"
            messageTextView.text = messageSchedule
        })
        checkbox.setOnClickListener {
            checkPrayerScheduleAlarm()
            sessionManager?.isPrayerScheduleAlarm = checkbox.isChecked
            sessionManager?.prayerScheduleName = viewModel.lastScheduleName.value

            if (checkbox.isChecked) {
                startSchedule()
                Snackbar.make(backImageView, "Pengingat shalat diaktifkan", Snackbar.LENGTH_LONG).show()
            }
        }
        checkbox.isChecked = sessionManager?.isPrayerScheduleAlarm == true
    }

    private fun checkPrayerScheduleAlarm() {
        if (sessionManager?.isPrayerScheduleAlarm == true) {
            cancelPrayerSchedule()
            Snackbar.make(backImageView, "Pengingat shalat dimatikan", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun startSchedule() {
        val intent = Intent(this, PrayerScheduleReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, PRAYER_SCHEDULE_CODE, intent, 0)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var schedulePrayerMillis = 3600000L
        if (viewModel.remainingHours.toIntOrNull() == TWO_HOURS) {
            schedulePrayerMillis = 7200000
        } else if (viewModel.remainingHours.toIntOrNull() == THREE_HOURS) {
            schedulePrayerMillis = 10800000
        } else if (viewModel.remainingHours.toIntOrNull() == FOUR_HOURS) {
            schedulePrayerMillis = 14400000
        } else if (viewModel.remainingHours.toIntOrNull() == FIVE_HOURS) {
            schedulePrayerMillis = 18000000
        } else if (viewModel.remainingHours.toIntOrNull() == SIX_HOURS) {
            schedulePrayerMillis = 21600000
        } else if (viewModel.remainingHours.toIntOrNull() == SEVEN_HOURS) {
            schedulePrayerMillis = 25200000
        } else if (viewModel.remainingHours.toIntOrNull() == EIGHT_HOURS) {
            schedulePrayerMillis = 28800000
        } else if (viewModel.remainingHours.toIntOrNull() == NINE_HOURS) {
            schedulePrayerMillis = 32400000
        } else if (viewModel.remainingHours.toIntOrNull() == TEN_HOURS) {
            schedulePrayerMillis = 36000000
        }

        log("SystemClock.elapsedRealtime() "+SystemClock.elapsedRealtime())
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + schedulePrayerMillis, pendingIntent)
    }

    private fun cancelPrayerSchedule() {
        val intent = Intent(this, PrayerScheduleReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, PRAYER_SCHEDULE_CODE, intent, 0)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun onKiblat() {
        val intent = Intent(this, CompassActivity::class.java)
        intent.putExtra(Constants.TOOLBAR_TITLE, "Kiblat") // Toolbar Title
        intent.putExtra(Constants.TOOLBAR_BG_COLOR, "#111111") // Toolbar Background color
        intent.putExtra(Constants.TOOLBAR_TITLE_COLOR, "#FFFFFF") // Toolbar Title color
        intent.putExtra(Constants.COMPASS_BG_COLOR, "#FFFFFF") // Compass background color
        intent.putExtra(Constants.ANGLE_TEXT_COLOR, "#FFFFFF") // Angle Text color
        intent.putExtra(Constants.DRAWABLE_DIAL, R.drawable.dial) // Your dial drawable resource
        intent.putExtra(Constants.DRAWABLE_QIBLA, R.drawable.qibla) // Your qibla indicator drawable resource
        intent.putExtra(Constants.FOOTER_IMAGE_VISIBLE, View.VISIBLE ) // Footer World Image visibility
        intent.putExtra(Constants.LOCATION_TEXT_VISIBLE, View.VISIBLE) // Location Text visibility
        startActivity(intent)
    }

    companion object {

        const val PRAYER_SCHEDULE_CODE = 1253
        const val TWO_HOURS = 2
        const val THREE_HOURS = 3
        const val FOUR_HOURS = 4
        const val FIVE_HOURS = 5
        const val SIX_HOURS = 6
        const val SEVEN_HOURS = 7
        const val EIGHT_HOURS = 8
        const val NINE_HOURS = 9
        const val TEN_HOURS = 10

        fun onNewIntent(context: Context): Intent {
            return Intent(context, PrayerScheduleActivity::class.java)
        }
    }
}