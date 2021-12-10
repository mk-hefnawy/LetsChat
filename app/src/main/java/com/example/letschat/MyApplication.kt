package com.example.letschat

import android.app.Application
import com.example.letschat.dependency_injection.AppContainer

class MyApplication: Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}