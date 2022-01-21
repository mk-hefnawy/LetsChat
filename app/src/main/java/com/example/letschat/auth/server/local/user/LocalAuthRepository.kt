package com.example.letschat.auth.server.local.user

import androidx.lifecycle.MutableLiveData
import com.example.letschat.other.Event
import com.example.letschat.server.local.RoomService
import com.example.letschat.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalAuthRepository(private val roomService: RoomService) {

    val insertStatusLiveData = MutableLiveData<Event<Long>>()

    suspend fun addUserToRoom(user: User){
        withContext(Dispatchers.Main) {
        roomService.addUserToRoom(user)
        roomService.insertStatusLiveData.observeForever{
            insertStatusLiveData.postValue(it)
        }
    }}

    suspend fun getUser(uid: String){
        roomService.getUser(uid)
    }
}