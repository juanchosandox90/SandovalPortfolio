package com.juansandoval.sandovalportfolio

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.juansandoval.sandovalportfolio.utils.TypeWriteTextView

class SplashActivity : AppCompatActivity() {

    private lateinit var textApp: TypeWriteTextView

    private val splashTimeout: Long = 5000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initView()
        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, splashTimeout)
    }

    private fun initView() {
        textApp = findViewById(R.id.textAppName)
        textApp.setCharDelay(100)
        textApp.animateText(getString(R.string.app_name))
    }
}