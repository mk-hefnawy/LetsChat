package com.example.letschat.chatroom.chat

import com.example.letschat.server.remote.FireBaseService
import javax.inject.Inject

class ChatRoomRepository @Inject constructor(
    private val fireBaseService: FireBaseService
) {
    fun sendChatMessage(chatMessage: ChatMessage){
        fireBaseService.sendChatMessage(chatMessage)
    }
}