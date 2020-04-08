package com.juansandoval.sandovalportfolio.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.juansandoval.sandovalportfolio.R
import com.juansandoval.sandovalportfolio.databinding.ActivityLoginBinding
import com.juansandoval.sandovalportfolio.ui.auth.AuthListener
import com.juansandoval.sandovalportfolio.viewmodel.LoginViewModel
import com.juansandoval.sandovalportfolio.utils.CustomDialog
import com.juansandoval.sandovalportfolio.utils.startHomeActivity

class LoginActivity : AppCompatActivity(), AuthListener {
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel.authInterface = this

        verifyUser()
    }

    private fun verifyUser() {
        val user = viewModel.verifyUserLoggedIn()
        if (user != null) {
            startHomeActivity()
        }
    }

    override fun onStarted() {
        CustomDialog(this, viewModel.authLiveData, this).show()
    }

    override fun onSuccess() {
        viewModel.authLiveData.value = Pair(1, null)
        startHomeActivity()
    }

    override fun onFailure(message: String?) {
        viewModel.authLiveData.value = Pair(2, message)
    }
}