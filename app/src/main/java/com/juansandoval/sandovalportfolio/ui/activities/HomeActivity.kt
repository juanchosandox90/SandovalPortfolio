package com.juansandoval.sandovalportfolio.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.juansandoval.sandovalportfolio.R
import com.juansandoval.sandovalportfolio.utils.startLoginActivity

class HomeActivity : AppCompatActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    override fun onBackPressed() {
        firebaseAuth.signOut()
        startLoginActivity()
    }
}
