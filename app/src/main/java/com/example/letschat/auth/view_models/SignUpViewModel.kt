package com.example.letschat.auth.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.letschat.auth.helper_classes.AuthValidator
import com.example.letschat.auth.server.remote.SignUpRepository
import com.example.letschat.auth.models.SignUpResultModel
import com.example.letschat.auth.server.local.user.LocalAuthRepository
import com.example.letschat.other.Event
import com.example.letschat.other.SingleLiveEvent
import com.example.letschat.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SignUpViewModel(
    private val validator: AuthValidator,
    private var signUpRepo: SignUpRepository,
    private val localAuthRepository: LocalAuthRepository
) : ViewModel() {

    var userName: String = ""
    var email: String = ""
    var password: String = ""


    val signUpResult = MutableLiveData<Event<SignUpResultModel>>()
    val insertStatusLiveData: MutableLiveData<Long> = MutableLiveData()
    val isUserNameTakenLiveData = SingleLiveEvent<Boolean>()


    fun isUserNameValid(): Pair<Boolean, String>{
        val (isUserNameValid, message) = validator.isUserNameValid(userName)
        return Pair(isUserNameValid, message)
    }

    fun isEmailValid(): Pair<Boolean, String>{
        val (isEmailValid, message) = validator.isEmailValid(email)
        return Pair(isEmailValid, message)
    }

    fun isPasswordValid(): Pair<Boolean, String>{
        val (isPasswordValid, message) = validator.isPasswordValid(password)
        return Pair(isPasswordValid, message)
    }

    fun signUp(){
        signUpRepo.signUp(userName, email, password)
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
            localAuthRepository.addUserToRoom(user)
            localAuthRepository.insertStatusLiveData.observeForever {
                insertStatusLiveData.postValue(it)
            }
        }
    }

    suspend fun isUserNameAlreadyTaken(userName: String) {
        signUpRepo.isUserNameAlreadyTaken(userName)
        signUpRepo.isUserNameTakenLiveData.observeForever{
            isUserNameTakenLiveData.value = it
        }
    }
}