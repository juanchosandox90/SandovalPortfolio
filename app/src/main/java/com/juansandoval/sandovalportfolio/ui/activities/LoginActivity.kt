package com.juansandoval.sandovalportfolio.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.juansandoval.sandovalportfolio.R
import com.juansandoval.sandovalportfolio.databinding.ActivityLoginBinding
import com.juansandoval.sandovalportfolio.ui.auth.AuthListener
import com.juansandoval.sandovalportfolio.utils.CustomDialog
import com.juansandoval.sandovalportfolio.viewmodel.LoginViewModel


class LoginActivity : AppCompatActivity(), AuthListener {
    private val firebaseAuth = FirebaseAuth.getInstance()

    val RC_SIGN_IN: Int = 1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions

    private var mDataBase: DatabaseReference? = null

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel.authInterface = this
        configureGoogleSignIn()
        verifyUser()
    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    fun googleSignIn(view: View) {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun verifyUser() {
        val user = viewModel.verifyUserLoggedIn()
        if (user != null) {
            val dashboardIntent = Intent(applicationContext, HomeActivity::class.java)
            dashboardIntent.putExtra("userId", firebaseAuth.currentUser!!.uid)
            dashboardIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(dashboardIntent)
        }
    }

    override fun onStarted() {
        CustomDialog(this, viewModel.authLiveData, this).show()
    }

    override fun onSuccess() {
        viewModel.authLiveData.postValue(Pair(1, null))
        val homeIntent = Intent(applicationContext, HomeActivity::class.java)
        homeIntent.putExtra("userId", firebaseAuth.currentUser!!.uid)
        homeIntent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        applicationContext.startActivity(homeIntent)
    }

    override fun onFailure(message: String?) {
        viewModel.authLiveData.value = Pair(2, message)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        onStarted()
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val currentUserId = firebaseAuth.currentUser
                val userId = currentUserId!!.uid

                mDataBase = FirebaseDatabase.getInstance().reference
                    .child("Users").child(userId)

                val userObject = HashMap<String, String>()

                mDataBase!!.addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            onSuccess()
                        } else {
                            userObject["display_name"] = acct.displayName.toString()
                            userObject["status"] = "Hello There"
                            userObject["email"] = acct.email.toString()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed :(", Toast.LENGTH_LONG).show()
            }
        }
    }
}