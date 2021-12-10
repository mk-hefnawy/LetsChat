package com.example.letschat.user

import com.example.letschat.server.FireBaseService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

class UserManager(val auth: FirebaseAuth, val db: FirebaseFirestore) {

    fun isUserLoggedIn(){}

    // getter
    fun getUserInfo(info: String){
        val email = getUserEmail()
        db.collection("users").document(email).get()
            .addOnSuccessListener { documentSnapshot ->
                val userName = documentSnapshot.get("userName")
                println("----------")
                print(userName)
            }
            .addOnFailureListener {
                println("----------")
                print(it.stackTrace)
            }
    }

    private fun getUserEmail(): String{
        return auth.currentUser?.email.toString()

    }
    // setters

    // delete my account


}