package com.juansandoval.sandovalportfolio.viewmodel

import android.app.Application
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.huawei.agconnect.auth.*
import com.huawei.agconnect.auth.VerifyCodeSettings.ACTION_REGISTER_LOGIN
import com.huawei.hmf.tasks.OnFailureListener
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hmf.tasks.TaskExecutors
import com.juansandoval.sandovalportfolio.R
import com.juansandoval.sandovalportfolio.ui.activities.LoginActivity
import com.juansandoval.sandovalportfolio.ui.auth.AuthListener
import java.util.*
import kotlin.collections.HashMap


class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    private val huaweiAuth = AGConnectAuth.getInstance()
    private var mDataBase: DatabaseReference? = null
    val signupLiveData = MutableLiveData<Pair<Int?, String?>>()

    var authInterface: AuthListener? = null
    var email: String? = null
    var pass: String? = null
    var confirmPass: String? = null
    var name: String? = null
    var verCode: String? = null

    fun sendVerificationCode(view: View) {
        if (email.isNullOrEmpty()) {
            authInterface?.onFailure("Invalid Email!")
        } else {
            val settings = VerifyCodeSettings.newBuilder()
                .action(ACTION_REGISTER_LOGIN) //ACTION_REGISTER_LOGIN/ACTION_RESET_PASSWORD
                .sendInterval(120) // Minimum sending interval, ranging from 30s to 120s.
                .locale(Locale.getDefault()) // Language in which a verification code is sent, which is optional. The default value is Locale.getDefault.
                .build()

            val task = EmailAuthProvider.requestVerifyCode(email, settings)
            task.addOnSuccessListener(
                TaskExecutors.uiThread(),
                OnSuccessListener {

                    Toast.makeText(
                        getApplication(),
                        "Verification code has been sent to your email!",
                        Toast.LENGTH_LONG
                    ).show()

                }).addOnFailureListener(
                TaskExecutors.uiThread(),
                OnFailureListener {
                    Toast.makeText(getApplication(), it.message, Toast.LENGTH_LONG).show()
                })
        }

    }

    fun signUpHuawei(view: View) {
        signupLiveData.postValue(Pair(0, null))
        authInterface?.onStarted()

        if (email.isNullOrEmpty() || pass.isNullOrEmpty() || confirmPass.isNullOrEmpty() || name.isNullOrEmpty() || verCode.isNullOrEmpty()) {
            authInterface?.onFailure("All fields are mandatory!")

        } else if (pass != confirmPass) {
            authInterface?.onFailure("Both password must match!")

        } else if (verCode.isNullOrEmpty()) {
            authInterface?.onFailure("Enter verification code to Register")
        } else {
            val emailUser =
                EmailUser.Builder().setEmail(email).setVerifyCode(verCode).setPassword(pass)
                    .build()
            AGConnectAuth.getInstance().createUser(emailUser)
                .addOnCompleteListener { auth ->
                    if (auth.isSuccessful) {
                        val currentUserId = AGConnectAuth.getInstance().currentUser
                        val userId = currentUserId!!.uid

                        mDataBase = FirebaseDatabase.getInstance().reference
                            .child("Users").child(userId)
                        val userObject = HashMap<String, String>()

                        userObject["display_name"] = name!!.trim()
                        userObject["status"] = "Hello There"
                        userObject["email"] = email!!.trim()
                        userObject["image"] = "default"
                        userObject["thumb_image"] = "default"

                        huaweiAuth.currentUser?.let { user ->
                            val userUpdate = ProfileRequest.Builder()
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
                        authInterface?.onFailure(auth.exception.message)

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