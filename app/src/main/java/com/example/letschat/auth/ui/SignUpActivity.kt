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
import androidx.lifecycle.lifecycleScope
import com.example.letschat.MyApplication
import com.example.letschat.home.ui.HomeActivity
import com.example.letschat.R
import com.example.letschat.auth.models.SignUpResultModel
import com.example.letschat.auth.view_models.SignUpViewModel
import com.example.letschat.databinding.ActivitySignUpBinding
import com.example.letschat.dependency_injection.AppContainer
import com.example.letschat.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SignUpActivity : AppCompatActivity(), View.OnClickListener{


    lateinit var appContainer: AppContainer
    private lateinit var signUpBinding: ActivitySignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel

    companion object{
        private const val TAG = "SignUpActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appContainer = (application as MyApplication).appContainer
        signUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        signUpViewModel = appContainer.signUpViewModel
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
                           handleSignUpResult(it)
                       })
                   }
                }
         }

    }

    private fun handleSignUpResult(signUpResult : SignUpResultModel?) {
        if (signUpResult?.isSignUpSuccessful == true){ // can't be with the (== true) because its a Boolean? not Boolean
            // Add User To Room
            addUserToRoomDatabase(signUpResult.user!!)

        }
        else{
            // Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, signUpResult?.errorType, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addUserToRoomDatabase(user: User) {
        val thisActivity = this
        lifecycleScope.launch(Dispatchers.Main) {
        signUpViewModel.addUserToRoomDatabase(user)
        signUpViewModel.insertStatusLiveData.observe(thisActivity, {
            if (it != -1L) {
                Log.d("Here", "Room Success")
                Log.d("Here", "User in Room with id: $it")
                updateUiForLoggedInUser(user)
            }
        })
    }}

    private fun updateUiForLoggedInUser(user: User) {
        Toast.makeText(this, "Sign up was successful", Toast.LENGTH_SHORT).show()
        startHomeActivity(user)
    }

    private fun startHomeActivity(user: User) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        // intent.putExtra("userId", userId)
        startActivity(intent)
        finish()
    }

    private fun showUserNameError(message: String) {
        signUpBinding.signUpUserNameField.error = message
    }

    private fun showEmailError(message: String) {
        signUpBinding.signUpEmailField.error = message
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
        finish()
    }


}