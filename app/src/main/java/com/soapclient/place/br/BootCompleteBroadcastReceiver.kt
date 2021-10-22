package com.soapclient.place.br

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.soapclient.place.service.LocationUpdateService

class BootCompleteBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (ACTION_BOOT_COMPLETED == intent.action) {
            startLocationUpdateService(context)
        }
    }

    private fun startLocationUpdateService(context: Context) {
        val serviceIntent = Intent(context, LocationUpdateService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    companion object {
        const val ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED"
    }
}