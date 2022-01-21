package com.example.letschat.home.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.letschat.chatroom.chat.ChatMessage
import com.example.letschat.home.server.local.LocalChatsRepository
import com.example.letschat.home.server.remote.RemoteChatsRepository
import com.example.letschat.other.Event
import com.example.letschat.server.remote.FireBaseService
import com.example.letschat.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    val localChatsRepository: LocalChatsRepository,
    val remoteChatsRepository: RemoteChatsRepository
) : ViewModel() {

    val allChatsLiveData = MutableLiveData<Event<List<String>>>()
    val listOfLastMessagesForEachChatLiveData = MutableLiveData<List<ChatMessage?>>()
    val listOfChattingUsersWithTheLastMessageLiveData = MutableLiveData<List<Pair<ChatMessage, User>>>()

    fun getAllChatsFromCache() {
        viewModelScope.launch {
            localChatsRepository.getAllChatsFromCache()
        }
        localChatsRepository.allChatsLiveData.observeForever {
            allChatsLiveData.value = it
        }
    }

    fun getLastMessageOfEachChat(listOfChatDocsIds: List<String>?) {
        remoteChatsRepository.getLastMessageOfEachChat(listOfChatDocsIds)
        remoteChatsRepository.listOfLastMessagesForEachChatLiveData.observeForever {
            listOfLastMessagesForEachChatLiveData.value = it
        }
    }

    fun getTheChattingUsers(listOfLastMessages: List<ChatMessage?>){
        remoteChatsRepository.getTheChattingUsers(listOfLastMessages)
        remoteChatsRepository.listOfChattingUsersWithTheLastMessageLiveData.observeForever {
            listOfChattingUsersWithTheLastMessageLiveData.value = it
        }
    }

}