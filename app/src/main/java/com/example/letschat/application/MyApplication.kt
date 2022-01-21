package com.example.letschat.application

import android.app.Application
import android.util.Log
import com.example.letschat.di.AppContainer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application()
