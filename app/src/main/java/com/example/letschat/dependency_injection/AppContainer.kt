package com.example.letschat.dependency_injection

import android.content.Context
import com.example.letschat.auth.helper_classes.*
import com.example.letschat.user.User
import com.example.letschat.auth.server.LoginRepository
import com.example.letschat.auth.server.SignUpRepository
import com.example.letschat.auth.view_models.LoginViewModel
import com.example.letschat.auth.view_models.SignUpViewModel
import com.example.letschat.home.view_models.HomeViewModel
import com.example.letschat.user.UserManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AppContainer(context: Context) {

    private var user: User = User("", "", "")
    private var validator = AuthValidator(User("", "", ""))
    private var loginRepo = LoginRepository(User("", "", ""))
    private var signUpRepo = SignUpRepository(User("", "", ""))
    private var federatedLoginManager = FederatedLoginManager(User("", "", ""))

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    var loginViewModel: LoginViewModel = LoginViewModelFactory(user, validator, loginRepo, federatedLoginManager).create()
    var homeViewModel: HomeViewModel = HomeViewModelFactory().create()
    var signUpViewModel: SignUpViewModel = SignUpViewModelFactory(user, validator, signUpRepo).create()
    var userManager: UserManager = UserManager(auth, db)



}