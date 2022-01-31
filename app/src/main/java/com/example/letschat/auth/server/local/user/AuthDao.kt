package com.example.letschat.auth.server.local.user

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.letschat.user.User

@Dao
interface AuthDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: User): Long

    @Query("SELECT * FROM user WHERE uid = :uid")
    suspend fun getUser(uid: String): List<User>

    @Query("DELETE FROM user")
    suspend fun deleteAllUsers()

    @Query("UPDATE user SET profilePictureUrl = :uploadImageUrl WHERE uid = :currentUserId")
    suspend fun updateProfilePictureUrlInCache(uploadImageUrl: String, currentUserId: String): Int // returns the number of rows updated successfully

}