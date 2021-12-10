package com.example.letschat.auth.view_models

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.letschat.auth.helper_classes.AuthValidator
import com.example.letschat.auth.helper_classes.FederatedLoginManager
import com.example.letschat.auth.server.LoginRepository
import com.example.letschat.auth.models.LoginResultModel
import com.example.letschat.user.User

class LoginViewModel(
    var user: User,
    private val validator: AuthValidator,
    private val loginRepository: LoginRepository,
    private val federatedLoginManager: FederatedLoginManager)
    : ViewModel() {

    var email: String = ""
    var password: String = ""

    val loginLiveData: MutableLiveData<LoginResultModel> = MutableLiveData()


    fun loginWithEmailAndPassword(){
        init()
        loginRepository.loginWithEmailAndPassword()
        loginRepository.loginResultLiveData.observeForever(Observer {
            loginLiveData.value = it
        })
    }

    fun loginWithGoogle(context: Context){
        init()
        federatedLoginManager.loginWithGoogle(context)
    }


    private fun init(){
        user.email = email
        user.password = password

        validator.user = user
        loginRepository.user = user
    }

    fun isEmailValid(): Pair<Boolean, String>{
        init()
        val (isEmailValid, message) = validator.isEmailValid()
        return Pair(isEmailValid, message)
    }

    fun isPasswordValid(): Pair<Boolean, String>{
        val (isPasswordValid, message) = validator.isPasswordValid()
        return Pair(isPasswordValid, message)
    }

    fun sendPasswordResetEmail() {
        loginRepository.sendPasswordResetEmail(user.email)
    }
}