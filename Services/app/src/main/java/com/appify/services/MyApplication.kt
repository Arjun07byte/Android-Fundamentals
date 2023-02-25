package com.appify.services

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val newChannel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                "Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "This is the service notification channel"
            }

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(newChannel)
        }
    }
}