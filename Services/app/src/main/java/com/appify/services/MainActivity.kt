package com.appify.services

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startServiceButton: Button = findViewById(R.id.button_startService)
        val stopServiceButton: Button = findViewById(R.id.button_stopService)
        val sendDataButton: Button = findViewById(R.id.button_sendData)
        val etDataToSend: EditText = findViewById(R.id.et_dataToSend)

        startServiceButton.setOnClickListener {
            startServiceButton.isEnabled = false; stopServiceButton.isEnabled = true
            sendDataButton.isEnabled = true; etDataToSend.isEnabled = true
        }

        stopServiceButton.setOnClickListener {
            Intent(this, MainService::class.java).also {
                stopService(it); startServiceButton.isEnabled = true
                stopServiceButton.isEnabled = false; sendDataButton.isEnabled = false
                etDataToSend.isEnabled = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Intent(this, MainService::class.java).also { startService(it) }
    }
}