package com.example.letschat.auth.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.letschat.R
import com.example.letschat.auth.models.SignUpResultModel
import com.example.letschat.auth.usecases.SyncUseCase
import com.example.letschat.auth.view_models.SignUpViewModel
import com.example.letschat.databinding.FragmentSignUpBinding
import com.example.letschat.di.AppContainer
import com.example.letschat.home.view_models.HomeViewModel
import com.example.letschat.other.SIGNUP
import com.example.letschat.user.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment(), View.OnClickListener {
    private lateinit var appContainer: AppContainer
    private lateinit var signUpBinding: FragmentSignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var navController: NavController

    val homeViewModel: HomeViewModel by viewModels()
    companion object {
        private const val TAG = "SignUpFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()
        setOnClickListeners()
        observeIsUserNameAlreadyTaken()
        observeSignUpResult()
    }

    private fun setUp() {
        appContainer = AppContainer(requireContext())

        navController = findNavController()

        signUpBinding = DataBindingUtil.bind(view?.findViewById(R.id.sign_up_fragment_root)!!)!!
        signUpViewModel = appContainer.signUpViewModel
        signUpBinding.viewModel = signUpViewModel
        signUpBinding.lifecycleOwner = this
    }

    private fun observeSignUpResult() {
        signUpViewModel.signUpResult.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { result ->
                handleSignUpResult(result)
            }
        })
    }

    private fun observeIsUserNameAlreadyTaken() {
        signUpViewModel.isUserNameTakenLiveData.observe(viewLifecycleOwner, { res ->
            res.getContentIfNotHandled()?.let {
                if (it) {
                    showUserNameAlreadyTakenError()

                    Log.d("Here", "User Name is already taken")
                } else {
                    continueValidation()
                }
            }

        })
    }

    private fun setOnClickListeners() {
        signUpBinding.signUpButton.setOnClickListener(this)
        signUpBinding.goToLoginScreen.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            signUpBinding.signUpButton -> onSignUpButtonClicked()
            signUpBinding.goToLoginScreen -> onGoToLoginButtonClicked()
        }
    }

    private fun onGoToLoginButtonClicked() {
        navController.navigate(SignUpFragmentDirections.actionSignUpToLogin())
    }

    private fun onSignUpButtonClicked() {
        val (isUserNameValid, message) = signUpViewModel.isUserNameValid()
        if (!isUserNameValid) showUserNameError(message)
        else {
            lifecycleScope.launch(Dispatchers.Main) {
                signUpViewModel.isUserNameAlreadyTaken(signUpViewModel.userName)
            }
        }
    }

    private fun continueValidation() {
        val (isEmailValid, message) = signUpViewModel.isEmailValid()
        if (!isEmailValid) showEmailError(message)
        else {
            val (isPasswordValid, message) = signUpViewModel.isPasswordValid()
            if (!isPasswordValid) showPasswordError(message)
            else {
                signUpViewModel.signUp()
            }
        }
    }

    private fun handleSignUpResult(signUpResult: SignUpResultModel?) {
        if (signUpResult?.isSignUpSuccessful == true) { // can't be with the (== true) because its a Boolean? not Boolean
            addUserToRoomDatabase(signUpResult.user!!)

        } else {
            Toast.makeText(requireContext(), "Sign up failed", Toast.LENGTH_SHORT).show()

        }
    }

    private fun addUserToRoomDatabase(user: User) {
        lifecycleScope.launch(Dispatchers.Main) {
            signUpViewModel.addUserToRoomDatabase(user)
            signUpViewModel.insertStatusLiveData.observe(viewLifecycleOwner, { result ->
                result.getContentIfNotHandled()?.let {
                    if (it != -1L) {
                        Log.d("Here", "Room Success")
                        Log.d("Here", "User in Room with id: $it")
                        Toast.makeText(
                            requireContext(),
                            "Signed up successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUiForLoggedInUser()
                    }
                }
            })
        }
    }

    private fun updateUiForLoggedInUser() {
        SyncUseCase(requireContext() ,viewLifecycleOwner, homeViewModel, findNavController(), SIGNUP)
    }

    private fun showPasswordError(message: String) {
        signUpBinding.signUpPasswordField.setError(message)
    }

    private fun showEmailError(message: String) {
        signUpBinding.signUpEmailField.error = message
    }

    private fun showUserNameError(message: String) {
        signUpBinding.signUpUserNameField.error = message
    }

    private fun showUserNameAlreadyTakenError() {
        signUpBinding.signUpUserNameField.error = "User name is already taken"
    }
}