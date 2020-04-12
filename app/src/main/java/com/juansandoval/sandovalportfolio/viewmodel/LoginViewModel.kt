package com.juansandoval.sandovalportfolio.viewmodel

import android.app.Application
import android.content.Intent
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.juansandoval.sandovalportfolio.ui.activities.SignUpActivity
import com.juansandoval.sandovalportfolio.ui.auth.AuthListener

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseAuth = FirebaseAuth.getInstance()

    var email: String? = null
    var password: String? = null

    var authInterface: AuthListener? = null
    val authLiveData = MutableLiveData<Pair<Int?, String?>>()

    fun onLoginButtonClick(view: View) {
        authLiveData.postValue(Pair(0, null))
        authInterface?.onStarted()

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            authInterface?.onFailure("Invalid email or password")

        } else {
            firebaseAuth.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener { auth ->
                    if (auth.isSuccessful) {
                        authInterface?.onSuccess()
                    } else {
                        authInterface?.onFailure(auth.exception?.message.toString())
                    }
                }
        }
    }

    fun verifyUserLoggedIn(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun goToSignup(view: View) {
        Intent(view.context, SignUpActivity::class.java).also {
            view.context.startActivity(it)
        }
    }
}