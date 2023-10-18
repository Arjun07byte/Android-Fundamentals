package com.appify.secretwindow.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.appify.secretwindow.R

class BlueLockActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blue_lock)

        webView = findViewById(R.id.webView)
        webView.webViewClient = MyWebViewClient()
        webView.loadUrl("https:www.instagram.com/rawat.amit.12")
        webView.settings.javaScriptEnabled = true
    }

    private class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            return false
        }
    }
}