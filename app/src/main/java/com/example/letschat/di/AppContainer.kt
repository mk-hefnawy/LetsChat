package com.example.letschat.di

import android.content.Context
import com.example.letschat.auth.helper_classes.*
import com.example.letschat.server.local.RoomService
import com.example.letschat.auth.server.local.user.LocalAuthRepository
import com.example.letschat.user.User
import com.example.letschat.auth.server.remote.LoginRepository
import com.example.letschat.auth.server.remote.SignUpRepository
import com.example.letschat.auth.view_models.LoginViewModel
import com.example.letschat.auth.view_models.SignUpViewModel
import com.example.letschat.home.server.local.LocalHomeRepository
import com.example.letschat.home.server.remote.HomeRepository
import com.example.letschat.home.server.remote.AddFriendRepository
import com.example.letschat.home.server.remote.FriendRequestsRepository
import com.example.letschat.home.view_models.AddFriendViewModel
import com.example.letschat.home.view_models.FriendRequestsViewModel
import com.example.letschat.home.view_models.HomeViewModel
import com.example.letschat.server.remote.FireBaseService
import com.example.letschat.user.UserManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AppContainer(context: Context) {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    private val fireBaseService = FireBaseService(auth, db)
    private val roomService: RoomService = RoomService(context)
    var validator = AuthValidator()

    var loginRepo = LoginRepository(fireBaseService)
    private var signUpRepo = SignUpRepository(fireBaseService)
    private val addFriendRepository = AddFriendRepository(fireBaseService)
    private var localUserRepository = LocalAuthRepository(roomService)
    private val localHomeRepository = LocalHomeRepository(roomService, auth)
    private val friendRequestsRepository = FriendRequestsRepository(fireBaseService)

    private var homeRepo = HomeRepository(fireBaseService)


    var federatedLoginManager = FederatedLoginManager(User(), fireBaseService)



    var loginViewModel: LoginViewModel = LoginViewModelFactory(validator, loginRepo, federatedLoginManager).create()
    var signUpViewModel: SignUpViewModel = SignUpViewModelFactory(validator, signUpRepo, localUserRepository).create()
    val addFriendViewModel = AddFriendViewModel(validator, addFriendRepository)
    val friendRequestsViewModel = FriendRequestsViewModel(friendRequestsRepository)

    var userManager: UserManager = UserManager(auth, db)



}