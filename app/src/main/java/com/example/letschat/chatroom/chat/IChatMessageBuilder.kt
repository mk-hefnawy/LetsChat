package com.example.letschat.chatroom.chat

import java.time.LocalDateTime

interface IChatMessageBuilder {

    fun senderId(senderId: String)
    fun receiverId(receiverId: String)
    fun message(receiverId: String)
    fun messageType(messageType: String)
    fun time(time: LocalDateTime)
    fun reset()
    fun getChatMessage(): ChatMessage
}