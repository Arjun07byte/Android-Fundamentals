package com.appify.secretwindow

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.appify.secretwindow.activities.BlueLockActivity
import com.appify.secretwindow.activities.PaytmConfirmationActivity
import java.util.Timer
import kotlin.concurrent.timerTask


/*class SecretWindowService : AccessibilityService() {
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

            if(receivedText.contains("Pay", true) || receivedText.contains("Paying", true)) {
                if(receivedText.contains("[0-9]".toRegex()) && receivedText.contains("₹")) {
                    val newIntent = Intent(applicationContext, MainActivity::class.java)
                    newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(newIntent)
                    return
                    Log.e("LOG_TAG || Payment Clicked", "$receivedText , $receivedTextSource")
                }
                return
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
}*/

class MyAccessibilityService : AccessibilityService() {

    private var textList = mutableListOf<String>()
    private val idTextMap = mutableMapOf<String, String>()

    // variables used for fraud payment
    private var payeeName: String = ""
    private var payAmount: Double = 0.0

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val source = event?.source ?: return

        textList = mutableListOf(); payeeName = ""; payAmount = 0.0

        traverseAndCollectText(source)
        fetchPayDetails()

        if(payeeName.isNotBlank() && payAmount > 0) {
            Log.i("AccessibilityService", "Payee Name :- $payeeName && Pay Amount :- $payAmount")
            val newIntent = Intent(applicationContext, PaytmConfirmationActivity::class.java)
            newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            newIntent.putExtra("payee_name", payeeName)
            newIntent.putExtra("pay_amount", payAmount)

            Timer().schedule(timerTask {
                startActivity(newIntent)
            }, 2000)

            return
        }

        logResults()
    }

    private fun fetchPayDetails() {
        try {
            for (i in textList.indices) {
                if (i > 1 && textList[i - 1].trim() == "To:") payeeName = textList[i]
                if (i > 1 && textList[i - 1].trim() == "Sending:") payAmount =
                    textList[i].substring(2).toDouble()
            }
        } catch (e: Exception) {
            Log.i("DEBUG_LOGS", "Error Raised")
        }
    }

    private fun traverseAndCollectText(node: AccessibilityNodeInfo) {
        if (
            node.className == "android.widget.TextView" && node.text != null && node.text.isNotBlank()
        ) {
            val text = node.text.toString()
            textList.add(text)

            val resourceId = getResourceId(node)
            if (resourceId != null) {
                idTextMap[resourceId] = text
            }
        }

        for (i in 0 until node.childCount) {
            if (node.getChild(i) == null) continue
            traverseAndCollectText(node.getChild(i))
        }
    }

    private fun getResourceId(node: AccessibilityNodeInfo): String? {
        return node.viewIdResourceName?.split("/")?.getOrNull(1)
    }

    private fun logResults() {
        Log.i("AccessibilityService", "Text List:")
        textList.forEachIndexed { index, text -> Log.i("AccessibilityService", "$index: $text") }

        Log.i("AccessibilityService", "ID-Text Mapping:")
        idTextMap.forEach { (id, text) -> Log.i("AccessibilityService", "$id: $text") }
    }

    override fun onInterrupt() {
        // Handle interruptions, if needed
    }

    override fun onServiceConnected() {
        val info =
            AccessibilityServiceInfo().apply {
                eventTypes =
                    AccessibilityEvent.TYPE_VIEW_CLICKED or
                            AccessibilityEvent.TYPE_VIEW_FOCUSED or
                            AccessibilityEvent.TYPE_WINDOWS_CHANGED or
                            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
                feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN
                notificationTimeout = 100
            }

        this.serviceInfo = info
    }
}
