package com.panic.button.core.base

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.panic.button.R
import com.panic.button.core.api.GSONManager
import com.panic.button.core.api.PostRequest
import com.panic.button.core.api.log
import com.panic.button.core.api.response.LoginResponse
import com.panic.button.core.model.Urls
import com.panic.button.core.model.UserModel
import com.panic.button.feature.panic.PanicButtonActivity
import com.panic.button.feature.police.PoliceActivity
import com.panic.button.feature.police.StreamingPoliceActivity
import com.panic.button.feature.splash.SplashActivity
import org.json.JSONObject
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if (token != BaseApplication.sessionManager?.fcmToken) {
            BaseApplication.sessionManager?.fcmToken = token
            reLogin(token)
        }
    }

    override fun handleIntent(intent: Intent?) {
        intent?.extras?.apply {
            val data = HashMap<String, String>()
            data["scheme"] = getString("scheme") ?: ""
            data["body"] = getString("body") ?: ""
            data["title"] = getString("title") ?: ""
            data["locations"] = getString("locations") ?: ""

            if (!getString("title").isNullOrBlank() && !getString("body").isNullOrBlank()) {
                BaseApplication.context?.apply {
                    showNotification(data, this)
                }
            }
        }
        super.handleIntent(intent)
    }

    private fun reLogin(fcmToken: String) {
        BaseApplication.sessionManager?.loginParams.takeIf { !it.isNullOrEmpty() }?.apply {
            val userModel = GSONManager.fromJson(this, UserModel::class.java)
            userModel.fcmToken = fcmToken
            val isOfficer = BaseApplication.sessionManager?.isPolice == true
            val url : String
            if (isOfficer) {
                url = Urls.LOGIN_OFICER
            } else {
                url = Urls.LOGIN_CITIZEN
            }

            resetAccessToken()
            PostRequest<UserModel>(url).post(
                params = userModel,
                response = {
                    val loginResponse = GSONManager.fromJson(it, LoginResponse::class.java)
                    if (loginResponse.isSuccess == true) {
                        BaseApplication.sessionManager?.accessToken = loginResponse.loginModel?.authModel?.accessToken
                    }
                })
        }
    }

    private fun resetAccessToken() {
        BaseApplication.sessionManager?.accessToken = ""
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        BaseApplication.context?.apply {
            showNotification(remoteMessage.data, this)
        }
    }

    private fun showNotification(data: Map<String, String>?, context: Context) {
        data?.let { scheme ->
            val finalScheme = scheme["scheme"]

            log("scheme $finalScheme")
            log("data $scheme")

            val title = "${scheme["title"] ?: "Rajawali"}"
            val message = "${scheme["body"] ?: "Notification from Rajawali"}"
            val locations = scheme["locations"] ?: ""

            val notificationIntent = Intent()

            if (finalScheme?.contains("PanicRequest") == true) {
                val roomId = finalScheme.split("/").last()
                notificationIntent.setClass(context, StreamingPoliceActivity::class.java)

                var victimLat = 0.0
                var victimLong = 0.0

                try {
                    val jsonLocation = JSONObject(locations)
                    victimLat = jsonLocation.optDouble("lat")
                    victimLong = jsonLocation.optDouble("long")

                    log("jsonLocation $jsonLocation victimLat $victimLat victimLong $victimLong")
                } catch (exception : Exception) {
                    log("json locations error $exception")
                }

                notificationIntent.putExtra(StreamingPoliceActivity.EXTRA_ROOM_ID, roomId)
                notificationIntent.putExtra(StreamingPoliceActivity.EXTRA_LAT, "$victimLat")
                notificationIntent.putExtra(StreamingPoliceActivity.EXTRA_LONG, "$victimLong")

                BaseApplication.sessionManager?.roomsId = roomId
                BaseApplication.sessionManager?.latitudeVictim = "$victimLat"
                BaseApplication.sessionManager?.longitudeVictim = "$victimLong"

                sendBroadCastPolicyActivity(roomId)
            } else if (finalScheme?.contains("PanicCompleted") == true) {
                BaseApplication.sessionManager?.roomsId = ""
                notificationIntent.setClass(context, SplashActivity::class.java)
                sendBroadCastVictimActivity()
            } else {
                notificationIntent.setClass(context, SplashActivity::class.java)
            }

            val ranInt = Random.nextInt()

            val intentPending = PendingIntent.getActivity(
                context,
                ranInt,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val channelId = "Rajawali"
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(message)
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
                val channel = NotificationChannel(
                    channelId,
                    channelId,
                    NotificationManager.IMPORTANCE_HIGH
                )
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(channel)
                manager.notify(ranInt, notification)
            } else {
                val notificationManager = NotificationManagerCompat.from(this)
                notificationManager.notify(ranInt, notification)
            }

        } ?: run {
            log("FCM kosong")
        }
    }

    private fun sendBroadCastPolicyActivity(roomId: String?) {
        val intent = Intent(PoliceActivity.POLICE_DATA_RECEIVER)
        intent.putExtra(PoliceActivity.EXTRA_ROOM_ID, roomId)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun sendBroadCastVictimActivity() {
        val intent = Intent(PanicButtonActivity.VICTIM_DATA_RECEIVER)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}