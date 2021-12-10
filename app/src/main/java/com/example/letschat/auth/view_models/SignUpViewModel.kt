package com.example.letschat.auth.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.letschat.auth.helper_classes.AuthValidator
import com.example.letschat.auth.server.SignUpRepository
import com.example.letschat.auth.models.SignUpResultModel
import com.example.letschat.user.User

class SignUpViewModel : ViewModel() {
    var userName: String = ""
    var email: String = ""
    var password: String = ""

    lateinit var user: User
    var validator=  AuthValidator(User("", "", ""))
    var signUpRepo=  SignUpRepository(User("", "", ""))
    val signUpResult = MutableLiveData<SignUpResultModel>()


    fun isUserNameValid(): Pair<Boolean, String>{
        initUser()
        val (isUserNameValid, message) = validator.isUserNameValid()
        return Pair(isUserNameValid, message)
    }

    fun isEmailValid(): Pair<Boolean, String>{
        val (isEmailValid, message) = validator.isEmailValid()
        return Pair(isEmailValid, message)
    }

    fun isPasswordValid(): Pair<Boolean, String>{
        val (isPasswordValid, message) = validator.isPasswordValid()
        return Pair(isPasswordValid, message)
    }

    private fun initUser(){
        user = User(userName, email, password)
        signUpRepo = SignUpRepository(user)
        validator = AuthValidator(user)
    }

    fun signUp(){
        print(user.userName)
        signUpRepo.signUp()
        signUpRepo.signUpResult.observeForever(Observer {
            signUpResult.value = it
        })
    }

    fun isUserAlreadyLoggedIn(): Pair<Boolean, String>{
        val (isUserLoggedIn, uId) = signUpRepo.isUserAlreadyLoggedIn()
        return Pair(isUserLoggedIn, uId)
    }

}