package com.example.letschat.di

import android.content.Context
import com.example.letschat.server.local.DataStorePreferences.DataStoreImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideDataStoreImp(@ApplicationContext context: Context): DataStoreImp = DataStoreImp(context)
}