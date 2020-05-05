package com.juansandoval.sandovalportfolio.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.juansandoval.sandovalportfolio.R

class WebViewActivity : AppCompatActivity() {

    private var webSite: String? = null
    private var company: String? = null
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (intent.extras != null) {
            webSite = intent!!.extras!!.get("website").toString()
            company = intent!!.extras!!.get("company").toString()
            setupPage()
        }
    }

    private fun setupPage() {
        webView = findViewById(R.id.wvWebSite)
        supportActionBar!!.title = company
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        webView.loadUrl(webSite)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
