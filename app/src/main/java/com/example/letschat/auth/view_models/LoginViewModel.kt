package com.example.letschat.auth.view_models

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.letschat.auth.helper_classes.AuthValidator
import com.example.letschat.auth.helper_classes.FederatedLoginManager
import com.example.letschat.auth.server.remote.LoginRepository
import com.example.letschat.auth.models.LoginResultModel
import com.example.letschat.other.Event
import com.example.letschat.other.SingleLiveData
import com.example.letschat.other.SingleLiveEvent
import com.example.letschat.user.User
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class LoginViewModel(
    private val validator: AuthValidator,
    private val loginRepository: LoginRepository,
    private val federatedLoginManager: FederatedLoginManager
) : ViewModel() {

    var email: String = ""
    var password: String = ""

    val loginLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()

    fun loginWithEmailAndPassword() {
        loginRepository.loginWithEmailAndPassword(email, password)
        loginRepository.loginLiveData.observeForever {
            loginLiveData.value = it
        }
    }

    fun loginWithGoogle(context: Context) {
        // init()
        federatedLoginManager.loginWithGoogle(context)
    }

    fun isEmailValid(): Pair<Boolean, String> {
        val (isEmailValid, message) = validator.isEmailValid(email)
        return Pair(isEmailValid, message)
    }

    fun isPasswordValid(): Pair<Boolean, String> {
        val (isPasswordValid, message) = validator.isPasswordValid(password)
        return Pair(isPasswordValid, message)
    }

    fun sendPasswordResetEmail() {
        loginRepository.sendPasswordResetEmail(email)
    }
}