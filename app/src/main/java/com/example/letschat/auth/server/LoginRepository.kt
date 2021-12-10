package com.example.letschat.auth.server

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.letschat.auth.models.LoginResultModel
import com.example.letschat.user.User
import com.example.letschat.server.FireBaseService

open class LoginRepository(var user: User) {

    protected val fireBaseService = FireBaseService(user)

    val loginResultLiveData: MutableLiveData<LoginResultModel> = MutableLiveData()


    fun loginWithEmailAndPassword(){
        fireBaseService.user = user
        fireBaseService.loginWithEmailAndPassword()
        fireBaseService.signInResultLiveData.observeForever(Observer {
            loginResultLiveData.value = it
        })
        }

    fun sendPasswordResetEmail(email: String) {
       fireBaseService.sendPasswordResetEmail(email)
    }
}
