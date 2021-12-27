package com.example.letschat.auth.server.remote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.letschat.auth.models.LoginResultModel
import com.example.letschat.user.User
import com.example.letschat.server.FireBaseService

open class LoginRepository {

    protected val fireBaseService = FireBaseService()

    val loginResultLiveData: MutableLiveData<LoginResultModel> = MutableLiveData()


    fun loginWithEmailAndPassword(email: String, password: String){

        fireBaseService.loginWithEmailAndPassword(email, password)
        fireBaseService.signInResultLiveData.observeForever(Observer {
            loginResultLiveData.value = it
        })
        }

    fun sendPasswordResetEmail(email: String) {
       fireBaseService.sendPasswordResetEmail(email)
    }
}
