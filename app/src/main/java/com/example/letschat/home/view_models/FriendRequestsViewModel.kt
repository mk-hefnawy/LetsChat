package com.example.letschat.home.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.letschat.home.server.remote.FriendRequestsRepository
import com.example.letschat.other.Event
import com.example.letschat.server.remote.FireBaseService
import com.example.letschat.user.User

class FriendRequestsViewModel(private val friendRequestsRepository: FriendRequestsRepository): ViewModel() {

    val allReceivedFriendRequestsLiveData = MutableLiveData<List<User>>()
    val friendRequestReactionLiveData = MutableLiveData<Event<FireBaseService.obj>>()

    fun getAllFriendRequests(){
        friendRequestsRepository.getAllFriendRequests()
        friendRequestsRepository.allReceivedFriendRequestsLiveData.observeForever {
            allReceivedFriendRequestsLiveData.value = it
        }
    }

    fun reactToFriendRequest(user: User, eventType: String) {
        friendRequestsRepository.reactToFriendRequest(user, eventType)
        friendRequestsRepository.friendRequestReactionLiveData.observeForever {
            friendRequestReactionLiveData.value = it
        }
    }
}