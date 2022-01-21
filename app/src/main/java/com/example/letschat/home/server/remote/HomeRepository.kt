package com.example.letschat.home.server.remote

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.example.letschat.other.Event
import com.example.letschat.other.SingleLiveEvent
import com.example.letschat.server.remote.FireBaseService
import com.example.letschat.user.User

class HomeRepository(private val fireBaseService: FireBaseService) {

    val userInfoLiveData = MutableLiveData<User>()
    val addFriendLiveData = SingleLiveEvent<Boolean>()



   /* fun addFriend(docId: String, userName: String){
        fireBaseService.addFriend(docId, userName)
        fireBaseService.addFriendLiveData.observeForever{
            addFriendLiveData.value = it
        }
    }*/

    fun getUserInfo(docId: String) {
        fireBaseService.getUserInfo(docId)
        fireBaseService.userInfoLiveData.observeForever{
            userInfoLiveData.value = it
        }
    }



}