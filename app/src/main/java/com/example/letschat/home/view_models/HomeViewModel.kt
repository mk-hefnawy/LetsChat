package com.example.letschat.home.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.letschat.auth.helper_classes.AuthValidator
import com.example.letschat.home.server.remote.HomeRepository
import com.example.letschat.other.SingleLiveEvent
import com.example.letschat.user.User


class HomeViewModel(
    private val validator: AuthValidator,
    private val homeRepository: HomeRepository,
) : ViewModel() {
    var userNameOfFriend: String = ""

    val userDocumentIdLiveData = SingleLiveEvent<String>()
    val userInfoLiveData = MutableLiveData<User>()
    val addFriendLiveData = SingleLiveEvent<Boolean>()

    fun isUserNameValid(): Pair<Boolean, String> {
        val (isUserNameValid, message) = validator.isUserNameValid(userNameOfFriend)
        return Pair(isUserNameValid, message)
    }

    /*fun addFriend(docId: String) {
        homeRepository.addFriend(docId, userNameOfFriend)
        homeRepository.addFriendLiveData.observeForever{
            addFriendLiveData.value = it
        }
    }*/

    fun getUserInfo(docId: String) {
        homeRepository.getUserInfo(docId)
        homeRepository.userInfoLiveData.observeForever{
            userInfoLiveData.value = it
        }
    }
}