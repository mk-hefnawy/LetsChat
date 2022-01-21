package com.example.letschat.base

import androidx.lifecycle.MutableLiveData
import com.example.letschat.other.Event
import com.example.letschat.server.local.RoomService
import com.example.letschat.user.User
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class LocalBaseRepository @Inject constructor(
    val roomService: RoomService,
    val auth: FirebaseAuth
) {
    val userProfilePictureLiveData = MutableLiveData<Event<Int>>()
    val userLiveData = MutableLiveData<List<User>>()

    suspend fun updateProfilePictureUrlInCache(uploadImageUrl: String) {
        roomService.updateProfilePictureUrlInCache(uploadImageUrl, auth.currentUser?.uid!!)
        roomService.userProfilePictureLiveData.observeForever {
            userProfilePictureLiveData.value = it
        }
    }

    suspend fun getUser() {
        roomService.getUser(auth.currentUser?.uid!!)
        roomService.userLiveData.observeForever {
            userLiveData.value = it
        }
    }
}