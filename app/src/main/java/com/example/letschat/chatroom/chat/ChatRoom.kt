package com.example.letschat.chatroom.chat

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatRoom(
    val currentUserId: String,
    val otherUserId: String,
    val chatDocId: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}