package com.example.letschat.auth.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.letschat.home.ui.HomeActivity
import com.example.letschat.R
import com.example.letschat.auth.models.SignUpResultModel
import com.example.letschat.auth.view_models.SignUpViewModel
import com.example.letschat.databinding.ActivitySignUpBinding


class SignUpActivity : AppCompatActivity(), View.OnClickListener{


    private lateinit var signUpBinding: ActivitySignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel

    companion object{
        private const val TAG = "SignUpActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        signUpViewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        signUpBinding.viewModel = signUpViewModel
        signUpBinding.lifecycleOwner = this

        setOnClickListeners()

    }

    private fun setOnClickListeners() {
        signUpBinding.signUpButton.setOnClickListener(this)
        signUpBinding.goToLoginScreen.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()

        val (isUserLoggedIn, uId) = signUpViewModel.isUserAlreadyLoggedIn()
        handleUiBasedOnSignInStatus(isUserLoggedIn, uId)
    }

    private fun handleUiBasedOnSignInStatus(isUserLoggedIn: Boolean, uId: String) {
        if(isUserLoggedIn){
            Log.d(TAG, "User Already Logged In // Take him to Home Screen")
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

    }

     private fun onSignUpButtonClicked() {
        val (isUserNameValid, message) = signUpViewModel.isUserNameValid()
         if (!isUserNameValid) showUserNameError(message)
         else {
              val (isEmailValid, message) = signUpViewModel.isEmailValid()
               if (!isEmailValid) showEmailError(message)
                else {
                   val (isPasswordValid, message) = signUpViewModel.isPasswordValid()
                   if (!isPasswordValid) showPasswordError(message)
                   else {
                       // start the sign up stuff
                       signUpViewModel.signUp()
                       signUpViewModel.signUpResult.observe(this, Observer {
                           updateUiBasedOnSignUpResult(it)
                       })
                   }
                }
         }

    }

    private fun updateUiBasedOnSignUpResult(signUpResult : SignUpResultModel?) {
        if (signUpResult?.isSignUpSuccessful == true){ // can't be with the (== true) because its a Boolean? not Boolean
            Toast.makeText(this, "Sign up was successful", Toast.LENGTH_SHORT).show()
            startHomeActivity(signUpResult.userId)
        }
        else{
            // Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, signUpResult?.errorType, Toast.LENGTH_SHORT).show()
        }
    }

    private fun startHomeActivity(userId: String) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    private fun showUserNameError(message: String) {
       signUpBinding.signUpUserNameField.setError(message)
    }

    private fun showEmailError(message: String) {
        signUpBinding.signUpEmailField.setError(message)
    }

    private fun showPasswordError(message: String) {
        signUpBinding.signUpPasswordField.setError(message)
    }

    override fun onClick(view: View?) {
        when (view){
            signUpBinding.signUpButton -> onSignUpButtonClicked()
            signUpBinding.goToLoginScreen -> onGoToLoginButtonClicked()
        }
    }

    private fun onGoToLoginButtonClicked() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


}