package com.example.letschat.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.letschat.R
import com.example.letschat.databinding.FragmentProfileBinding
import com.example.letschat.di.AppContainer
import com.example.letschat.home.view_models.AddFriendViewModel
import com.google.gson.Gson

class ProfileFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var appContainer: AppContainer
    private lateinit var addFriendViewModel: AddFriendViewModel
    private lateinit var searchedUser: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.setContentView(requireActivity(), R.layout.fragment_profile)
        appContainer = AppContainer(requireContext())
        addFriendViewModel = appContainer.addFriendViewModel
        binding.viewModel = addFriendViewModel
        binding.lifecycleOwner = this

        val userJson= Gson().toJson(arguments?.get("searchedUser"))
        searchedUser = Gson().fromJson(userJson, User::class.java)

        observeAddFriend()
        setClickListeners()
        showUserInfo(searchedUser)
    }

    private fun observeAddFriend() {
        addFriendViewModel.isUserAddFriendRequestSuccessful.observe(viewLifecycleOwner, {event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    Toast.makeText(requireContext(), "Friend Request Sent", Toast.LENGTH_SHORT).show()
                    // toggle the add button
                    toggleAddButton()
                }
                else{
                    Toast.makeText(requireContext(), "Friend Request Failed", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun toggleAddButton() {
        binding.theUserAdd.text = "Cancel Friend Request"
    }

    private fun setClickListeners() {
        binding.theUserAdd.setOnClickListener(this)
    }

    private fun showUserInfo(user: User?) {
        binding.theUserName.text = user?.userName
    }

    override fun onClick(view: View?) {
        when(view){
            binding.theUserAdd -> onAddClicked(searchedUser)
        }
    }

    private fun onAddClicked(searchedUser: User) {
        addFriendViewModel.addUser(searchedUser)
    }

}