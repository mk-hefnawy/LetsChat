package com.example.letschat.home.view_models

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.letschat.auth.helper_classes.AuthValidator
import com.example.letschat.chatroom.chat.ChatRoom
import com.example.letschat.home.server.local.LocalHomeRepository
import com.example.letschat.home.server.remote.HomeRepository
import com.example.letschat.other.Event
import com.example.letschat.other.SingleLiveEvent
import com.example.letschat.server.remote.FireBaseService
import com.example.letschat.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
     val homeRepository: HomeRepository,
     val localHomeRepository: LocalHomeRepository
) : ViewModel() {
    var userNameOfFriend: String = ""

    val userDocumentIdLiveData = SingleLiveEvent<String>()
    val userInfoLiveData = MutableLiveData<User>()
    val userDataFromServerLiveData = MutableLiveData<Event<User>>()
    val updateUserDataInCacheResultLiveData = MutableLiveData<Event<Long>>()

    val userChatsInServerLiveData = MutableLiveData<Event<List<ChatRoom>>>()
    val updateUserChatsInCacheLiveData = MutableLiveData<Event<List<Long>>>()

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

    fun getUserDataFromServer() {
        homeRepository.getUserDataFromServer()
        homeRepository.userDataInServerLiveData.observeForever{ userInServer ->
            userDataFromServerLiveData.value = userInServer
        }
    }

    fun updateUserInCacheWithServerData(serverData: User?) {
        viewModelScope.launch {
            localHomeRepository.updateUserInCacheWithServerData(serverData)
            localHomeRepository.updateUserDataInCacheResultLiveData.observeForever {
                updateUserDataInCacheResultLiveData.value = it
            }
        }
    }

    fun getUserChatsFromBothServerAndCache() {
        homeRepository.getUserChatsFromServer()
        homeRepository.userChatsInServerLiveData.observeForever {
            userChatsInServerLiveData.value = it
        }
    }

    fun updateUserChatsInCache(userChatsInServer: List<ChatRoom>?) {
        viewModelScope.launch {
            localHomeRepository.updateUserChatsInCache(userChatsInServer)
            localHomeRepository.updateUserChatsInCacheLiveData.observeForever {
                updateUserChatsInCacheLiveData.value = it
            }
        }

    }

}