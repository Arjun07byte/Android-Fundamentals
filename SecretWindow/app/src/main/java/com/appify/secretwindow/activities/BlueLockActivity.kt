package com.appify.secretwindow.activities

import android.content.ContentResolver
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import com.appify.secretwindow.R
import java.io.File

class BlueLockActivity : AppCompatActivity() {
    private val audioList = listOf(
        R.raw.audio_1, R.raw.audio_2, R.raw.audio_3,
        R.raw.audio_4, R.raw.audio_5
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blue_lock)

        /*val myUri = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE
                    + File.pathSeparator + File.separator + File.separator
                    + this.packageName + File.separator
                    + audioList.random()
        )
        val mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(applicationContext, myUri)
            prepare()
            start()
        }*/

        findViewById<TextView>(R.id.nameText).text = intent.getStringExtra("payee_name")
        findViewById<TextView>(R.id.amountText).text = intent.getDoubleExtra("pay_amount",0.0).toString()
    }
}