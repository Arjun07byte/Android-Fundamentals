package com.appify.backgroundreceivers

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appify.backgroundreceivers.databinding.ActivityMainBinding
import com.appify.backgroundreceivers.receiverClasses.MainBroadcastReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var myBroadcastReceiver: MainBroadcastReceiver
    private lateinit var myViewBinder: ActivityMainBinding
    private val newIntentFilter = IntentFilter()
    private var areReceiversRegistered = true

    inner class HomeInterface {
        fun chargingDisconnected() {
            myViewBinder.imgViewCharging.alpha =
                0.1F; myViewBinder.imgViewChargingDisconnected.alpha = 1F
        }

        fun charging() {
            myViewBinder.imgViewCharging.alpha =
                1F; myViewBinder.imgViewChargingDisconnected.alpha = 0.1F
        }

        fun headsetConnected() {
            myViewBinder.imgViewHeadset.alpha = 1F; myViewBinder.imgViewHeadsetOff.alpha = 0.1F
        }

        fun headsetDisconnected() {
            myViewBinder.imgViewHeadset.alpha = 0.1F; myViewBinder.imgViewHeadsetOff.alpha = 1F
        }

        fun airplaneOn() {
            myViewBinder.imgViewAirplaneOn.alpha = 1F; myViewBinder.imgViewAirplaneOff.alpha = 0.1F
        }

        fun airplaneOff() {
            myViewBinder.imgViewAirplaneOn.alpha = 0.1F; myViewBinder.imgViewAirplaneOff.alpha = 1F
        }

        fun timeChanged() {
            myViewBinder.textViewTime.text = getCurrentTime()

            myViewBinder.textViewTime.alpha = 1F
            CoroutineScope(Dispatchers.IO).launch {
                delay(3000); myViewBinder.textViewTime.alpha = 0.1F
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myViewBinder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(myViewBinder.root)

        val myHomeInterface = HomeInterface()
        myBroadcastReceiver = MainBroadcastReceiver(myHomeInterface)

        registerMyBackgroundReceiver(); myViewBinder.textViewTime.text = getCurrentTime()

        // setting up the listener for Stop and Start Button
        myViewBinder.buttonStopReceivers.setOnClickListener {
            if (areReceiversRegistered) {
                unregisterReceiver(myBroadcastReceiver); areReceiversRegistered = false
                myViewBinder.buttonStopReceivers.text = "Start"

                myViewBinder.layoutChargingReceiver.alpha = 0.2F
                myViewBinder.layoutHeadsetReceiver.alpha = 0.2F
                myViewBinder.layoutAirplaneModeReceiver.alpha = 0.2F
                myViewBinder.layoutTimeReceiver.alpha = 0.2F
                myViewBinder.textViewErrorMessage.visibility = View.VISIBLE
            } else {
                registerMyBackgroundReceiver(); areReceiversRegistered = true
                myViewBinder.buttonStopReceivers.text = "Stop"

                myViewBinder.layoutChargingReceiver.alpha = 1F
                myViewBinder.layoutHeadsetReceiver.alpha = 1F
                myViewBinder.layoutAirplaneModeReceiver.alpha = 1F
                myViewBinder.layoutTimeReceiver.alpha = 1F
                myViewBinder.textViewErrorMessage.visibility = View.GONE
            }
        }
    }

    private fun registerMyBackgroundReceiver() {
        newIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED)
        newIntentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        newIntentFilter.addAction(Intent.ACTION_HEADSET_PLUG)
        newIntentFilter.addAction(Intent.ACTION_TIME_TICK)
        newIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)

        newIntentFilter.also {
            registerReceiver(myBroadcastReceiver, it)
        }
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("hh:mm", Locale.getDefault())
            .format(Calendar.getInstance().time)
    }

    override fun onStop() {
        super.onStop(); unregisterReceiver(myBroadcastReceiver)
    }

}