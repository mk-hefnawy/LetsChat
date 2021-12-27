package com.example.letschat.home.view_models

import androidx.lifecycle.ViewModel
import com.example.letschat.auth.helper_classes.AuthValidator
import com.example.letschat.home.server.HomeRepository

class HomeViewModel(
    private val validator: AuthValidator,
    private val homeRepository: HomeRepository
): ViewModel() {
    var userNameOfFriend: String = ""

    fun isUserNameValid(): Pair<Boolean, String> {
        val (isUserNameValid, message) = validator.isUserNameValid(userNameOfFriend)
        return Pair(isUserNameValid, message)
    }

        fun addFriend(){
            homeRepository.addFriend(userNameOfFriend)
    }
}