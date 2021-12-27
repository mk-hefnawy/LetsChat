package com.example.letschat.user

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class User(
    var uid: String = "",
    var docId: String = "",
    var userName: String = "",
    var email: String = "",

    @Ignore
    var password: String = ""
   ): Serializable{

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

}
