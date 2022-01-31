package com.example.letschat.base

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.letschat.chatroom.chat.ChatMessage
import com.example.letschat.other.Event
import com.example.letschat.server.remote.FireBaseService
import com.google.firebase.firestore.DocumentChange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BaseRepository @Inject constructor(val fireBaseService: FireBaseService){

    val uploadImageLiveData = MutableLiveData<Event<FireBaseService.UploadImageObject>>()
    val addedMessagesLiveData =  MutableLiveData<ArrayList<ChatMessage>>()

    fun uploadImage(bitmap: Bitmap) {
        fireBaseService.uploadImage(bitmap)
        fireBaseService.uploadImageLiveData.observeForever {
            uploadImageLiveData.value = it
        }
    }

     fun listenToChatsChanges() {
        fireBaseService.listenToChatsChanges()
        fireBaseService.addedMessagesLiveData.observeForever {
            addedMessagesLiveData.value = it
        }
    }
}