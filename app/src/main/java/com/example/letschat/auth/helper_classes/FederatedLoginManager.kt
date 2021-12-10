package com.example.letschat.auth.helper_classes

import android.content.Context
import com.example.letschat.R
import com.example.letschat.user.User
import com.example.letschat.auth.server.LoginRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


class FederatedLoginManager(user: User): LoginRepository(user) {


    fun loginWithGoogle(context: Context){
        super.fireBaseService.loginWithGoogle(context)
    }

    fun getGoogleClientForLogin(context: Context){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }
}