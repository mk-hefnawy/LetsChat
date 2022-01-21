package com.example.letschat.chatroom.data.local

import androidx.lifecycle.MutableLiveData
import com.example.letschat.chatroom.chat.ChatRoom
import com.example.letschat.other.Event
import com.example.letschat.server.local.RoomService
import com.example.letschat.user.User
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class LocalChatRoomRepository @Inject constructor(
    val roomService: RoomService,
    val auth: FirebaseAuth
) {

    val chatRoomDocIdLiveData = MutableLiveData<Event<List<String>>>()
    val justCachedRoomChatDocumentResult = MutableLiveData<Event<Long>>()

    suspend fun getChatRoomDocId(chattingUser: User){
        roomService.getChatDocId(auth.currentUser?.uid!!, chattingUser.uid)
        roomService.chatRoomDocIdLiveData.observeForever {
            chatRoomDocIdLiveData.value = it
        }
    }

    suspend fun cacheJustCreatedDocumentId(chattingUserId: String, docId: String) {
        val chatRoom = ChatRoom(auth.currentUser?.uid!!, chattingUserId, docId)
        roomService.cacheJustCreatedDocumentId(chatRoom)
        roomService.justCachedRoomChatDocumentResult.observeForever {
            justCachedRoomChatDocumentResult.value = it
        }
    }

    suspend fun deleteAllChatRooms() {
        roomService.deleteAllChatRooms()
        roomService.justCachedRoomChatDocumentResult.observeForever {
            justCachedRoomChatDocumentResult.value = it
        }
    }
}