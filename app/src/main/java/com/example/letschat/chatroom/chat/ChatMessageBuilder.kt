package com.example.letschat.chatroom.chat

import java.time.LocalDateTime

class ChatMessageBuilder : IChatMessageBuilder {
    private lateinit var chatMessage: ChatMessage

    init {
        reset()
    }

    override fun reset(){
        chatMessage = ChatMessage()
    }

    override fun senderId(senderId: String) {
        chatMessage.senderId = senderId
    }

    override fun receiverId(receiverId: String) {
        chatMessage.receiverId = receiverId
    }

    override fun message(receiverId: String) {
        chatMessage.receiverId = receiverId
    }

    override fun messageType(messageType: String) {
        chatMessage.messageType = messageType
    }

    override fun time(time: LocalDateTime) {
        chatMessage.time = time
    }

    override fun getChatMessage(): ChatMessage{
        return chatMessage
    }
}