package com.example.letschat.auth.server.local

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.letschat.user.User

class RoomService(private val context: Context) {

    val insertStatusLiveData = MutableLiveData<Long>()

    suspend fun addUserToRoom(user: User){
        val isInsertSuccessful = AppRoomDatabase(context).userDao().addUser(user)
        insertStatusLiveData.postValue(isInsertSuccessful)
    }
}