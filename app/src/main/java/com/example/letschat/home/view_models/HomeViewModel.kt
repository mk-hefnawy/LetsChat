package com.example.letschat.home.view_models

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.letschat.auth.helper_classes.AuthValidator
import com.example.letschat.home.server.local.LocalHomeRepository
import com.example.letschat.home.server.remote.HomeRepository
import com.example.letschat.other.Event
import com.example.letschat.other.SingleLiveEvent
import com.example.letschat.server.remote.FireBaseService
import com.example.letschat.user.User
import kotlinx.coroutines.launch


class HomeViewModel(
    private val validator: AuthValidator,
    private val homeRepository: HomeRepository,
    private val localHomeRepository: LocalHomeRepository
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
        homeRepository.userInfoLiveData.observeForever {
            userInfoLiveData.value = it
        }
    }


    fun deleteAllUsersFromCache() {
        viewModelScope.launch {
            localHomeRepository.deleteAllUsers()
        }
    }


}