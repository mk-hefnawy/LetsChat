package com.example.letschat.chatroom.data.local

import androidx.room.*
import com.example.letschat.chatroom.chat.ChatRoom

@Dao
interface ChatRoomDao {

    @Query("SELECT chatDocId FROM chatroom WHERE (currentUserId = :currentUserId AND otherUserId = :otherUserId) OR (currentUserId = :otherUserId AND otherUserId = :currentUserId)")
    suspend fun getChatRoomId(currentUserId: String, otherUserId: String): List<String>

    @Insert
    suspend fun cacheJustCreatedDocumentId(chatRoom: ChatRoom): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun cacheUserChats(userChats: List<ChatRoom>): List<Long>

    @Query("DELETE FROM chatroom")
    suspend fun deleteAllChatRooms()
}