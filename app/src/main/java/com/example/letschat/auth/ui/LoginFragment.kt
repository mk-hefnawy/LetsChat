package com.example.letschat.auth.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.letschat.R
import com.example.letschat.auth.view_models.LoginViewModel
import com.example.letschat.databinding.FragmentLoginBinding
import com.example.letschat.di.AppContainer

class LoginFragment : Fragment(), View.OnClickListener {
    private lateinit var loginFragmentBinding: FragmentLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var appContainer: AppContainer
    private lateinit var navController: NavController


    companion object {
        const val TAG = "LoginFragmentHere"
        private const val GOOGLE_SIGN_IN_REQUEST_CODE = 300

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()
        setOnClickListeners()
        observeLoginResult()
    }

    private fun setUp() {
        loginFragmentBinding = DataBindingUtil.setContentView(
            requireActivity(), R.layout.fragment_login
        )
        appContainer = AppContainer(requireContext())

        loginViewModel = appContainer.loginViewModel

        loginFragmentBinding.viewModel = loginViewModel
        loginFragmentBinding.lifecycleOwner = viewLifecycleOwner
        navController = findNavController()
    }

    private fun observeLoginResult() {
        loginViewModel.loginLiveData.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { result ->
                updateUiBasedOnLoginResult(result)
            }
        })
    }


    private fun setOnClickListeners() {
        loginFragmentBinding.goToSignUpScreen.setOnClickListener(this)
        loginFragmentBinding.loginButton.setOnClickListener(this)
        loginFragmentBinding.forgotPasswordTextView.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0!!) {
            loginFragmentBinding.goToSignUpScreen -> goToSignUpFragment()
            loginFragmentBinding.googleButton -> onGoogleButtonClicked()
            loginFragmentBinding.loginButton -> onLoginButtonClicked()
            loginFragmentBinding.forgotPasswordTextView -> onForgotPasswordClicked()
            else -> println("Click Problem")
        }
    }

    private fun onLoginButtonClicked() {
        val (isEmailValid, message) = loginViewModel.isEmailValid()
        if (!isEmailValid) showEmailError(message)
        else {
            val (isPasswordValid, message) = loginViewModel.isPasswordValid()
            if (!isPasswordValid) showPasswordError(message)
            else {
                loginViewModel.loginWithEmailAndPassword()
            }
        }
    }

    private fun updateUiBasedOnLoginResult(isLoginSuccessful: Boolean) {
        if (isLoginSuccessful) { // can't be without the (== true) because its a Boolean? not Boolean
            Toast.makeText(requireContext(), "Login was successful", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Login was Successful")
            goToHomeFragment()
        } else {
            Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Login failed")
        }
    }

    private fun goToHomeFragment() {
        navController.navigate(LoginFragmentDirections.actionLoginToHome())
    }

    private fun showPasswordError(message: String) {
        loginFragmentBinding.loginPasswordField.setError(message)
    }

    private fun showEmailError(message: String) {
        loginFragmentBinding.loginEmailField.setError(message)
    }


    private fun onForgotPasswordClicked() {
        val (isEmailValid, message) = loginViewModel.isEmailValid()
        if (!isEmailValid) showEmailError(message)
        else {
            loginViewModel.sendPasswordResetEmail()
        }
    }


    private fun onGoogleButtonClicked() {

    }

    private fun goToSignUpFragment() {
        navController.navigate(LoginFragmentDirections.actionLoginToSignUp())
    }
}