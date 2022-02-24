package com.panic.button.feature.police

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.panic.button.BR
import com.panic.button.R
import com.panic.button.core.api.GSONManager
import com.panic.button.core.api.PostRequest
import com.panic.button.core.api.log
import com.panic.button.core.base.*
import com.panic.button.core.base.location.RequestLocation
import com.panic.button.core.model.*
import com.panic.button.databinding.ActivityPoliceBinding
import kotlinx.android.synthetic.main.activity_police.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class PoliceActivity : MvvmActivity<ActivityPoliceBinding, PoliceViewModel>(PoliceViewModel::class) {

    private var roomId: String? = null

    override val layoutResource: Int = R.layout.activity_police

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        updateLocation()
        BaseApplication.sessionManager?.roomsId.takeIf { !it.isNullOrBlank() }?.let {
            openStreamingPolice()
        } ?: kotlin.run {
            hideVictim()
        }
    }

    private fun onEventJoined() {
        runOnUiThread {
            showVictim()
            setVibrator(this)
        }
    }

    fun onHistory(view: View) {
        startActivity(Intent(this, HistoryPoliceActivity::class.java))
    }

    fun onLogoutPolice(view: View) {
        loading.isVisible = true
        BaseApplication.sessionManager?.loginParams.takeIf { !it.isNullOrEmpty() }?.apply {
            val userModel = GSONManager.fromJson(this, UserModel::class.java)
            PostRequest<UserModel>(Urls.POST_OFFICER_LOGOUT).post(
                params = userModel,
                response = {
                    BaseApplication.baseApplication?.logout()
                    finish()
                })
        }
    }

    fun onAcceptReport(view: View) {
        roomId.takeIf { !it.isNullOrEmpty() }?.let {
            openStreamingPolice()
        } ?: kotlin.run {
            showAlert(this, "Room name not found")
        }
    }

    private fun openStreamingPolice(){
        val intent = Intent(this, StreamingPoliceActivity::class.java)
        intent.putExtra(StreamingPoliceActivity.EXTRA_ROOM_ID, BaseApplication.sessionManager?.roomsId)
        intent.putExtra(StreamingPoliceActivity.EXTRA_LAT, BaseApplication.sessionManager?.latitudeVictim)
        intent.putExtra(StreamingPoliceActivity.EXTRA_LONG, BaseApplication.sessionManager?.longitudeVictim)
        startActivity(intent)
        finish()
    }

    fun onSettingPolice(view: View) {
        if (menuView.visibility == View.VISIBLE) {
            menuView.visibility = View.GONE
        } else {
            menuView.visibility = View.VISIBLE
        }
    }

    fun hideVictim() {
        emptyTextView.visibility = View.VISIBLE
        ovalImageView.visibility = View.GONE
        ovalTransparent.visibility = View.GONE
        ovalSpinKitView.visibility = View.GONE
        helpTextView.visibility = View.GONE
        infoHelpTextView.visibility = View.GONE
    }

    fun showVictim() {
        emptyTextView.visibility = View.GONE
        ovalImageView.visibility = View.VISIBLE
        ovalTransparent.visibility = View.VISIBLE
        ovalSpinKitView.visibility = View.VISIBLE
        helpTextView.visibility = View.VISIBLE
        infoHelpTextView.visibility = View.VISIBLE
    }

    override fun onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver, IntentFilter(
                POLICE_DATA_RECEIVER
            )
        )
        super.onStart()
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        super.onStop()
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            roomId = intent.getStringExtra(EXTRA_ROOM_ID)
            log("roomId $roomId")
            onEventJoined()
        }
    }

    override fun onResume() {
        super.onResume()
        RequestLocation(this)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

        checkVersion(this) {}
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe
    fun onEvent(model: SubmitReportModel) {
        finish()
    }

    @Subscribe
    fun onEvent(model: LocationModel) {
        updateLocation()
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private fun updateLocation() {
        PostRequest<CallModel>(Urls.UPDATE_POSITION_OFFICER).post(
            params = CallModel(
                latitude = BaseApplication.sessionManager?.latitude,
                longitude = BaseApplication.sessionManager?.longitude
            ),
            response = {
            })
    }

    companion object {

        const val POLICE_DATA_RECEIVER = "police_data_receiver"
        const val EXTRA_ROOM_ID = "extra_room_name"

        fun onNewIntent(context: Context): Intent {
            return Intent(context, PoliceActivity::class.java)
        }
    }
}