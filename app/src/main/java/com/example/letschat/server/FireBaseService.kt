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

class FireBaseService(){

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    

    val signUpResult: MutableLiveData<SignUpResultModel> = MutableLiveData()
    val signInResultLiveData: MutableLiveData<LoginResultModel> = MutableLiveData()


    fun signUp(userName: String, email: String, password: String) {
        Log.d("Here", "Email: $email Password: $password")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Here", "Task is Successful")
                    val userId = auth.currentUser?.uid.toString()
                    val user = User(userId, "", userName, email)
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
                    println("==========")
                    Log.d("LoginActivityHere","Sign Successful")
                    val userId = auth.currentUser?.uid.toString()
                    sendSignInResult(LoginResultModel(true, userId))

                } else {
                    println("==========")
                    Log.d("LoginActivityHere","Sign Failed")
                    sendSignInResult(LoginResultModel(false, ""))
                }
            }
            .addOnFailureListener {it ->
                Log.d("LoginActivityHere","Sign in Task Failed")
                println(it.stackTrace)
            }

    }

    fun loginWithGoogle(context: Context) {

    }

     private fun addUserToFirestore(user: User){
         val theUser = hashMapOf(
            "uid" to user.uid,
            "userName" to user.userName,
            "email" to user.email,
        )
        db.collection("users")
            .add(theUser)
            .addOnSuccessListener { documentReference ->

                user.docId = documentReference.id
                notifyUiThatUserWasCreated(user)

            }
            .addOnFailureListener { e ->
                Log.v("TAG", "Error adding document", e)
                sendFireStoreRegisterError()
            }
    }

     private fun notifyUiThatUserWasCreated(user: User){
        signUpResult.value = SignUpResultModel(true, "", user)
    }

     private fun sendUserCreationError(){
        signUpResult.value = SignUpResultModel(false, "CreationError", null)
    }

     private fun sendFireStoreRegisterError(){
        signUpResult.value = SignUpResultModel(false, "FireStoreRegisterError", null)
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

    fun addFriend(userName: String) {

    }


}