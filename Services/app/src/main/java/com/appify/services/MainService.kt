package com.appify.services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.*

class MainService : Service() {
    private val myTAG = "Inside MainService.kt"
    override fun onBind(intent: Intent?): IBinder? = null

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(myTAG, "Service Started")

        val newIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 1, newIntent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("First Notification")
            .setContentText("Here is the info for the notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        var currTime = 1
        GlobalScope.launch(Dispatchers.IO) {
            while(true){
                delay(1000);
                with(NotificationManagerCompat.from(this@MainService)) {
                    notify(
                        1,
                        builder.setContentText("Here is $currTime info for the notification")
                            .build()
                    )
                    currTime += 1
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        Log.e(myTAG, "Service Stopped")
        super.onDestroy()
    }
}