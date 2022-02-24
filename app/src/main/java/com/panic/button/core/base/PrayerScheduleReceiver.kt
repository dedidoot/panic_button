package com.panic.button.core.base

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.panic.button.R
import com.panic.button.core.base.BaseApplication.Companion.sessionManager
import com.panic.button.feature.splash.SplashActivity
import kotlin.random.Random

class PrayerScheduleReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, dataIntent: Intent?) {
        showNotification(context)
    }

    private fun showNotification(context: Context) {
        val notificationIntent = Intent()
        notificationIntent.setClass(context, SplashActivity::class.java)

        val ranInt = Random.nextInt()
        val intentPending = PendingIntent.getActivity(context, ranInt, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val channelId = "Rajawali"
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Waktunya untuk shalat ${sessionManager?.prayerScheduleName}!")
            .setContentText("Selamat menunikan ibadah shalat ${sessionManager?.prayerScheduleName}.")
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setSound(soundUri, AudioManager.STREAM_ALARM)
            .setLights(Color.RED, 3000, 3000)
            .setContentIntent(intentPending)
            .setSmallIcon(R.drawable.ic_notifications_red_24dp)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        setVibrator(context)

        val notification = notificationBuilder.build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH)
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(ranInt, notification)

        sessionManager?.isPrayerScheduleAlarm = false
    }
}