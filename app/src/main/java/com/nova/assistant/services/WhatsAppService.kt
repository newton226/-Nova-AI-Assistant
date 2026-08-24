package com.nova.assistant.services

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast

class WhatsAppService {
    
    companion object {
        private const val WHATSAPP_PACKAGE = "com.whatsapp"
        private const val WHATSAPP_BUSINESS_PACKAGE = "com.whatsapp.w4b"
        
        fun sendDirectMessage(context: Context, phoneNumber: String, message: String) {
            try {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:$phoneNumber")
                    putExtra("sms_body", message)
                    setPackage(WHATSAPP_PACKAGE)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("WhatsApp", "Error sending message", e)
                // Try opening WhatsApp directly
                openWhatsAppWithMessage(context, message)
            }
        }
        
        fun openWhatsAppWithMessage(context: Context, message: String) {
            try {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, message)
                    setPackage(WHATSAPP_PACKAGE)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("WhatsApp", "Error opening WhatsApp", e)
            }
        }
        
        fun sendToContact(context: Context, contactName: String, message: String) {
            // First, try to find the contact in WhatsApp
            val intent = Intent(Intent.ACTION_VIEW).apply {
                val uri = Uri.parse("content://com.android.contacts/data")
                data = uri
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("WhatsApp", "Error finding contact", e)
            }
        }
        
        fun isWhatsAppInstalled(context: Context): Boolean {
            return try {
                context.packageManager.getPackageInfo(WHATSAPP_PACKAGE, 0)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}
