package com.example.letschat.application

import android.app.Application
import android.util.Log
import com.example.letschat.chatroom.chat.ChatMessage
import com.example.letschat.di.AppContainer
import com.example.letschat.server.remote.FireBaseService
import com.google.firebase.firestore.DocumentChange
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application()
