package com.example.letschat.home.server.remote

import androidx.lifecycle.MutableLiveData
import com.example.letschat.other.Event
import com.example.letschat.server.remote.FireBaseService
import com.example.letschat.user.User

class AddFriendRepository(
    private val fireBaseService: FireBaseService
) {
    val potentialFriendLiveData = MutableLiveData<User?>()
    val isUserAddFriendRequestSuccessful = MutableLiveData<Event<Boolean>>()

    fun searchUser(userNameOfUser: String){
        fireBaseService.searchUser(userNameOfUser)
        fireBaseService.searchedUserLiveData.observeForever{
            potentialFriendLiveData.value = it
        }
    }

    fun addUser(searchedUser: User){
        fireBaseService.addUser(searchedUser)
        fireBaseService.isUserAddFriendRequestSuccessful.observeForever {
            isUserAddFriendRequestSuccessful.value = it
        }
    }
}