package com.example.letschat.auth.server.remote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.letschat.auth.models.SignUpResultModel
import com.example.letschat.other.Event
import com.example.letschat.other.SingleLiveEvent
import com.example.letschat.user.User
import com.example.letschat.server.remote.FireBaseService


open class SignUpRepository(
    private val fireBaseService: FireBaseService
) {

    val signUpResult: MutableLiveData<Event<SignUpResultModel>> = MutableLiveData()
    val isUserNameTakenLiveData = SingleLiveEvent<Boolean>()

    fun signUp(userName: String, email: String, password: String) {
        fireBaseService.signUp(userName = userName, email = email, password = password)
        fireBaseService.signUpResult.observeForever{
            signUpResult.value = it
        }
    }

    fun isUserAlreadyLoggedIn(): Pair<Boolean, String> {
        val (isUserLoggedIn, uId) = fireBaseService.isUserAlreadyLoggedIn()
        return Pair(isUserLoggedIn, uId)
    }

    fun isUserNameAlreadyTaken(userName: String) {
        fireBaseService.isUserNameAlreadyTaken(userName)
        fireBaseService.isUserNameTakenLiveData.observeForever {
            isUserNameTakenLiveData.value = it
        }
    }
}