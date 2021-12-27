package com.example.letschat.dependency_injection

import android.content.Context
import com.example.letschat.auth.helper_classes.*
import com.example.letschat.auth.server.local.AppRoomDatabase
import com.example.letschat.auth.server.local.RoomService
import com.example.letschat.auth.server.local.user.LocalUserRepository
import com.example.letschat.user.User
import com.example.letschat.auth.server.remote.LoginRepository
import com.example.letschat.auth.server.remote.SignUpRepository
import com.example.letschat.auth.view_models.LoginViewModel
import com.example.letschat.auth.view_models.SignUpViewModel
import com.example.letschat.home.server.HomeRepository
import com.example.letschat.home.view_models.HomeViewModel
import com.example.letschat.server.FireBaseService
import com.example.letschat.user.UserManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AppContainer(context: Context) {

    private var user: User = User("", "", "")
    private val fireBaseService = FireBaseService()
    private val roomService: RoomService = RoomService(context)
    private var validator = AuthValidator()

    private var loginRepo = LoginRepository()
    private var signUpRepo = SignUpRepository(User("", "", ""))
    private var localUserRepository = LocalUserRepository(roomService)
    private var homeRepo = HomeRepository(fireBaseService)

    private var federatedLoginManager = FederatedLoginManager(User("", "", ""))

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    var loginViewModel: LoginViewModel = LoginViewModelFactory(validator, loginRepo, federatedLoginManager).create()
    var homeViewModel: HomeViewModel = HomeViewModelFactory(validator, homeRepo).create()
    var signUpViewModel: SignUpViewModel = SignUpViewModelFactory(user, validator, signUpRepo, localUserRepository).create()
    var userManager: UserManager = UserManager(auth, db)



}