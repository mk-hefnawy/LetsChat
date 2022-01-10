package com.example.letschat.auth.helper_classes

import com.example.letschat.auth.server.local.user.LocalAuthRepository
import com.example.letschat.auth.server.remote.SignUpRepository
import com.example.letschat.auth.view_models.SignUpViewModel
import com.example.letschat.user.User

class SignUpViewModelFactory(
    private val validator: AuthValidator,
    private val signUpRepository: SignUpRepository,
    private val localAuthRepository: LocalAuthRepository

    ): Factory<SignUpViewModel> {
        override fun create(): SignUpViewModel {
            return SignUpViewModel(validator, signUpRepository, localAuthRepository)
        }
}