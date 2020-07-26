package com.juansandoval.sandovalportfolio.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.HwIdAuthProvider
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.juansandoval.sandovalportfolio.R
import com.juansandoval.sandovalportfolio.databinding.ActivitySignUpBinding
import com.juansandoval.sandovalportfolio.ui.auth.AuthListener
import com.juansandoval.sandovalportfolio.utils.CustomDialog
import com.juansandoval.sandovalportfolio.viewmodel.SignUpViewModel
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity(), AuthListener {

    private val huaweiAuth = AGConnectAuth.getInstance()
    private val HUAWEIID_SIGNIN = 8000
    private var mDataBase: DatabaseReference? = null

    private lateinit var viewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivitySignUpBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel.authInterface = this
        btnHuaweiSignUp.setOnClickListener {
            loginWithHuaweiID()
        }

    }

    private fun loginWithHuaweiID() {
        val authParams = HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setEmail()
            .setProfile()
            .setAccessToken()
            .createParams()
        val service = HuaweiIdAuthManager.getService(this, authParams)
        startActivityForResult(service.signInIntent, HUAWEIID_SIGNIN)
    }

    override fun onStarted() {
        CustomDialog(this, viewModel.signupLiveData, this).show()
    }

    override fun onSuccess() {
        viewModel.signupLiveData.postValue(Pair(1, null))
        val homeIntent = Intent(applicationContext, HomeActivity::class.java)
        homeIntent.putExtra("userId", huaweiAuth.currentUser!!.uid)
        homeIntent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        applicationContext.startActivity(homeIntent)
    }

    override fun onFailure(message: String?) {
        viewModel.signupLiveData.value = Pair(2, message)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        onStarted()
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == HUAWEIID_SIGNIN) {
            val authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data)
            if (authHuaweiIdTask.isSuccessful) {
                val huaweiAccount = authHuaweiIdTask.result
                val accessToken = huaweiAccount.accessToken
                val credential = HwIdAuthProvider.credentialWithToken(accessToken)
                AGConnectAuth.getInstance().signIn(credential)
                    .addOnCompleteListener { auth ->
                        if (auth.isSuccessful) {
                            val user = AGConnectAuth.getInstance().currentUser
                            val uId = user.uid
                            mDataBase = FirebaseDatabase.getInstance().reference
                                .child("Users").child(uId!!)
                            val userObject = HashMap<String, String>()
                            mDataBase!!.addListenerForSingleValueEvent(object : ValueEventListener {

                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        onSuccess()
                                    } else {
                                        userObject["display_name"] = user.displayName
                                        userObject["status"] = "Hello There"
                                        if (user.email != null) {
                                            userObject["email"] = huaweiAccount.email
                                        } else {
                                            userObject["email"] = "default"
                                        }
                                        userObject["image"] = "default"
                                        userObject["thumb_image"] = "default"
                                        mDataBase!!.setValue(userObject)
                                            .addOnCompleteListener { task: Task<Void> ->
                                                if (task.isSuccessful) {
                                                    onSuccess()
                                                } else {
                                                    onFailure(getString(R.string.ops_something_went_wrong))
                                                }
                                            }
                                    }
                                }

                                override fun onCancelled(dbError: DatabaseError) {
                                    onFailure(getString(R.string.ops_something_went_wrong))
                                }
                            })

                        } else {
                            onFailure(getString(R.string.ops_something_went_wrong))
                        }
                    }
            }
        }
    }
}
