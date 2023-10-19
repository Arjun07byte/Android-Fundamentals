package com.appify.secretwindow.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.appify.secretwindow.R

class BlueLockActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private var urlDictionary = listOf(
        "https://www.youtube.com/watch?v=rRDtIgKBrpE",
        "https://www.youtube.com/watch?v=G4aVLJjsMnw",
        "https://www.youtube.com/watch?v=Kx53D4xjyKQ",
        "https://www.youtube.com/watch?v=28sptQICKCk",
        "https://www.youtube.com/watch?v=3fOasfoBbtc",
        "https://www.youtube.com/watch?v=tPzyve2IUx8",
        "https://www.youtube.com/watch?v=E_4H5araq5Q",
        "https://www.youtube.com/watch?v=j-eP7NpRJto"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blue_lock)

        webView = findViewById(R.id.webView)
        webView.webViewClient = MyWebViewClient()
        webView.loadUrl(urlDictionary.random())
        webView.settings.javaScriptEnabled = true
    }

    private class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            return false
        }
    }
}