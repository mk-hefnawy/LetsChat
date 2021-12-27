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
import com.example.letschat.MyApplication
import com.example.letschat.home.ui.HomeActivity
import com.example.letschat.R
import com.example.letschat.auth.view_models.LoginViewModel
import com.example.letschat.auth.models.LoginResultModel
import com.example.letschat.databinding.ActivityLoginBinding
import com.example.letschat.dependency_injection.AppContainer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseUser


class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    lateinit var appContainer: AppContainer

    private lateinit var  googleSignInButton: MaterialButton

    private companion object {
        private const val TAG = "LoginActivityHere"
        private const val GOOGLE_SIGN_IN_REQUEST_CODE = 300
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appContainer = (application as MyApplication).appContainer

        loginViewModel = appContainer.loginViewModel
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginBinding.viewModel = loginViewModel
        loginBinding.lifecycleOwner = this

        setOnClickListeners()
    }



    override fun onStart() {
        super.onStart()
        // so if the user is already signed in, they have to be directed to HomeActivity
        val currentUser = appContainer.auth.currentUser
        handleUiBasedOnSignInStatus(currentUser)

    }

    private fun onLoginButtonClicked(){
        val (isEmailValid, message) = loginViewModel.isEmailValid()
        if (!isEmailValid) showEmailError(message)
        else {
            val (isPasswordValid, message) = loginViewModel.isPasswordValid()
            if (!isPasswordValid) showPasswordError(message)
            else {
                Log.d(TAG, "onLoginButtonClicked Else Block")
                loginViewModel.loginWithEmailAndPassword()
                loginViewModel.loginLiveData.observe(this, Observer {
                    updateUiBasedOnLoginResult(it)
                })
            }
        }}

    private fun onGoogleButtonClicked(){
        loginViewModel.loginWithGoogle(this)
    }


    private fun goToSignUpScreen(){
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }



    private fun handleGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInButton = loginBinding.googleButton
        googleSignInButton.setOnClickListener { view ->
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
        }
    }

    private fun setOnClickListeners(){
        loginBinding.goToSignUpScreen.setOnClickListener(this)
        loginBinding.loginButton.setOnClickListener(this)
        loginBinding.forgotPasswordTextView.setOnClickListener(this)
    }

   /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }*/

    /*private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            fireBaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)

        }
    }*/

   /* private fun fireBaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful){
                    Log.d(TAG, "SignedInWithCredential: Success")
                    val user = auth.currentUser
                    handleUiBasedOnSignInStatus(user)
                }
                else{
                    Log.d(TAG, "SignedInWithCredential: Failure")
                    handleUiBasedOnSignInStatus(null)
                }
            }
    }*/

    private fun handleUiBasedOnSignInStatus(currentUser: FirebaseUser?) {
        if(currentUser == null){
            return
        }
        else{
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

    }

    override fun onClick(p0: View?) {
        when(p0!!){
             loginBinding.goToSignUpScreen -> goToSignUpScreen()
             loginBinding.googleButton -> onGoogleButtonClicked()
             loginBinding.loginButton -> onLoginButtonClicked()
             loginBinding.forgotPasswordTextView -> onForgotPasswordClicked()
            else -> println("Click Problem")
        }
    }

    private fun onForgotPasswordClicked() {
        val (isEmailValid, message) = loginViewModel.isEmailValid()
        if (!isEmailValid) showEmailError(message)
        else{
            loginViewModel.sendPasswordResetEmail()
        }
    }

    private fun showEmailError(message: String) {
        loginBinding.loginEmailField.setError(message)
    }

    private fun showPasswordError(message: String) {
        loginBinding.loginPasswordField.setError(message)
    }

    private fun updateUiBasedOnLoginResult(loginResult : LoginResultModel?) {
        if (loginResult?.isLoginSuccessful == true){ // can't be without the (== true) because its a Boolean? not Boolean
            Toast.makeText(this, "Login was successful", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Login was Successful")
            startHomeActivity(loginResult.userId)
        }
        else{
            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
        }
        if (loginResult == null){
            println("---------------------------")
            println("The result is null")
        }
    }

    private fun startHomeActivity(userId: String) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("userId", userId)
        startActivity(intent)
        finish()
    }
}


