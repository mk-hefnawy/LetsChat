package com.example.letschat.chatroom.data.remote

import androidx.lifecycle.MutableLiveData
import com.example.letschat.chatroom.chat.ChatMessage
import com.example.letschat.other.Event
import com.example.letschat.server.remote.FireBaseService
import javax.inject.Inject

class ChatRoomRepository @Inject constructor(
    private val fireBaseService: FireBaseService
) {

    val chatRoomDocumentIdLiveData = MutableLiveData<Event<Pair<String, ChatMessage>>>()
    val chatMessageLiveData = MutableLiveData<Event<Pair<Boolean, ChatMessage>>>()
    val chatMessagesLiveData = MutableLiveData<List<ChatMessage>>()

     fun sendChatMessage(chatMessage: ChatMessage, docId: String) {
        fireBaseService.sendChatMessage(docId, chatMessage)
        fireBaseService.chatMessageLiveData.observeForever {
            chatMessageLiveData.value = it
        }
    }

    fun sendFirstMessage(firstChatMessage: ChatMessage) {
        fireBaseService.sendFirstMessage(firstChatMessage)
        fireBaseService.chatRoomDocumentId.observeForever {
            chatRoomDocumentIdLiveData.value = it
        }
    }

    fun getAllPreviousMessages(docId: String) {
        fireBaseService.getAllPreviousMessages(docId)
        fireBaseService.chatMessagesLiveData.observeForever {
            chatMessagesLiveData.value = it
        }
    }
}