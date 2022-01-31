package com.example.letschat.di

import com.example.letschat.base.BaseRepository
import com.example.letschat.base.LocalBaseRepository
import com.example.letschat.chatroom.data.local.LocalChatRoomRepository
import com.example.letschat.chatroom.data.remote.ChatRoomRepository
import com.example.letschat.home.server.local.LocalHomeRepository
import com.example.letschat.home.server.remote.FriendsRepository
import com.example.letschat.home.server.remote.HomeRepository
import com.example.letschat.server.local.RoomService
import com.example.letschat.server.remote.FireBaseService
import com.google.firebase.auth.FirebaseAuth
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
    fun provideHomeRepository(fireBaseService: FireBaseService): HomeRepository{
        return HomeRepository(fireBaseService)
    }

    @Provides
    fun provideLocalHomeRepository(roomService: RoomService, auth: FirebaseAuth): LocalHomeRepository{
        return LocalHomeRepository(roomService, auth)
    }

    @Provides
    fun provideChatRoomRepository(fireBaseService: FireBaseService): ChatRoomRepository {
        return ChatRoomRepository(fireBaseService)
    }

    @Provides
    fun provideLocalChatRoomRepository(roomService: RoomService, auth: FirebaseAuth): LocalChatRoomRepository {
        return LocalChatRoomRepository(roomService, auth)
    }

    @Provides
    fun provideLocalBaseRepository(roomService: RoomService, auth: FirebaseAuth): LocalBaseRepository {
        return LocalBaseRepository(roomService, auth)
    }

    @Provides
    fun provideBaseRepository(fireBaseService: FireBaseService): BaseRepository {
        return BaseRepository(fireBaseService)
    }
}