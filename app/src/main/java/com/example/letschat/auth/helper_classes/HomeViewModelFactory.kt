package com.example.letschat.auth.helper_classes

import com.example.letschat.home.server.HomeRepository
import com.example.letschat.home.view_models.HomeViewModel


class HomeViewModelFactory(
    private val validator: AuthValidator,
    private val homeRepository: HomeRepository
    ) : Factory<HomeViewModel> {
        override fun create(): HomeViewModel {
            return HomeViewModel(validator, homeRepository)
        }
}