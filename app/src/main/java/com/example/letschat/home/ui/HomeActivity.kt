package com.example.letschat.home.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.letschat.MyApplication
import com.example.letschat.home.view_models.HomeViewModel
import com.example.letschat.R
import com.example.letschat.auth.ui.LoginActivity
import com.example.letschat.databinding.ActivityHomeBinding
import com.example.letschat.dependency_injection.AppContainer
import com.example.letschat.user.UserManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class HomeActivity : AppCompatActivity() {

    lateinit var appContainer: AppContainer
    private lateinit var logOutButton: Button
    private lateinit var homeBinding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var userManager: UserManager
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    val user = mutableMapOf<String, Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appContainer = (application as MyApplication).appContainer
        homeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeBinding.viewModel = homeViewModel
        homeBinding.lifecycleOwner = this

        userManager = appContainer.userManager
        userManager.getUserInfo("ho5a")

        println("--------------------------------------------------------------")
        println(auth.currentUser?.email)

        // createUserInFireStore(db)

        logOutButton = homeBinding.logoutButton
        logOutButton.setOnClickListener{ view ->
            signOut(auth)
        }
    }

    private fun signOut(auth: FirebaseAuth) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()


        val signInGoogleUser = GoogleSignIn.getClient(this, gso)

        auth.signOut()
        signInGoogleUser.signOut()
        val logOutIntent = Intent(this, LoginActivity::class.java)
        logOutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(logOutIntent)
    }

    private fun createUserInFireStore(db: FirebaseFirestore) {
        // auth.currentUser?.uid?.let { user.put("Id", it) }
        user.put("FirstName", "HHHHHHHHHHHHHHHHHHHH")
        user.put("LastName", "Khaled")
        user.put("Age", "23")

        db.collection("users")
            .add(user)
    }
}