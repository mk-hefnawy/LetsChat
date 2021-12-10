package com.example.letschat.dependency_injection

import android.content.Context
import com.example.letschat.auth.helper_classes.AuthValidator
import com.example.letschat.auth.helper_classes.FederatedLoginManager
import com.example.letschat.auth.helper_classes.LoginViewModelFactory
import com.example.letschat.user.User
import com.example.letschat.auth.server.LoginRepository
import com.example.letschat.auth.view_models.LoginViewModel
import com.example.letschat.user.UserManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AppContainer(context: Context) {

    private var user: User = User("", "", "")
    private var validator = AuthValidator(User("", "", ""))
    private var loginRepo = LoginRepository(User("", "", ""))
    private var federatedLoginManager = FederatedLoginManager(User("", "", ""))

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    var loginViewModel: LoginViewModel = LoginViewModelFactory(user, validator, loginRepo, federatedLoginManager).create()
    var userManager: UserManager = UserManager(auth, db)



}