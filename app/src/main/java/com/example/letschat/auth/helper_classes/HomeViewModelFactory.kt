package com.example.letschat.auth.helper_classes

import com.example.letschat.auth.view_models.LoginViewModel
import com.example.letschat.home.view_models.HomeViewModel


class HomeViewModelFactory : Factory<HomeViewModel> {
        override fun create(): HomeViewModel {
            return HomeViewModel()
        }
}