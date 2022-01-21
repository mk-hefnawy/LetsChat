package com.example.letschat.user

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class User(
    var uid: String = "",
    var userName: String = "",
    var email: String = "",
    var profilePictureUrl: String = "",
   ): Serializable{

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var friendsOrNot = false
}
