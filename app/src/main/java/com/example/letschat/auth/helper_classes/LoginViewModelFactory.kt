package com.example.letschat.auth.helper_classes

import com.example.letschat.user.User
import com.example.letschat.auth.server.remote.LoginRepository
import com.example.letschat.auth.view_models.LoginViewModel


class LoginViewModelFactory(
    private val validator: AuthValidator,
    private val loginRepository: LoginRepository,
    private val federatedLoginManager: FederatedLoginManager

): Factory<LoginViewModel> {
    override fun create(): LoginViewModel {
        return LoginViewModel(validator, loginRepository, federatedLoginManager)
    }
}