package com.example.letschat.home.server.remote

import androidx.lifecycle.MutableLiveData
import com.example.letschat.other.Event
import com.example.letschat.server.remote.FireBaseService
import com.example.letschat.user.User

class FriendRequestsRepository(private val fireBaseService: FireBaseService) {

    val allReceivedFriendRequestsLiveData = MutableLiveData<List<User>>()
    val friendRequestReactionLiveData = MutableLiveData<Event<Pair<Boolean, String>>>()

    fun getAllFriendRequests(){
        fireBaseService.getAllFriendRequests()
        fireBaseService.allReceivedFriendRequestsLiveData.observeForever {
            allReceivedFriendRequestsLiveData.value = it
        }
    }

    fun reactToFriendRequest(user: User, eventType: String) {
        fireBaseService.reactToFriendRequest(user, eventType)
        fireBaseService.friendRequestReactionLiveData.observeForever {
            friendRequestReactionLiveData.value = it
        }
    }
}