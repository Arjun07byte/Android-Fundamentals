package com.appify.backgroundreceivers.receiverClasses

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MainBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                Toast.makeText(context, "Phone is Charging", Toast.LENGTH_LONG).show()
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                Toast.makeText(context, "Charging Disconnected", Toast.LENGTH_LONG).show()
            }
            Intent.ACTION_AIRPLANE_MODE_CHANGED -> {
                if (intent.getBooleanExtra("state", false)) {
                    Toast.makeText(context, "Airplane Mode On", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Airplane Mode Off", Toast.LENGTH_LONG).show()
                }
            }
            Intent.ACTION_HEADSET_PLUG -> {
                if (intent.getIntExtra("state", 0) == 1) {
                    Toast.makeText(context, "Headset Plugged", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Headset Unplugged", Toast.LENGTH_LONG).show()
                }
            }
            Intent.ACTION_TIME_TICK -> {
                Toast.makeText(context, "Time has Changed", Toast.LENGTH_LONG).show()
            }
        }
    }
}