package com.juansandoval.sandovalportfolio.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.juansandoval.sandovalportfolio.R
import com.juansandoval.sandovalportfolio.databinding.ActivitySignUpBinding
import com.juansandoval.sandovalportfolio.ui.auth.AuthListener
import com.juansandoval.sandovalportfolio.utils.CustomDialog
import com.juansandoval.sandovalportfolio.utils.startHomeActivity
import com.juansandoval.sandovalportfolio.viewmodel.SignUpViewModel

class SignUpActivity : AppCompatActivity(), AuthListener {

    private lateinit var viewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivitySignUpBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel.authInterface = this
    }

    override fun onStarted() {
        CustomDialog(this, viewModel.signupLiveData, this).show()
    }

    override fun onSuccess() {
        viewModel.signupLiveData.value = Pair(1, null)
        startHomeActivity()
    }

    override fun onFailure(message: String?) {
        viewModel.signupLiveData.value = Pair(2, message)
    }
}
