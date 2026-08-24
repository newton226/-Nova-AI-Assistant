package com.nova.assistant.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.nova.assistant.services.BackgroundService

class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("NovaBoot", "Device booted, starting services")
            
            // Start background service
            val serviceIntent = Intent(context, BackgroundService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}
