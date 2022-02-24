package com.panic.button.feature.police

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.panic.button.R
import com.panic.button.core.api.GSONManager
import com.panic.button.core.api.PostRequest
import com.panic.button.core.api.log
import com.panic.button.core.api.response.BaseResponse
import com.panic.button.core.base.BaseApplication
import com.panic.button.core.base.BaseDialogView
import com.panic.button.core.base.location.LocationHelper
import com.panic.button.core.base.location.RequestLocation
import com.panic.button.core.base.view.DrawMapHelper
import com.panic.button.core.base.view.TouchableWrapper
import com.panic.button.core.model.CallModel
import com.panic.button.core.model.SubmitReportModel
import com.panic.button.core.model.Urls
import com.panic.button.feature.splash.SplashActivity
import kotlinx.android.synthetic.main.activity_streaming_police.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class StreamingPoliceActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener,
    ConnectionCallbacks, OnConnectionFailedListener,
    GoogleMap.OnMapClickListener, TouchableWrapper.TouchAction,
    EasyPermissions.PermissionCallbacks {

    private var mMap: GoogleMap? = null
    private var googleApiClient: GoogleApiClient? = null
    private var currentLocation: Location? = null
    private var drawMapHelper: DrawMapHelper? = null
    private var isUpdateCamera = false
    private var latVictim: Double = 0.0
    private var longVictim: Double = 0.0
    private val perms = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streaming_police)

        intent?.getStringExtra(EXTRA_ROOM_ID).takeIf { !it.isNullOrBlank() }?.apply {
            latVictim = intent?.getStringExtra(EXTRA_LAT)?.toDoubleOrNull() ?: 0.0
            longVictim = intent?.getStringExtra(EXTRA_LONG)?.toDoubleOrNull() ?: 0.0
            log("latVictim $latVictim")
            log("longVictim $longVictim")
            BaseApplication.sessionManager?.roomsId = intent?.getStringExtra(EXTRA_ROOM_ID)
            buildGoogleApiClient()
            setupGoogleMap()
            acceptCall()
            clearNotification()
        } ?: kotlin.run {
            BaseDialogView(this).setMessage("Room id tidak di temukan")
                .setOnClickPositive {
                    finish()
                }.show()
        }
    }

    private fun clearNotification() {
        val notification = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notification.cancelAll()
    }

    private fun acceptCall() {
        PostRequest<SubmitReportModel>(Urls.ACCEPT_CALL).post(
            params = SubmitReportModel(BaseApplication.sessionManager?.roomsId),
            response = {
                val model = GSONManager.fromJson(it, BaseResponse::class.java)
                if (!model.isSuccess()) {
                    showRoomExpired()
                }
            })
    }

    private fun showRoomExpired() {
        BaseDialogView(this).setMessage("Room id sudah kedaluwarsa.").setOnClickPositive {
            startActivity(Intent(this, SplashActivity::class.java))
            BaseApplication.sessionManager?.resetCallVictim()
            finish()
        }.setPositiveString("Oke").show()
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

    override fun onDestroy() {
        if (googleApiClient?.isConnected == true) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient) {
                log("removed")
            }
            googleApiClient?.disconnect()
        }

        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    private fun buildGoogleApiClient() {
        googleApiClient = LocationHelper.generateGoogleClient(this, this, this)
        googleApiClient?.connect()
    }

    private fun setupGoogleMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.victimMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        drawMapHelper = DrawMapHelper(this, mMap)
        drawMapHelper?.setLocationVictim(LatLng(latVictim, longVictim))
        googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap?.isBuildingsEnabled = true
        googleMap?.isTrafficEnabled = true
        // googleMap?.isMyLocationEnabled = true
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap?.uiSettings?.isZoomGesturesEnabled = true
        googleMap?.uiSettings?.isMyLocationButtonEnabled = true
        googleMap?.setOnCameraMoveStartedListener(this)
        googleMap?.setOnCameraIdleListener(this)
        //googleMap?.setOnMapClickListener(this)
    }

    private fun updateCurrentLocationMoveCamera() {
        currentLocation?.takeIf { it.latitude != 0.0 && it.longitude != 0.0 }?.apply {
            val latLng = LatLng(latitude, longitude)
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
        } ?: kotlin.run {
            updatingCurrentLocation()
        }
    }

    private fun updatingCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        val lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        if (lastLocation?.latitude != 0.0 && lastLocation?.longitude != 0.0) {
            currentLocation = lastLocation
            updateCurrentLocationMoveCamera()
        }
    }

    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
            googleApiClient,
            LocationHelper.generateLocationRequest()
        ) { location ->
            currentLocation = location
            if (!isUpdateCamera) { 
                updateCurrentLocationMoveCamera()
                isUpdateCamera = true
                drawMapHelper?.customDraw(currentLocation)
            }

            BaseApplication.sessionManager?.latitude = "${currentLocation?.latitude}"
            BaseApplication.sessionManager?.longitude = "${currentLocation?.longitude}"
            log("changed ${currentLocation?.latitude} ${currentLocation?.longitude}")
            updateLocation()
        }
    }

    override fun onCameraMoveStarted(p0: Int) {}

    override fun onCameraIdle() {}

    override fun onConnected(bundle: Bundle?) {
        Log.e("onConnected", "onConnected")
        requestLocationUpdates()
    }

    override fun onConnectionSuspended(p0: Int) {
        googleApiClient?.connect()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {}

    override fun onMapClick(latLong: LatLng?) {
        // Already two locations
        drawMapHelper?.mMarkerPoints?.takeIf { it.size > 1 }?.apply {
            drawMapHelper?.mMarkerPoints?.clear()
            mMap?.clear()
        }

        // Adding new item to the ArrayList
        drawMapHelper?.mMarkerPoints?.add(latLong)

        // Creating MarkerOptions
        val options = MarkerOptions()

        // Setting the position of the marker
        options.position(latLong ?: LatLng(0.0, 0.0))

        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */
        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */
        if (drawMapHelper?.mMarkerPoints?.size == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        } else if (drawMapHelper?.mMarkerPoints?.size == 2) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        }

        // Add new marker to the Google Map Android API V2
        mMap?.addMarker(options)

        // Checks, whether start and end locations are captured
        drawMapHelper?.mMarkerPoints?.takeIf { it.size >= 2 }?.apply {
            drawMapHelper?.mOrigin = drawMapHelper?.mMarkerPoints?.get(0)
            drawMapHelper?.mDestination = drawMapHelper?.mMarkerPoints?.get(1)
            drawMapHelper?.drawRoute()
        }
    }

    override fun onTouchUp() {
        if (!policeStreamingScrollView.isScrollable) {
            policeStreamingScrollView.setScrollingEnabled(true)
        }
    }

    override fun onTouchDown() {
        if (policeStreamingScrollView.isScrollable) {
            policeStreamingScrollView.setScrollingEnabled(false)
        }
    }

    override fun onTouchMove() {
        if (policeStreamingScrollView.isScrollable) {
            policeStreamingScrollView.setScrollingEnabled(false)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPermissionsGranted(requestCode: Int, permissions: List<String>) {
        permissions.forEach {
            log("cek " + it)

        }
        permissions.find { it.equals(Manifest.permission.ACCESS_FINE_LOCATION, ignoreCase = true) }
            ?.apply {
                RequestLocation(this@StreamingPoliceActivity)
            }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        log("onPermissionsDenied:" + requestCode + ":" + perms.size)
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this)
                .setTitle(getString(R.string.title_settings_dialog))
                .setRationale(getString(R.string.rationale_ask_again))
                .setPositiveButton("Pengaturan")
                .setNegativeButton("Batal")
                .setRequestCode(RC_SETTINGS_SCREEN_PERM)
                .build()
                .show()
        } else {
            recheckLocationPermission()
        }
    }

    fun continueReport(view: View) {
        startActivity(Intent(this, SubmitReportActivity::class.java))
    }

    @Subscribe
    fun onEvent(model: SubmitReportModel) {
        if (model.callId?.equals(SubmitReportModel.SUCCESS, ignoreCase = true) == true) {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        checkFeaturePermissionGranted()

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    private fun showPermissionDialog() {
        EasyPermissions.requestPermissions(
            this, "Aplikasi membutuhkan akses device, mohon izinkan terlebih dahulu",
            EASY_PERMISSION_CODE, *perms
        )
    }

    @AfterPermissionGranted(EASY_PERMISSION_CODE)
    private fun checkFeaturePermissionGranted() {
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            showPermissionDialog()
        } else {
            recheckLocationPermission()
        }
    }

    private fun recheckLocationPermission() {
        if (EasyPermissions.hasPermissions(this, *perms)) {
            RequestLocation(this)
        } else {
            showPermissionDialog()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestLocation.LOCATION_CODE && resultCode == Activity.RESULT_OK) {

        } else if (requestCode == RequestLocation.LOCATION_CODE && resultCode == Activity.RESULT_CANCELED) {
            recheckLocationPermission()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        const val EASY_PERMISSION_CODE = 342
        private const val RC_SETTINGS_SCREEN_PERM = 123
        const val EXTRA_ROOM_ID = "extra_room_id"
        const val EXTRA_LAT = "extra_lat"
        const val EXTRA_LONG = "extra_long"
    }
}