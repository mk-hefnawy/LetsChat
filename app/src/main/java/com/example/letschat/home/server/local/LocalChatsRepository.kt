package com.example.letschat.home.server.local

import androidx.lifecycle.MutableLiveData
import com.example.letschat.other.Event
import com.example.letschat.server.local.RoomService
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class LocalChatsRepository @Inject constructor(
    val roomService: RoomService,
    val auth: FirebaseAuth
) {

    val allChatsLiveData = MutableLiveData<Event<List<String>>>()

    suspend fun getAllChatsFromCache() {
        roomService.getAllChatsFromCache(auth.currentUser?.uid!!)
        roomService.allChatsLiveData.observeForever {
            allChatsLiveData.value = it
        }
    }

}