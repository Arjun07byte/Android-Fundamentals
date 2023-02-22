package com.appify.backgroundreceivers.receiverClasses

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.appify.backgroundreceivers.MainActivity

class MainBroadcastReceiver(private val homeInterface: MainActivity.HomeInterface) :
    BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_POWER_CONNECTED -> homeInterface.charging()
            Intent.ACTION_POWER_DISCONNECTED -> homeInterface.chargingDisconnected()
            Intent.ACTION_AIRPLANE_MODE_CHANGED -> {
                if (intent.getBooleanExtra("state", false)) {
                    homeInterface.airplaneOn()
                } else {
                    homeInterface.airplaneOff()
                }
            }
            Intent.ACTION_HEADSET_PLUG -> {
                if (intent.getIntExtra("state", 0) == 1) {
                    homeInterface.headsetConnected()
                } else {
                    homeInterface.headsetDisconnected()
                }
            }
            Intent.ACTION_TIME_TICK -> homeInterface.timeChanged()
        }
    }
}