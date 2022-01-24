package com.example.letschat.notifications.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class FCMService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d("Here", "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        //sendRegistrationToServer(token)
    }
}