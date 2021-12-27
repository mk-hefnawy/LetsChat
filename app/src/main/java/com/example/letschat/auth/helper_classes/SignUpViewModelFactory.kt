package com.example.letschat.auth.helper_classes

import com.example.letschat.auth.server.local.user.LocalUserRepository
import com.example.letschat.auth.server.remote.SignUpRepository
import com.example.letschat.auth.view_models.SignUpViewModel
import com.example.letschat.user.User

class SignUpViewModelFactory(
    private val user: User,
    private val validator: AuthValidator,
    private val signUpRepository: SignUpRepository,
    private val localUserRepository: LocalUserRepository

    ): Factory<SignUpViewModel> {
        override fun create(): SignUpViewModel {
            return SignUpViewModel(user, validator, signUpRepository, localUserRepository)
        }
}