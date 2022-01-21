package com.example.letschat.server.local

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.letschat.chatroom.chat.ChatRoom
import com.example.letschat.other.Event
import com.example.letschat.user.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RoomService @Inject constructor(@ApplicationContext val context: Context) {

    val insertStatusLiveData = MutableLiveData<Event<Long>>()
    val chatRoomDocIdLiveData = MutableLiveData<Event<List<String>>>()
    val justCachedRoomChatDocumentResult = MutableLiveData<Event<Long>>()

    val allChatsLiveData = MutableLiveData<Event<List<String>>>()
    val userLiveData = MutableLiveData<List<User>>()

    val userProfilePictureLiveData = MutableLiveData<Event<Int>>()

    suspend fun addUserToRoom(user: User) {
        val isInsertSuccessful = AppRoomDatabase(context).authDao().addUser(user)
        insertStatusLiveData.value = Event(isInsertSuccessful)
    }

    suspend fun getChatDocId(currentUserId: String, otherUserId: String) {
        val docId = AppRoomDatabase(context).chatRoomDao().getChatRoomId(currentUserId, otherUserId)
        chatRoomDocIdLiveData.value = Event(docId)
    }

    suspend fun cacheJustCreatedDocumentId(chatRoom: ChatRoom) {
        val cachingDocIdResult =
            AppRoomDatabase(context).chatRoomDao().cacheJustCreatedDocumentId(chatRoom)
        justCachedRoomChatDocumentResult.value = Event(cachingDocIdResult)
    }

    suspend fun deleteAllChatRooms(){
        AppRoomDatabase(context).chatRoomDao().deleteAllChatRooms()
    }

    suspend fun getAllChatsFromCache(currentUserId: String) {
        val allChats = AppRoomDatabase(context).chatsDao().getAllChatsFromCache(currentUserId)
        allChatsLiveData.value = Event(allChats)
    }

    suspend fun getUser(uid: String) {
        val user = AppRoomDatabase(context).authDao().getUser(uid)
        userLiveData.value = user
    }

    suspend fun deleteAllUsers(){
        AppRoomDatabase(context).authDao().deleteAllUsers()
    }

    suspend fun updateProfilePictureUrlInCache(uploadImageUrl: String, currentUserId: String) {
        val result = AppRoomDatabase(context).authDao().updateProfilePictureUrlInCache(uploadImageUrl, currentUserId)
        userProfilePictureLiveData.value = Event(result)
    }
}