package com.example.letschat.home.ui

import com.example.letschat.user.User

interface IGenericFriends {
        fun onClick(user: User, eventType: String)
        fun onFriendClicked(user: User)
}