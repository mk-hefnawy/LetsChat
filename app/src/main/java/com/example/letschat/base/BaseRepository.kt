package com.example.letschat.base

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.example.letschat.other.Event
import com.example.letschat.server.remote.FireBaseService
import javax.inject.Inject

class BaseRepository @Inject constructor(val fireBaseService: FireBaseService){

    val uploadImageLiveData = MutableLiveData<Event<FireBaseService.UploadImageObject>>()

    fun uploadImage(bitmap: Bitmap) {
        fireBaseService.uploadImage(bitmap)
        fireBaseService.uploadImageLiveData.observeForever {
            uploadImageLiveData.value = it
        }
    }
}