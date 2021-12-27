package com.example.letschat.home.ui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.letschat.MyApplication
import com.example.letschat.databinding.DialougeAddFriendBinding
import com.example.letschat.dependency_injection.AppContainer
import com.example.letschat.home.view_models.HomeViewModel

class AddFriendDialog (context: Context, val homeViewModel: HomeViewModel, private val layoutId: Int): Dialog(context), View.OnClickListener {

    private lateinit var binding: DialougeAddFriendBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)

        setClickListeners()
    }

    private fun setClickListeners() {
        binding.btnOkFriendRequest.setOnClickListener(this)
        binding.btnCancelFriendRequest.setOnClickListener(this)
    }

    override fun onClick(view: View){
        when(view){
            binding.btnCancelFriendRequest -> this.dismiss()
            binding.btnOkFriendRequest -> onOkButtonClicked()
        }
    }

    private fun onOkButtonClicked() {
        val (isUserNameValid, message) = homeViewModel.isUserNameValid()
        if (isUserNameValid) homeViewModel.addFriend()
        else showErrorMessage(message)
    }

    private fun showErrorMessage(message: String) {
        binding.editTextFriendUserName.error = message
    }
}