package com.example.letschat.di

import androidx.navigation.NavController
import com.example.letschat.base.BaseActivity
import com.example.letschat.chatroom.helpers.Validator
import com.example.letschat.home.adapters.GenericFriendsAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FragmentModule {
    @Provides
    fun provideGenericFriendsAdapter(): GenericFriendsAdapter{
        return GenericFriendsAdapter()
    }

    @Provides
    fun provideNavController(): NavController{
        return NavController(BaseActivity())
    }

    @Provides
    fun provideValidator(): Validator{
        return Validator()
    }

}