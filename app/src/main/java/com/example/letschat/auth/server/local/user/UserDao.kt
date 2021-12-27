package com.example.letschat.auth.server.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.letschat.user.User

@Dao
interface UserDao {

    @Query("SELECT docId FROM USER WHERE uid = :userId")
    suspend fun getUserDocumentId(userId: String): String

    @Insert
    suspend fun addUser(user: User): Long
}