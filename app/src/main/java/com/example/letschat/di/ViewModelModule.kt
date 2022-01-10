package com.example.letschat.di

import com.example.letschat.chatroom.chat.ChatRoomRepository
import com.example.letschat.home.server.remote.FriendsRepository
import com.example.letschat.server.remote.FireBaseService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {

    @Provides
    fun provideFriendsRepository(fireBaseService: FireBaseService):FriendsRepository{
        return FriendsRepository(fireBaseService)
    }

    @Provides
    fun provideChatRoomRepository(fireBaseService: FireBaseService): ChatRoomRepository{
        return ChatRoomRepository(fireBaseService)
    }
}