package com.appify.backgroundreceivers

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appify.backgroundreceivers.receiverClasses.MainBroadcastReceiver

class MainActivity : AppCompatActivity() {
    private lateinit var myBroadcastReceiver: MainBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myBroadcastReceiver = MainBroadcastReceiver()
        val newIntentFilter = IntentFilter()
        newIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED)
        newIntentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        newIntentFilter.addAction(Intent.ACTION_HEADSET_PLUG)
        newIntentFilter.addAction(Intent.ACTION_TIME_TICK)
        newIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        newIntentFilter.also {
            registerReceiver(myBroadcastReceiver, it)
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(myBroadcastReceiver)
    }
}