package com.appify.secretwindow

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.appify.secretwindow.activities.BlueLockActivity


class SecretWindowService : AccessibilityService() {
    val blueLockProhibitionList = listOf(
        "Telegram",
        "Chrome"
    )

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.notificationTimeout = 100
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event!!.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            val receivedText = event.text.toString()
            val receivedTextSource = event.source?.packageName.toString()

            for (blueLockProhibitionItem in blueLockProhibitionList) {
                if (receivedText.contains(blueLockProhibitionItem, true)) {
                    val newIntent = Intent(applicationContext, BlueLockActivity::class.java)
                    newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(newIntent)
                    return
                }
            }

            Log.e("LOG_TAG", "$receivedText , $receivedTextSource")
        }
    }

    override fun onInterrupt() {
        Log.e("LOG_TAG", "Service Interrupted")
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("LOG_TAG", "Service onCreate")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.e("LOG_TAG", "Service onLowMemory")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("LOG_TAG", "Service onDestroy")
    }
}