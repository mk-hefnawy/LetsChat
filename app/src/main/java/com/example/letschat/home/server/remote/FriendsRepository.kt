package com.example.letschat.home.server.remote

import androidx.lifecycle.MutableLiveData
import com.example.letschat.server.remote.FireBaseService
import com.example.letschat.user.User
import javax.inject.Inject


class FriendsRepository @Inject constructor(val fireBaseService: FireBaseService) {
    val friendsLiveData = MutableLiveData<List<User>>()
    fun getAllFriends() {
        fireBaseService.getAllFriends()
        fireBaseService.friendsLiveData.observeForever { users->
            users.forEach{ user ->
                user.friendsOrNot = true
            }
            friendsLiveData.value = users
        }
    }
}