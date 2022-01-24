package com.example.letschat.user

import androidx.room.Entity
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
    var friendShipStatus: FriendShipStatus = FriendShipStatus.NONE
}

enum class FriendShipStatus{
    NONE,
    FRIENDS,
    YOU_SENT_A_FRIEND_REQUEST,
    THEY_SENT_A_FRIEND_REQUEST
}
