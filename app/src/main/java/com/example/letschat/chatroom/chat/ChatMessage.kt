package com.example.letschat.chatroom.chat

import java.time.LocalDateTime

data class ChatMessage(
    var senderId: String,
    val receiverId: String,
    val message: String,
    val messageType: String,
    val time: LocalDateTime?)
