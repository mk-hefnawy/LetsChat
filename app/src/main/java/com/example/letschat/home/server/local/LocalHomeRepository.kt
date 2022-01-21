package com.example.letschat.home.server.local

import androidx.lifecycle.MutableLiveData
import com.example.letschat.other.Event
import com.example.letschat.server.local.RoomService
import com.example.letschat.user.User
import com.google.firebase.auth.FirebaseAuth


class LocalHomeRepository(val roomService: RoomService, val auth:FirebaseAuth){






    suspend fun deleteAllUsers(){
        roomService.deleteAllUsers()
    }


}