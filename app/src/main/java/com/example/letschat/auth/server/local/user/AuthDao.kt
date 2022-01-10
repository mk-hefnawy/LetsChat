package com.example.letschat.auth.server.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.letschat.user.User

@Dao
interface AuthDao {

    @Insert
    suspend fun addUser(user: User): Long
}