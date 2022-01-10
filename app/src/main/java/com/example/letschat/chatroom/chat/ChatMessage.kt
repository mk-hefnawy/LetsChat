package com.example.letschat.chatroom.chat

import java.time.LocalDateTime

class ChatMessage{
    lateinit var senderId: String
    lateinit var receiverId: String
    lateinit var message: String
    lateinit var messageType: String
    lateinit var time: LocalDateTime

}
