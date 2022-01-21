package com.example.letschat.home.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.letschat.home.server.remote.FriendsRepository
import com.example.letschat.other.Event
import com.example.letschat.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
     private val friendsRepository: FriendsRepository
) : ViewModel() {

    val friendsLiveData = MutableLiveData<Event<List<User>>>()

    fun getAllFriends(){
        friendsRepository.getAllFriends()
        friendsRepository.friendsLiveData.observeForever {
            friendsLiveData.value = it
        }
    }
}