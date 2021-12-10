package com.example.letschat.auth.server

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.letschat.auth.models.SignUpResultModel
import com.example.letschat.user.User
import com.example.letschat.server.FireBaseService


open class SignUpRepository(val user: User) {

    lateinit var fireBaseService: FireBaseService
    val signUpResult: MutableLiveData<SignUpResultModel> = MutableLiveData()

    protected fun checkIfEmailOrUserNameAreAlreadyInDB(){

    }

    fun signUp(){
        fireBaseService = FireBaseService(user)
        print(user.userName)
        fireBaseService.signUp()
        fireBaseService.signUpResult.observeForever(Observer {
            signUpResult.value = it
        })
    }

    fun isUserAlreadyLoggedIn(): Pair<Boolean, String>{
        fireBaseService = FireBaseService(user)
        val (isUserLoggedIn, uId) = fireBaseService.isUserAlreadyLoggedIn()
        return Pair(isUserLoggedIn, uId)
    }
}