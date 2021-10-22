package com.soapclient.place.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.soapclient.place.R
import com.soapclient.place.delegate.SignInViewModelDelegate
import com.soapclient.place.domain.di.ApplicationScope
import com.soapclient.place.domain.entity.BatteryInfo
import com.soapclient.place.domain.entity.Location
import com.soapclient.place.domain.usecase.location.UpdateLocationUseCase
import com.soapclient.place.view.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationUpdateService: Service() {

    @Inject
    lateinit var updateLocationUseCase: UpdateLocationUseCase

    @Inject
    lateinit var signInViewModelDelegate: SignInViewModelDelegate

    @Inject @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private val locationRequest by lazy {
        LocationRequest.create()
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(MAX_WAIT_TIME)
    }

    private val locationCallback by lazy {
        object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                updateCurrentLocation(locationResult)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        requestLocationUpdates()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, getForegroundNotification(applicationContext), FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            startForeground(NOTIFICATION_ID, getForegroundNotification(applicationContext))
        }
        return START_STICKY
    }

    private fun getBatteryInfo(): BatteryInfo {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
            applicationContext.registerReceiver(null, filter)
        }

        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL

        val batteryPct: Float? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        }
        val percent = batteryPct?.toInt() ?: 999
        return BatteryInfo(percent, isCharging)
    }

    private fun updateCurrentLocation(locationResult: LocationResult) {
        val location = locationResult.lastLocation
        val batteryInfo = getBatteryInfo()
        signInViewModelDelegate.userInfo.value?.getUid()?.let { uid ->
            applicationScope.launch {
                updateLocationUseCase(
                    UpdateLocationUseCase.Param(
                        uid,
                        Location(
                            location.latitude,
                            location.longitude,
                            batteryInfo.percent,
                            batteryInfo.isCharging,
                            System.currentTimeMillis()
                        )
                    )
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun getForegroundNotification(context: Context): Notification {
        createNotificationChannel(context,
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.notification_channel_name),
                getString(R.string.notification_channel_description),
                NotificationManager.IMPORTANCE_DEFAULT,
                false)
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSmallIcon(R.drawable.btn_phone)
                .build()
    }

    private fun createNotificationChannel(context: Context, id: String, name: String, description: String, importance: Int, showBadge: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(id, name, importance)
            serviceChannel.description = description
            serviceChannel.setShowBadge(showBadge)
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "Sop-notification-channel-location"
        private const val NOTIFICATION_ID = 1000
        private const val UPDATE_INTERVAL = 60000L
        private const val FASTEST_UPDATE_INTERVAL = 30000L
        private const val MAX_WAIT_TIME = UPDATE_INTERVAL * 5L
    }
}