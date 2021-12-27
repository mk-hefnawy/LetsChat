package com.example.letschat.auth.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.letschat.auth.helper_classes.AuthValidator
import com.example.letschat.auth.server.remote.SignUpRepository
import com.example.letschat.auth.models.SignUpResultModel
import com.example.letschat.auth.server.local.user.LocalUserRepository
import com.example.letschat.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.withContext

class SignUpViewModel(
    var user: User,
    private val validator: AuthValidator,
    private var signUpRepo: SignUpRepository,
    private val localUserRepository: LocalUserRepository
) : ViewModel() {

    var userName: String = ""
    var email: String = ""
    var password: String = ""


    val signUpResult = MutableLiveData<SignUpResultModel>()
    val insertStatusLiveData: MutableLiveData<Long> = MutableLiveData()

    fun isUserNameValid(): Pair<Boolean, String>{
        initUser()
        val (isUserNameValid, message) = validator.isUserNameValid(user.userName)
        return Pair(isUserNameValid, message)
    }

    fun isEmailValid(): Pair<Boolean, String>{
        val (isEmailValid, message) = validator.isEmailValid(user.email)
        return Pair(isEmailValid, message)
    }

    fun isPasswordValid(): Pair<Boolean, String>{
        val (isPasswordValid, message) = validator.isPasswordValid(user.password)
        return Pair(isPasswordValid, message)
    }

    private fun initUser(){
        user = User(userName= userName,email=  email, password= password)
        signUpRepo = SignUpRepository(user)
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

    suspend fun addUserToRoomDatabase(user: User) {
        withContext(Dispatchers.Main) {
            localUserRepository.addUserToRoom(user)
            localUserRepository.insertStatusLiveData.observeForever {
                insertStatusLiveData.postValue(it)
            }
        }
    }
}