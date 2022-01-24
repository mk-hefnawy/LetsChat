package com.example.letschat.di

import android.content.Context
import androidx.navigation.NavController
import com.example.letschat.base.BaseActivity
import com.example.letschat.chatroom.helpers.Validator
import com.example.letschat.chatroom.ui.ChatMessagesAdapter
import com.example.letschat.home.adapters.ChatsAdapter
import com.example.letschat.home.adapters.GenericFriendsAdapter
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FragmentModule {
    @Provides
    fun provideGenericFriendsAdapter(@ApplicationContext context: Context): GenericFriendsAdapter{
        return GenericFriendsAdapter(context)
    }

    @Provides
    fun provideNavController(): NavController{
        return NavController(BaseActivity())
    }

    @Provides
    fun provideValidator(): Validator{
        return Validator()
    }

    @Provides
    fun provideChatMessageAdapter(auth: FirebaseAuth): ChatMessagesAdapter{
        return ChatMessagesAdapter(auth)
    }

    @Provides
    fun provideChatsAdapter(auth: FirebaseAuth, @ApplicationContext context: Context): ChatsAdapter{
        return ChatsAdapter(auth, context)
    }

}