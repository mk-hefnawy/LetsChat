package com.example.letschat.chatroom.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.letschat.chatroom.chat.ChatRoom

@Dao
interface ChatRoomDao {

    @Query("SELECT chatDocId FROM chatroom WHERE (currentUserId = :currentUserId AND otherUserId = :otherUserId) OR (currentUserId = :otherUserId AND otherUserId = :currentUserId)")
    suspend fun getChatRoomId(currentUserId: String, otherUserId: String): List<String>

    @Insert
    suspend fun cacheJustCreatedDocumentId(chatRoom: ChatRoom): Long

    @Query("DELETE FROM chatroom")
    suspend fun deleteAllChatRooms()
}