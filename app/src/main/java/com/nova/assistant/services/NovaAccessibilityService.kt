package com.nova.assistant.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class NovaAccessibilityService : AccessibilityService() {
    
    companion object {
        var instance: NovaAccessibilityService? = null
            private set
    }
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or
                    AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            notificationTimeout = 100
        }
        
        serviceInfo = info
        Log.d("NovaAccessibility", "Service connected")
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            Log.d("NovaAccessibility", "Event: ${it.eventType}")
            
            // Handle different accessibility events
            when (it.eventType) {
                AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                    handleViewClicked(it)
                }
                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                    handleTextChanged(it)
                }
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    handleWindowChanged(it)
                }
            }
        }
    }
    
    private fun handleViewClicked(event: AccessibilityEvent) {
        // Handle view clicks for automation
        Log.d("NovaAccessibility", "View clicked: ${event.text}")
    }
    
    private fun handleTextChanged(event: AccessibilityEvent) {
        // Handle text changes
        Log.d("NovaAccessibility", "Text changed: ${event.text}")
    }
    
    private fun handleWindowChanged(event: AccessibilityEvent) {
        // Handle window changes
        Log.d("NovaAccessibility", "Window changed: ${event.packageName}")
    }
    
    override fun onInterrupt() {
        Log.d("NovaAccessibility", "Service interrupted")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
    
    fun performClick(x: Int, y: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Perform click at coordinates
            val path = android.graphics.Path()
            path.moveTo(x.toFloat(), y.toFloat())
            dispatchGesture(
                android.accessibilityservice.GestureDescription.Builder()
                    .addStroke(android.accessibilityservice.GestureDescription.StrokeDescription(path, 0, 100))
                    .build(),
                null,
                null
            )
        }
    }
    
    fun performSwipe(startX: Int, startY: Int, endX: Int, endY: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val path = android.graphics.Path()
            path.moveTo(startX.toFloat(), startY.toFloat())
            path.lineTo(endX.toFloat(), endY.toFloat())
            dispatchGesture(
                android.accessibilityservice.GestureDescription.Builder()
                    .addStroke(android.accessibilityservice.GestureDescription.StrokeDescription(path, 0, 300))
                    .build(),
                null,
                null
            )
        }
    }
    
    fun typeText(text: String) {
        // Type text using accessibility service
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("text", text)
        clipboard.setPrimaryClip(clip)
        
        // Paste text
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_PASTE)
    }
}
