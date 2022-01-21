package com.example.letschat.server.remote

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.letschat.auth.models.LoginResultModel
import com.example.letschat.auth.models.SignUpResultModel
import com.example.letschat.auth.ui.LoginFragment
import com.example.letschat.chatroom.chat.ChatMessage
import com.example.letschat.other.*
import com.example.letschat.user.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FireBaseService @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore
) {

    val signUpResult: MutableLiveData<Event<SignUpResultModel>> = MutableLiveData()
    val loginLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()

    val allReceivedFriendRequestsLiveData = MutableLiveData<List<User>>()
    val friendsLiveData = MutableLiveData<Event<List<User>>>()

    val isUserNameTakenLiveData = MutableLiveData<Event<Boolean>>()
    val userInfoLiveData = MutableLiveData<User>()
    val isUserAddFriendRequestSuccessful = MutableLiveData<Event<Boolean>>()
    val potentialFriendLiveData = MutableLiveData<User>()

    val friendRequestReactionLiveData = MutableLiveData<Event<obj>>()
    private val makingFriendsLiveData = MutableLiveData<Event<Boolean>>()

    val chatRoomDocumentId = MutableLiveData<Event<Pair<String, ChatMessage>>>()
    val chatMessageLiveData = MutableLiveData<Event<Pair<Boolean, ChatMessage>>>()
    val chatMessagesLiveData = MutableLiveData<List<ChatMessage>>()

    private var listOfLastMessagesForEachChat = ArrayList<ChatMessage?>()
    val listOfLastMessagesForEachChatLiveData = MutableLiveData<List<ChatMessage?>>()

    private var listOfChattingUsersWithTheLastMessage = ArrayList<Pair<ChatMessage, User>>()
    val listOfChattingUsersWithTheLastMessageLiveData =
        MutableLiveData<List<Pair<ChatMessage, User>>>()

    val uploadImageLiveData = MutableLiveData<Event<UploadImageObject>>()

    object obj {
        var result = true
        lateinit var message: String
        var uid = ""
    }

    object UploadImageObject{
        var result = true
        lateinit var bitmap: Bitmap
        lateinit var profilePictureUrl: String
    }



    fun signUp(userName: String, email: String, password: String) {
        Log.d("Here", "Email: $email Password: $password")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Here", "Task is Successful")
                    val userId = auth.currentUser?.uid.toString()
                    val user = User(uid = userId, userName = userName, email = email)
                    addUserToFirestore(user)

                } else {
                    Log.d("Here", "Task Failed")
                    sendUserCreationError()
                    print(task.exception)
                }
            }
    }

    fun loginWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(LoginFragment.TAG, "Sign Successful")
                    loginLiveData.value = Event(true)

                } else {
                    Log.d(LoginFragment.TAG, "Sign Failed")
                    loginLiveData.value = Event(false)
                }
            }
            .addOnFailureListener {
                Log.d(LoginFragment.TAG, "Sign in Task Failed")
                println(it.stackTrace)
            }

    }

    fun loginWithGoogle(context: Context) {

    }

    private fun addUserToFirestore(user: User) {
        val theUser = hashMapOf(
            UID to auth.currentUser?.uid,
            USER_NAME to user.userName,
            EMAIL to user.email,
            PROFILE_PICTURE_URL to "",
            RECEIVED_FRIEND_REQUESTS to mutableListOf<String>(),
            SENT_FRIEND_REQUESTS to mutableListOf<String>(),
            FRIENDS to mutableListOf<String>()
        )
        db.collection("users")
            .document(user.uid)
            .set(theUser)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    notifyUiThatUserWasCreated(user)
                } else {
                    sendUserCreationError()
                }
            }

            .addOnFailureListener {
                Log.v("TAG", "Error adding document", it)
                sendFireStoreRegisterError()
            }
    }

    fun isUserNameAlreadyTaken(userName: String) {
        db.collection("users")
            .whereEqualTo(USER_NAME, userName)
            .get()
            .addOnSuccessListener { documents ->
                Log.d("Here", "Firebase User name is taken or not")
                isUserNameTakenLiveData.value = Event(documents.size() > 0)
            }
            .addOnFailureListener {
                Log.d("Here", "Failed")
                Log.d("Here", it.stackTrace.toString())
            }
    }


    fun isUserAlreadyLoggedIn(): Pair<Boolean, String> {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uId = currentUser.uid
            return Pair(
                true,
                uId
            ) // then the activity/viewModel will use that uid to grab the data of that user to update the Ui based on that data
        } else {
            return Pair(false, "")
        }
    }

    private fun sendSignInResult(signInResult: LoginResultModel) {
        //signInResultLiveData.value = Event(signInResult)
        // signInResultLiveData.value = signInResult
    }

    fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Email is sent to $email")
                } else {
                    println("Email is sent to $email")
                }
            }
    }

    fun addUser(searchedUser: User) {
        db.collection("users")
            .document(searchedUser.uid)
            .update(
                "receivedFriendRequests",
                FieldValue.arrayUnion(auth.currentUser?.uid)
            )
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    changeCurrentUser_SentFriendRequests(searchedUser)
                } else {
                    isUserAddFriendRequestSuccessful.value = Event(false)
                }
            }
    }

    private fun changeCurrentUser_SentFriendRequests(searchedUser: User) {
        db.collection("users")
            .document(auth.currentUser?.uid!!)
            .update(
                "sentFriendRequests",
                FieldValue.arrayUnion(searchedUser.uid)
            )
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    isUserAddFriendRequestSuccessful.value = Event(true)
                } else {
                    isUserAddFriendRequestSuccessful.value = Event(false)
                }
            }
    }

    fun getUserInfo(docId: String) {
        db.collection("users")
            .document(docId)
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                Log.d("HereFireBase", "UserName: ${user?.userName}")
                userInfoLiveData.value = user!!
            }
            .addOnFailureListener {
                Log.d("Here", it.stackTrace.toString())
            }
    }

    fun searchUser(userNameOfUser: String) {
        db.collection("users")
            .whereEqualTo("userName", userNameOfUser)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result.documents.size > 0) {
                        val userDoc = it.result.documents[0].reference
                        userDoc.get().addOnCompleteListener {
                            if (it.isSuccessful) {
                                val user = it.result.toObject(User::class.java)
                                potentialFriendLiveData.value = user!!
                            }
                        }
                    } else {
                        Log.d("Here", "No User name found")
                    }
                } else {
                    Log.d("Here", "Search user task unsuccessful")
                }
            }
    }

    fun getAllFriendRequests() {
        db.collection("users")
            .document(auth.currentUser?.uid!!)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result
                    val userIdsOfReceivedFriendRequests: List<String> =
                        document.get(RECEIVED_FRIEND_REQUESTS) as List<String>
                    if (userIdsOfReceivedFriendRequests.isNotEmpty()) {
                        getUsersThatSentFriendRequest(userIdsOfReceivedFriendRequests)
                    } else {
                        allReceivedFriendRequestsLiveData.value = listOf()
                    }
                } else {
                    Log.d("Here", "Cannot load friend requests")
                }
            }
    }

    private fun getUsersThatSentFriendRequest(userIdsOfReceivedFriendRequests: List<String>?) {
        db.collection("users")
            .whereIn("uid", userIdsOfReceivedFriendRequests!!)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val listOfUsersDocumentSnapshots = task.result.documents
                    extractUsersFromDocumentsSnapshots(
                        listOfUsersDocumentSnapshots,
                        allReceivedFriendRequestsLiveData
                    )
                } else {
                    Log.d("Here", "Getting users that sent a friend request Failed")
                }
            }
    }

    private fun extractFriendsFromDocumentsSnapshots(
        listOfUsersDocumentSnapshots: List<DocumentSnapshot>,
        liveData: MutableLiveData<Event<List<User>>>
    ) {
        val users = ArrayList<User>()
        listOfUsersDocumentSnapshots.forEach { docSnapshot ->
            docSnapshot.toObject((User::class.java))?.let {
                users.add(it)
            }
        }

        liveData.value = Event(users)
    }

    private fun extractUsersFromDocumentsSnapshots(
        listOfUsersDocumentSnapshots: List<DocumentSnapshot>,
        liveData: MutableLiveData<List<User>>
    ) {
        val users = ArrayList<User>()
        listOfUsersDocumentSnapshots.forEach { docSnapshot ->
            docSnapshot.toObject((User::class.java))?.let {
                users.add(it)
            }
        }

        liveData.value = users
    }

    private fun getFriendsFromTheirIds(friendsIds: List<String>) {
        db.collection("users")
            .whereIn("uid", friendsIds)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val listOfFriendsDocumentSnapshots = task.result.documents
                    extractFriendsFromDocumentsSnapshots(
                        listOfFriendsDocumentSnapshots,
                        friendsLiveData
                    )
                } else {
                    Log.d("Here", "Getting users that sent a friend request Failed")
                }
            }

    }

    private fun notifyUiThatUserWasCreated(user: User) {
        signUpResult.value = Event(SignUpResultModel(true, "", user))
    }

    private fun sendUserCreationError() {
        signUpResult.value = Event(SignUpResultModel(false, "CreationError", null))
    }

    private fun sendFireStoreRegisterError() {
        signUpResult.value = Event(SignUpResultModel(false, "FireStoreRegisterError", null))
    }

    fun reactToFriendRequest(user: User, eventType: String) {
        val usersCollection = db.collection("users")
        if (eventType == "accept") {
            makeTheTwoUsersFriends(usersCollection, user.uid)
            observeMakingFriendsResult(usersCollection, user.uid, eventType)
        } else {
            removeFriendRequestAfterMakingFriendsFromCurrentUser(
                usersCollection,
                user.uid,
                eventType
            )
        }
    }

    private fun removeFriendRequestAfterMakingFriendsFromCurrentUser(
        usersCollection: CollectionReference,
        uid: String,
        eventType: String
    ) {
        usersCollection.document(auth.currentUser?.uid!!)
            .update(RECEIVED_FRIEND_REQUESTS, FieldValue.arrayRemove(uid))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    removeFriendRequestAfterMakingFriendsFromTheOtherUser(
                        usersCollection,
                        uid,
                        eventType
                    )
                } else {
                    Log.d("Here", "Error Removing Request")
                }
            }
    }

    private fun removeFriendRequestAfterMakingFriendsFromTheOtherUser(
        usersCollection: CollectionReference,
        uid: String,
        eventType: String
    ) {
        usersCollection.document(uid)
            .update(SENT_FRIEND_REQUESTS, FieldValue.arrayRemove(auth.currentUser?.uid!!))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    obj.message = "Friend Request Accepted Successfully"
                    obj.uid = uid
                    if (eventType == "accept") {
                        friendRequestReactionLiveData.value =
                            Event(obj)
                    } else {
                        obj.message = "Friend Request Declined Successfully"
                        friendRequestReactionLiveData.value =
                            Event(obj)
                    }

                } else {
                    Log.d("Here", "Error Removing Request")
                }
            }
    }

    private fun makeTheTwoUsersFriends(usersCollection: CollectionReference, uid: String) {
        addCurrentUserToTheOtherUser(usersCollection, uid)
    }

    private fun addCurrentUserToTheOtherUser(usersCollection: CollectionReference, uid: String) {
        usersCollection.document(uid)
            .update(FRIENDS, FieldValue.arrayUnion(auth.currentUser?.uid))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    addTheOtherUserToTheCurrentUser(usersCollection, uid)
                }
            }
    }

    private fun addTheOtherUserToTheCurrentUser(usersCollection: CollectionReference, uid: String) {
        usersCollection.document(auth.currentUser?.uid!!)
            .update(FRIENDS, FieldValue.arrayUnion(uid))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    makingFriendsLiveData.value = Event(true)

                } else {
                    Log.d("Here", "Friend Request Failed")
                }
            }
    }

    private fun observeMakingFriendsResult(
        usersCollection: CollectionReference,
        uid: String,
        eventType: String
    ) {
        makingFriendsLiveData.observeForever { result ->
            result.getContentIfNotHandled()?.let {
                if (it) removeFriendRequestAfterMakingFriendsFromCurrentUser(
                    usersCollection,
                    uid,
                    eventType
                )
            }
        }
    }

    fun getAllFriends() {
        db.collection("users")
            .document(auth.currentUser?.uid!!)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    val friendsIds: List<String>? =
                        document.get(FRIENDS) as? List<String>
                    getFriendsFromTheirIds(friendsIds!!)
                }
            }
    }

    fun sendFirstMessage(firstChatMessage: ChatMessage) {
        firstChatMessage.senderId = auth.currentUser?.uid!!
        val hash = hashMapOf(
            "messages" to arrayListOf(firstChatMessage)
        )
        db.collection("chats")
            .add(hash)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val chatDocId = task.result.id
                    Log.d("Here", "ChatDocId: ${chatDocId}")
                    chatRoomDocumentId.value = Event(Pair(chatDocId, firstChatMessage))
                } else {
                    Log.d("Here", "Error getting the chat document")
                }
            }
    }

    fun sendChatMessage(chatDocId: String, chatMessage: ChatMessage) {
        chatMessage.senderId = auth.currentUser?.uid!!
        db.collection("chats")
            .document(chatDocId)
            .update("messages", FieldValue.arrayUnion(chatMessage))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // live data stuff
                    Log.d("Here", "Message Added")
                    chatMessageLiveData.value = Event(Pair(true, chatMessage))
                } else {
                    chatMessageLiveData.value = Event(Pair(false, chatMessage))
                }

            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun getAllPreviousMessages(docId: String) {
        db.collection("chats")
            .document(docId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val listOFChatMessages = ArrayList<ChatMessage>()
                    val messages = task.result.data as HashMap<String, Any>?
                    messages?.let {
                        val list = it["messages"] as ArrayList<HashMap<String, Any>>
                        list.forEach {
                            listOFChatMessages.add(
                                ChatMessage(
                                    it["senderId"].toString(),
                                    it["receiverId"].toString(),
                                    it["message"].toString(),
                                    "text",
                                    null
                                )
                            )
                        }
                    }
                    chatMessagesLiveData.value = listOFChatMessages
                }

            }
    }

    fun getLastMessageOfEachChat(index: Int, listOfChatDocsIds: List<String>?) {
        if (index == listOfChatDocsIds?.size) {
            listOfLastMessagesForEachChat.let {
                listOfLastMessagesForEachChatLiveData.value = it
                listOfLastMessagesForEachChat = arrayListOf()
            }
            return
        }

        var lastMessage: ChatMessage? = null
        db.collection("chats")
            .document(listOfChatDocsIds?.get(index)!!)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val messages = task.result.data as HashMap<String, Any>?
                    messages?.let {
                        val list = it["messages"] as ArrayList<HashMap<String, Any>>
                        val lastChatMessageAsHash = list.last()
                        lastMessage = ChatMessage(
                            lastChatMessageAsHash["senderId"].toString(),
                            lastChatMessageAsHash["receiverId"].toString(),
                            lastChatMessageAsHash["message"].toString(),
                            "text",
                            null
                        )
                    }

                    listOfLastMessagesForEachChat.add(lastMessage)
                    getLastMessageOfEachChat(index + 1, listOfChatDocsIds)
                }
            }

    }

    fun getTheChattingUsers(index: Int, listOfLastMessages: List<ChatMessage?>) {
        if (index == listOfLastMessages.size) {
            listOfChattingUsersWithTheLastMessageLiveData.value = listOfChattingUsersWithTheLastMessage
            listOfChattingUsersWithTheLastMessage = arrayListOf() // making it empty
            return
        }
        val chattingUserId =
            if (listOfLastMessages.get(index)?.senderId == auth.currentUser?.uid) listOfLastMessages.get(
                index
            )?.receiverId
            else listOfLastMessages.get(index)?.senderId

        db.collection("users")
            .document(chattingUserId!!)
            .get()
            .addOnSuccessListener { docSnapShot ->

                val chattingUser = docSnapShot.toObject(User::class.java)
                listOfChattingUsersWithTheLastMessage.add(Pair(listOfLastMessages[index]!!, chattingUser!!))
                getTheChattingUsers(index + 1, listOfLastMessages)
            }
            .addOnFailureListener {
                Log.d("Here", "Error Getting the Chatting Users")
            }
    }

    fun uploadImage(bitmap: Bitmap) {
        val storageRef = FirebaseStorage.getInstance().reference
        val currentUserProfilePictureReference = storageRef.child("usersImages/" + UUID.randomUUID().toString())

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val pictureByteArray = baos.toByteArray()

        val uploadTask = currentUserProfilePictureReference.putBytes(pictureByteArray)

        uploadTask.addOnSuccessListener {
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful){
                   task.exception?.let {
                       throw it
                   }
                }
                currentUserProfilePictureReference.downloadUrl
            }
                .addOnCompleteListener { task->
                    if (task.isSuccessful){
                        val downloadUri = task.result
                        setUserProfilePicture(downloadUri, bitmap)
                    }else{
                        Log.d("Here", "Error Getting the Picture Url")
                    }
                }
        }
        uploadTask.addOnFailureListener{
            UploadImageObject.bitmap = bitmap
            UploadImageObject.result = false
            UploadImageObject.profilePictureUrl = ""
            uploadImageLiveData.value = Event(UploadImageObject)
        }
    }

    private fun setUserProfilePicture(downloadUri: Uri?, bitmap: Bitmap) {
        downloadUri?.let {
            db.collection("users")
                .document(auth.currentUser?.uid!!)
                .update(PROFILE_PICTURE_URL, downloadUri.toString()) // notice toString(), because uri exceeds the max length
                .addOnSuccessListener {
                    UploadImageObject.bitmap = bitmap
                    UploadImageObject.profilePictureUrl = downloadUri.toString()
                    uploadImageLiveData.value = Event(UploadImageObject)
                }
                .addOnFailureListener {
                    Log.d("Here", "Error Setting Picture Url to Current User")
                }
        }
    }
}