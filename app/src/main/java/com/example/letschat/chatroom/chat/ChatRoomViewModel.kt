package com.example.letschat.chatroom.chat

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val chatRoomRepository: ChatRoomRepository
): ViewModel() {
    var message = ""

    fun sendChatMessage(chatMessage: ChatMessage){
        chatRoomRepository.sendChatMessage(chatMessage)
    }
}