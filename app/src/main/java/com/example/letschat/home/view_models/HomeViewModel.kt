package com.example.letschat.home.view_models

import androidx.lifecycle.ViewModel
import com.example.letschat.auth.helper_classes.AuthValidator

class HomeViewModel(
    val validator: AuthValidator
): ViewModel() {
    var userNameOfFriend: String = ""

    fun isUserNameValid(): Pair<Boolean, String> {
        val (isUserNameValid, message) = validator.isUserNameValid()
        return Pair(isUserNameValid, message)
    }

        fun addFriend(){

    }
}