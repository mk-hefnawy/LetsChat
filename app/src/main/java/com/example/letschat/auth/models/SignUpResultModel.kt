package com.example.letschat.auth.models

import com.example.letschat.user.User

data class SignUpResultModel(val isSignUpSuccessful: Boolean,
                             val errorType: String,
                             val user: User?)