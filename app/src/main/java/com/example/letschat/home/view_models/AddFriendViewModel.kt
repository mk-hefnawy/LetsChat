package com.example.letschat.home.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.letschat.auth.helper_classes.AuthValidator
import com.example.letschat.home.server.remote.AddFriendRepository
import com.example.letschat.other.Event
import com.example.letschat.user.User

class AddFriendViewModel(
    private val validator: AuthValidator,
    private val  addFriendRepository: AddFriendRepository
): ViewModel() {
    var userNameOfFriend = ""
    val searchedUserLiveData = MutableLiveData<User>()
    val isUserAddFriendRequestSuccessful = MutableLiveData<Event<Boolean>>()

    fun searchUser(){
        addFriendRepository.searchUser(userNameOfFriend)
        addFriendRepository.potentialFriendLiveData.observeForever{
            searchedUserLiveData.value = it
        }
    }

    fun isUserNameValid(): Pair<Boolean, String> {
        val (isUserNameValid, message) = validator.isUserNameValid(userNameOfFriend)
        return Pair(isUserNameValid, message)
    }

    fun addUser(searchedUser: User){
        addFriendRepository.addUser(searchedUser)
        addFriendRepository.isUserAddFriendRequestSuccessful.observeForever{
            isUserAddFriendRequestSuccessful.value = it
        }
    }
}