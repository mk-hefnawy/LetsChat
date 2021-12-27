package com.example.letschat.auth.server.local.user

import androidx.lifecycle.MutableLiveData
import com.example.letschat.auth.server.local.RoomService
import com.example.letschat.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalUserRepository(private val roomService: RoomService) {

    val insertStatusLiveData = MutableLiveData<Long>()

    suspend fun addUserToRoom(user: User){
        withContext(Dispatchers.Main) {
        roomService.addUserToRoom(user)
        roomService.insertStatusLiveData.observeForever{
            insertStatusLiveData.postValue(it)
        }
    }}
}