package com.example.letschat.chatroom.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.letschat.chatroom.data.local.LocalChatRoomRepository
import com.example.letschat.chatroom.data.remote.ChatRoomRepository
import com.example.letschat.other.DOC_DOES_NOT_EXIST
import com.example.letschat.other.Event
import com.example.letschat.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val chatRoomRepository: ChatRoomRepository,
    private val localChatRoomRepository: LocalChatRoomRepository
) : ViewModel() {
    var message = ""

    val chatRoomDocIdLiveData = MutableLiveData<Event<List<String>>>()
    val justCreatedChatRoomDocumentIdLiveData = MutableLiveData<Event<Pair<String, ChatMessage>>>()
    val justCachedRoomChatDocumentResult = MutableLiveData<Event<Long>>()

    val chatMessageLiveData = MutableLiveData<Event<Pair<Boolean, ChatMessage>>>()
    val chatMessagesLiveData = MutableLiveData<List<ChatMessage>>()

    fun sendChatMessage(chatMessage: ChatMessage, docId: String) {
        chatRoomRepository.sendChatMessage(chatMessage, docId)
        chatRoomRepository.chatMessageLiveData.observeForever {
            chatMessageLiveData.value = it
        }
    }

    fun getChatRoomDocument(chattingUser: User) {
        viewModelScope.launch(Dispatchers.Main) {
            localChatRoomRepository.getChatRoomDocId(chattingUser)
            localChatRoomRepository.chatRoomDocIdLiveData.observeForever {
                chatRoomDocIdLiveData.value = it
            }
        }
    }

    fun sendFirstMessage(firstChatMessage: ChatMessage) {
        chatRoomRepository.sendFirstMessage(firstChatMessage)
        chatRoomRepository.chatRoomDocumentIdLiveData.observeForever {
            justCreatedChatRoomDocumentIdLiveData.value = it
        }
    }

    fun cacheJustCreatedDocumentId(chattingUserId: String, docId: String) {
        viewModelScope.launch(Dispatchers.Main) {
            localChatRoomRepository.cacheJustCreatedDocumentId(chattingUserId, docId)
            localChatRoomRepository.justCachedRoomChatDocumentResult.observeForever {
                justCachedRoomChatDocumentResult.value = it
            }
        }
    }

    fun getAllPreviousMessages(docId: String) {
        chatRoomRepository.getAllPreviousMessages(docId)
        chatRoomRepository.chatMessagesLiveData.observeForever {
            chatMessagesLiveData.value = it
        }
    }

    fun deleteAllChatRooms(){
        viewModelScope.launch {
            localChatRoomRepository.deleteAllChatRooms()
        }
    }
}