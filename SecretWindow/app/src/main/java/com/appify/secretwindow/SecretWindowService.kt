package com.appify.secretwindow

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.appify.secretwindow.activities.BlueLockActivity


class SecretWindowService : AccessibilityService() {
    private lateinit var blueLockIntent: Intent
    private lateinit var blueLockPendingIntent: PendingIntent

    private val blueLockProhibitionList = listOf(
        "Telegram", "Instagram",
        "Chrome", "Brave", "Terabox",
        "pvt_arjun10"
    )

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.notificationTimeout = 100
        serviceInfo = info

        blueLockIntent = Intent(applicationContext, SecretWindowService::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        blueLockPendingIntent =
            PendingIntent.getActivity(applicationContext, 0, blueLockIntent, PendingIntent.FLAG_IMMUTABLE)
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
        sendNotification("Please Turn it On","BlueLock is Destroyed", true)
    }

    override fun onCreate() {
        super.onCreate()
        sendNotification("Don't Worry","BlueLock is Created", false)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        sendNotification("Please Turn it On","BlueLock is Destroyed", true)
    }

    override fun onDestroy() {
        super.onDestroy()
        sendNotification("Please Turn it On","BlueLock is Destroyed", true)
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification(text: String, title: String, isPendingIntentAllowed: Boolean) {
        val builder = NotificationCompat.Builder(applicationContext, "BlueLockChannel")
            .setSmallIcon(R.drawable.ic_block)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if(isPendingIntentAllowed) {
            builder.setContentIntent(blueLockPendingIntent)
            builder.setAutoCancel(true)
        }

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(100, builder.build())
        }
    }
}