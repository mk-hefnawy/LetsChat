package com.example.letschat.server.remote

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.letschat.auth.models.LoginResultModel
import com.example.letschat.auth.models.SignUpResultModel
import com.example.letschat.auth.ui.LoginFragment
import com.example.letschat.chatroom.chat.ChatMessage
import com.example.letschat.other.*
import com.example.letschat.user.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import javax.inject.Inject

class FireBaseService @Inject constructor(
    var auth: FirebaseAuth,
    val db: FirebaseFirestore
) {

    val signUpResult: MutableLiveData<Event<SignUpResultModel>> = MutableLiveData()
    val loginLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()

    val allReceivedFriendRequestsLiveData = MutableLiveData<List<User>>()
    val friendsLiveData = MutableLiveData<List<User>>()

    val isUserNameTakenLiveData = MutableLiveData<Boolean>()
    val userInfoLiveData = MutableLiveData<User>()
    val isUserAddFriendRequestSuccessful = MutableLiveData<Event<Boolean>>()
    val potentialFriendLiveData = MutableLiveData<User>()

    val friendRequestReactionLiveData = MutableLiveData<Event<Pair<Boolean, String>>>()
    private val makingFriendsLiveData = MutableLiveData<Event<Boolean>>()

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
                isUserNameTakenLiveData.value = documents.size() > 0
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
                    val userIdsOfReceivedFriendRequests: List<String>? =
                        document.get(RECEIVED_FRIEND_REQUESTS) as? List<String>
                    getUsersThatSentFriendRequest(userIdsOfReceivedFriendRequests)
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
                    extractUsersFromDocumentsSnapshots(listOfUsersDocumentSnapshots, allReceivedFriendRequestsLiveData)
                } else {
                    Log.d("Here", "Getting users that sent a friend request Failed")
                }
            }
    }

    private fun extractUsersFromDocumentsSnapshots(listOfUsersDocumentSnapshots: List<DocumentSnapshot>, liveData: MutableLiveData<List<User>>) {
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
                    extractUsersFromDocumentsSnapshots(listOfFriendsDocumentSnapshots, friendsLiveData)
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
                    if (eventType == "accept") {
                        friendRequestReactionLiveData.value =
                            Event(Pair(true, "Friend Request Accepted Successfully"))
                    } else {
                        friendRequestReactionLiveData.value =
                            Event(Pair(true, "Friend Request Declined Successfully"))
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
                if (task.isSuccessful){
                    val document = task.result
                    val friendsIds: List<String>? =
                        document.get(FRIENDS) as? List<String>
                    getFriendsFromTheirIds(friendsIds!!)
                }
            }
    }

    fun sendChatMessage(chatMessage: ChatMessage) {

    }

}