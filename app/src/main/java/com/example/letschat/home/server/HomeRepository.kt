package com.example.letschat.home.server

import com.example.letschat.server.FireBaseService

class HomeRepository(private val fireBaseService: FireBaseService) {

    fun addFriend(userName: String){
        fireBaseService.addFriend(userName)
    }
}