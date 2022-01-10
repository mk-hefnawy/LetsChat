package com.example.letschat.application

import android.app.Application
import android.util.Log
import com.example.letschat.di.AppContainer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    lateinit var appContainer: AppContainer
    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(applicationContext)
        // checking if the user is signed in
        if (appContainer.auth.currentUser != null) {
            listenForDocumentChanges(appContainer.auth.currentUser!!.uid)
        }
    }

    private fun listenForDocumentChanges(uid: String) {
        val doc = appContainer.db.collection("users").document(uid)
        doc.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("HereApplication", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d("HereApplication", "Current data: ${snapshot.data}")
            } else {
                Log.d("HereApplication", "Current data: null")
            }
        }
    }
}