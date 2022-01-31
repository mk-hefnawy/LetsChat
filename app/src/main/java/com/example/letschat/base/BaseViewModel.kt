package com.example.letschat.base

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.letschat.chatroom.chat.ChatMessage
import com.example.letschat.other.Event
import com.example.letschat.user.User
import com.google.firebase.firestore.DocumentChange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BaseViewModel @Inject constructor(
    private val baseRepository: BaseRepository,
    private val localBaseRepository: LocalBaseRepository
) : ViewModel() {

    val uploadImageLiveData = MutableLiveData<Event<Pair<Boolean, Bitmap>>>()
    val userForDrawerLiveData = MutableLiveData<List<User>>()
    val addedMessagesLiveData =  MutableLiveData<ArrayList<ChatMessage>>()

    fun uploadImage(bitmap: Bitmap) {
        baseRepository.uploadImage(bitmap)
        baseRepository.uploadImageLiveData.observeForever { res ->
            res.getContentIfNotHandled()?.let {
                if (it.result) {
                    updateProfilePictureUrlInCache(it.profilePictureUrl, bitmap)
                } else {
                    uploadImageLiveData.value = Event(Pair(false, it.bitmap))
                }
            }
        }
    }

    private fun updateProfilePictureUrlInCache(uploadImageUrl: String, bitmap: Bitmap) {
        viewModelScope.launch {
            localBaseRepository.updateProfilePictureUrlInCache(uploadImageUrl)
            localBaseRepository.userProfilePictureLiveData.observeForever { res ->
                res.getContentIfNotHandled()?.let {
                    if (it > 0) uploadImageLiveData.value = Event(Pair(true, bitmap))
                    else {
                        uploadImageLiveData.value = Event(Pair(false, bitmap))
                    }
                }


            }
        }
    }

    fun getUserForDrawer() {
        viewModelScope.launch {
            localBaseRepository.getUser()
            localBaseRepository.userLiveData.observeForever {
                userForDrawerLiveData.value = it
            }
        }
    }

    fun listenToChatsChanges() {
        baseRepository.listenToChatsChanges()
        baseRepository.addedMessagesLiveData.observeForever {
            addedMessagesLiveData.value = it
        }
    }

}