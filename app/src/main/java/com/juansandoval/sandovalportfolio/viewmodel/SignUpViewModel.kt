package com.juansandoval.sandovalportfolio.viewmodel

import android.app.Application
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.juansandoval.sandovalportfolio.R
import com.juansandoval.sandovalportfolio.ui.activities.LoginActivity
import com.juansandoval.sandovalportfolio.ui.auth.AuthListener

class SignUpViewModel(application: Application) : AndroidViewModel(application) {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var mDataBase: DatabaseReference? = null
    val signupLiveData = MutableLiveData<Pair<Int?, String?>>()

    var authInterface: AuthListener? = null
    var email: String? = null
    var pass: String? = null
    var confirmPass: String? = null
    var name: String? = null

    fun signUp(view: View) {
        signupLiveData.postValue(Pair(0, null))
        authInterface?.onStarted()

        if (email.isNullOrEmpty() || pass.isNullOrEmpty() || confirmPass.isNullOrEmpty() || name.isNullOrEmpty()) {
            authInterface?.onFailure("All fields are mandatory!")

        } else if (pass != confirmPass) {
            authInterface?.onFailure("Both password must match!")

        } else {
            firebaseAuth.createUserWithEmailAndPassword(email!!, pass!!)
                .addOnCompleteListener { auth ->
                    if (auth.isSuccessful) {
                        val currentUserId = firebaseAuth.currentUser
                        val userId = currentUserId!!.uid

                        mDataBase = FirebaseDatabase.getInstance().reference
                            .child("Users").child(userId)
                        val userObject = HashMap<String, String>()

                        userObject["display_name"] = name!!.trim()
                        userObject["status"] = "Hello There"
                        userObject["email"] = email!!.trim()
                        userObject["image"] = "default"
                        userObject["thumb_image"] = "default"

                        firebaseAuth.currentUser?.let { user ->
                            val userUpdate = UserProfileChangeRequest.Builder()
                            userUpdate.setDisplayName(name)
                            userObject["display_name"] = name!!
                            user.updateProfile(userUpdate.build()).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    authInterface?.onSuccess()
                                } else {
                                    authInterface?.onFailure(it.exception?.message)
                                }
                            }
                        }

                        mDataBase!!.setValue(userObject).addOnCompleteListener { task: Task<Void> ->
                            if (task.isSuccessful) {
                                Log.d("Successful", "true")
                            } else {
                                Toast.makeText(
                                    view.context,
                                    view.context.getString(R.string.ops_something_went_wrong),
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }

                    } else {
                        authInterface?.onFailure(auth.exception?.message)

                    }
                }
        }
    }

    fun goToLogin(view: View) {
        Intent(view.context, LoginActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            view.context.startActivity(it)
        }
    }
}