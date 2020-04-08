package com.juansandoval.sandovalportfolio.utils

import android.content.Context
import android.content.Intent
import com.juansandoval.sandovalportfolio.ui.activities.HomeActivity
import com.juansandoval.sandovalportfolio.ui.activities.LoginActivity

fun Context.startHomeActivity() =
    Intent(this, HomeActivity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }

fun Context.startLoginActivity() =
    Intent(this, LoginActivity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }

