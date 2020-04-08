package com.juansandoval.sandovalportfolio.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.juansandoval.sandovalportfolio.ui.auth.AuthListener

class LoginViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    var email: String? = null
    var password: String? = null

    var authInterface: AuthListener? = null
    val authLiveData = MutableLiveData<Pair<Int?, String?>>()

    fun onLoginButtonClick(view: View) {
        authLiveData.value = Pair(0, null)
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
}