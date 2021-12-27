package com.example.letschat.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class UserManager(val auth: FirebaseAuth, val db: FirebaseFirestore) {

    fun isUserLoggedIn(){}

    // getter
    fun getUserName(){
        val email = getUserEmail()
        db.collection("users").document(email).get()
            .addOnSuccessListener { documentSnapshot ->
                val userName = documentSnapshot.get("userName")
                println("User Name")
                println(userName)
            }
            .addOnFailureListener {
                println("UserName Error")
                println(it.stackTrace)
            }
    }

    private fun getUserEmail(): String{

        return auth.currentUser?.email.toString()

    }
    // setters

    // delete my account


}