package com.example.letschat.home.server.local

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ChatsDao {

    @Query("SELECT chatDocId FROM CHATROOM WHERE (currentUserId = :currentUserId) OR (otherUserId = :currentUserId)")
    suspend fun getAllChatsFromCache(currentUserId: String): List<String>
}