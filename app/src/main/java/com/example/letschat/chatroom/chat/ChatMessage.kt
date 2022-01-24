package com.example.letschat.chatroom.chat


import java.util.Date

data class ChatMessage(
    var senderId: String,
    val receiverId: String,
    val message: String,
    val messageType: String,
    val time: Date?)
