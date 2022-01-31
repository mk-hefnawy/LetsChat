package com.example.letschat.home.server.remote

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.example.letschat.chatroom.chat.ChatRoom
import com.example.letschat.other.Event
import com.example.letschat.other.SingleLiveEvent
import com.example.letschat.server.remote.FireBaseService
import com.example.letschat.user.User

class HomeRepository(private val fireBaseService: FireBaseService) {

    val userInfoLiveData = MutableLiveData<User>()
    val userDataInServerLiveData = MutableLiveData<Event<User>>()
    val userChatsInServerLiveData = MutableLiveData<Event<List<ChatRoom>>>()


    fun getUserInfo(docId: String) {
        fireBaseService.getUserInfo(docId)
        fireBaseService.userInfoLiveData.observeForever{
            userInfoLiveData.value = it
        }
    }

    fun getUserDataFromServer() {
        fireBaseService.getUserDataFromServer()
        fireBaseService.userDataInServerLiveData.observeForever { user->
            userDataInServerLiveData.value = user
        }
    }

    fun getUserChatsFromServer() {
        fireBaseService.getUserChatsFromServer()
        fireBaseService.userChatsInServerLiveData.observeForever {
            userChatsInServerLiveData.value = it
        }
    }


}