package com.example.letschat.user

data class User(var userName: String, var email: String, var password: String){
    val id: String? = null
    var birthData: String? = null

    // any non-necessary data
}
