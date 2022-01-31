package com.example.letschat.home.server.local

import androidx.lifecycle.MutableLiveData
import com.example.letschat.chatroom.chat.ChatRoom
import com.example.letschat.other.Event
import com.example.letschat.server.local.RoomService
import com.example.letschat.user.User
import com.google.firebase.auth.FirebaseAuth


class LocalHomeRepository(val roomService: RoomService, val auth:FirebaseAuth){

    val userDataInCacheLiveData = MutableLiveData<Event<User?>>()
    val updateUserDataInCacheResultLiveData = MutableLiveData<Event<Long>>()

    val updateUserChatsInCacheLiveData = MutableLiveData<Event<List<Long>>>()

    suspend fun deleteAllUsers(){
        roomService.deleteAllUsers()
    }

     suspend fun getUserDataFromCache() {
     roomService.getUserDataFromCache(auth.currentUser!!.uid)
        roomService.userDataInCacheLiveData.observeForever {
            userDataInCacheLiveData.value = it
        }
    }

    suspend fun updateUserInCacheWithServerData(serverData: User?) {
        roomService.updateUserInCacheWithServerData(serverData)
        roomService.updateUserDataInCacheResultLiveData.observeForever {
            updateUserDataInCacheResultLiveData.value = it
        }
    }

    suspend fun updateUserChatsInCache(userChatsInServer: List<ChatRoom>?) {
        roomService.updateUserChatsInCache(userChatsInServer)
        roomService.updateUserChatsInCacheLiveData.observeForever {
            updateUserChatsInCacheLiveData.value = it
        }
    }


}