package com.example.letschat.home.server.remote

import androidx.lifecycle.MutableLiveData
import com.example.letschat.chatroom.chat.ChatMessage
import com.example.letschat.other.Event
import com.example.letschat.server.remote.FireBaseService
import com.example.letschat.user.User
import javax.inject.Inject

class RemoteChatsRepository @Inject constructor(
    val firebaseService: FireBaseService
) {
    val listOfLastMessagesForEachChatLiveData = MutableLiveData<List<ChatMessage?>>()
    val listOfChattingUsersWithTheLastMessageLiveData = MutableLiveData<List<Pair<ChatMessage, User>>>()


    fun getLastMessageOfEachChat(listOfChatDocsIds: List<String>?) {
        firebaseService.getLastMessageOfEachChat(0, listOfChatDocsIds)
        firebaseService.listOfLastMessagesForEachChatLiveData.observeForever {
            listOfLastMessagesForEachChatLiveData.value = it
        }
    }

    fun getTheChattingUsers(listOfLastMessages: List<ChatMessage?>) {
        firebaseService.getTheChattingUsers(0, listOfLastMessages)
        firebaseService.listOfChattingUsersWithTheLastMessageLiveData.observeForever {
            listOfChattingUsersWithTheLastMessageLiveData.value = it
        }
    }

}