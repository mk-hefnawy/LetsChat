package com.example.letschat.base

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.letschat.R
import com.example.letschat.databinding.FragmentBaseBinding
import com.example.letschat.di.AppContainer
import com.google.firebase.auth.FirebaseUser


class BaseFragment : Fragment(), View.OnClickListener {
    private lateinit var fragmentBaseBinding: FragmentBaseBinding
    private lateinit var navController: NavController
    private lateinit var appContainer: AppContainer

    companion object {
        const val TAG = "BaseFragmentHere"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_base, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initializations
        fragmentBaseBinding = DataBindingUtil.setContentView(requireActivity(), R.layout.fragment_base)
        appContainer = AppContainer(requireContext())
        navController = findNavController()
        setOnClickListeners()

    }

    private fun setOnClickListeners() {
      fragmentBaseBinding.goToLogin.setOnClickListener(this)
      fragmentBaseBinding.goToSignUp.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.go_to_login -> onLoginClicked()
            R.id.go_to_sign_up ->  onSignUpClicked()
        }
    }

    private fun onSignUpClicked() {
        Log.d("Here", "Sign Up Clicked")
        navController.navigate(BaseFragmentDirections.actionBaseToSignUp())
    }

    private fun onLoginClicked() {
        Log.d("Here", "Login Clicked")
        navController.navigate(BaseFragmentDirections.actionBaseToLogin())
    }

    private fun handleUiBasedOnSignInStatus(currentUser: FirebaseUser?) {
        if(currentUser == null){
            return
        }
        else{
            goToHomeFragment()
        }
    }

    private fun goToHomeFragment() {
        navController.navigate(BaseFragmentDirections.actionBaseToHome())
    }

    override fun onStart() {
        super.onStart()
        // so if the user is already signed in, they have to be directed to HomeActivity
        val currentUser = appContainer.auth.currentUser
        handleUiBasedOnSignInStatus(currentUser)

    }
}