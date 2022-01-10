package com.example.letschat.auth.server.remote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.letschat.auth.models.LoginResultModel
import com.example.letschat.other.Event
import com.example.letschat.other.SingleLiveEvent
import com.example.letschat.server.remote.FireBaseService
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlin.math.log

open class LoginRepository(
    private val fireBaseService: FireBaseService
) {
    val loginLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()


    fun loginWithEmailAndPassword(email: String, password: String) {
        fireBaseService.loginWithEmailAndPassword(email, password)
        fireBaseService.loginLiveData.observeForever {
            loginLiveData.value = it
        }
    }

    fun sendPasswordResetEmail(email: String) {
        fireBaseService.sendPasswordResetEmail(email)
    }
}
