package com.example.letschat.auth.helper_classes

import com.example.letschat.home.server.local.LocalHomeRepository
import com.example.letschat.home.server.remote.HomeRepository
import com.example.letschat.home.view_models.HomeViewModel


class HomeViewModelFactory(
    private val validator: AuthValidator,
    private val homeRepository: HomeRepository,
    private val localHomeRepository: LocalHomeRepository
    ) : Factory<HomeViewModel> {
        override fun create(): HomeViewModel {
            return HomeViewModel(validator, homeRepository, localHomeRepository)
        }
}