package com.example.letschat.home.server.remote

import androidx.lifecycle.MutableLiveData
import com.example.letschat.other.Event
import com.example.letschat.server.remote.FireBaseService
import com.example.letschat.user.FriendShipStatus
import com.example.letschat.user.User
import javax.inject.Inject


class FriendsRepository @Inject constructor(val fireBaseService: FireBaseService) {
    val friendsLiveData = MutableLiveData<Event<List<User>>>()
    fun getAllFriends() {
        fireBaseService.getAllFriends()
        fireBaseService.friendsLiveData.observeForever { usersEvent->
            usersEvent.getContentIfNotHandled()?.let { users ->
                users.forEach{ user ->
                    user.friendShipStatus = FriendShipStatus.FRIENDS
                }
                friendsLiveData.value = Event(users)
            }

        }
    }
}