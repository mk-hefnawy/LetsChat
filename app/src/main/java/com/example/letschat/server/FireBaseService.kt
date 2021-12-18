package com.example.letschat.server

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.letschat.auth.models.LoginResultModel
import com.example.letschat.auth.models.SignUpResultModel
import com.example.letschat.user.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FireBaseService(var user: User){

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    

    val signUpResult: MutableLiveData<SignUpResultModel> = MutableLiveData()
    val signInResultLiveData: MutableLiveData<LoginResultModel> = MutableLiveData()


    fun signUp() {

    auth.createUserWithEmailAndPassword(user.email, user.password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid.toString()
                addUserToFirestore(userId)

            } else {
                sendUserCreationError()
                print(task.exception)
            }
        }
    }

    fun loginWithEmailAndPassword() {
        auth.signInWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("==========")
                    println("Sign with Email and Password was Successful")
                    val userId = auth.currentUser?.uid.toString()
                    sendSignInResult(LoginResultModel(true, userId))

                } else {
                    println("==========")
                    println("Sign with Email and Password Failed")
                    sendSignInResult(LoginResultModel(false, ""))
                }
            }
            .addOnFailureListener {it ->
                println("----------------------------------------")
                println(it.stackTrace)
            }

    }

    fun loginWithGoogle(context: Context) {

    }

     private fun addUserToFirestore(userId: String){
         val user = hashMapOf(
            "id" to userId,
            "userName" to user.userName,
            "email" to user.email,
        )
        db.collection("users")
            .document(this.user.email)
            .set(user)
            .addOnSuccessListener { documentReference ->
                // Log.v("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
                Log.v("TAG", "DocumentSnapshot added with ID: ${this.user.userName}")
                notifyUiThatUserWasCreated(userId)

            }
            .addOnFailureListener { e ->
                Log.v("TAG", "Error adding document", e)
                sendFireStoreRegisterError()
            }
    }

     private fun notifyUiThatUserWasCreated(userId: String){
        signUpResult.value = SignUpResultModel(true, "", userId)
    }

     private fun sendUserCreationError(){
        signUpResult.value = SignUpResultModel(false, "CreationError", "")
    }

     private fun sendFireStoreRegisterError(){
        signUpResult.value = SignUpResultModel(false, "FireStoreRegisterError", "")
    }

     fun isUserAlreadyLoggedIn(): Pair<Boolean, String>{
        val currentUser = auth.currentUser
        if(currentUser != null){
            val uId = currentUser.uid
            return Pair(true, uId) // then the activity/viewModel will use that uid to grab the data of that user to update the Ui based on that data
        }
        else {
           return Pair(false, "")
        }
    }

     private fun sendSignInResult(signInResult: LoginResultModel){
        signInResultLiveData.value = signInResult
    }

     fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(OnCompleteListener { task->
                if(task.isSuccessful){
                    println("Email is sent to $email")
                }else {
                    println("Email is sent to $email")
                }
            })
    }


}