package com.example.letschat.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
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

        binding = DataBindingUtil.bind(view.findViewById(R.id.profile_fragment_root)!!)!!

        appContainer = AppContainer(requireContext())
        addFriendViewModel = appContainer.addFriendViewModel
        binding.viewModel = addFriendViewModel
        binding.lifecycleOwner = this

        searchedUser = ProfileFragmentArgs.fromBundle(requireArguments()).searchedUser

        observeAddFriend()
        setClickListeners()
        updateUi(searchedUser)
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
        binding.theUserFriends.visibility = View.GONE
        binding.theUserAdd.visibility = View.GONE
        binding.theUserAcceptFriendRequest.visibility = View.GONE
        binding.theUserCancelAdd.visibility = View.VISIBLE
    }

    private fun setClickListeners() {
        binding.theUserAdd.setOnClickListener(this)
    }

    private fun updateUi(user: User?) {
        user?.let {
            binding.theUserName.text = it.userName
            Glide.with(requireContext())
                .load(it.profilePictureUrl)
                .placeholder(R.drawable.outline_account_circle_black_48)
                .into(binding.theUserImage)

            toggleFriendShipButton(user)

        }

    }

    private fun toggleFriendShipButton(user: User) {
        when(user.friendShipStatus){
            FriendShipStatus.FRIENDS -> {
                binding.theUserFriends.visibility = View.VISIBLE
                binding.theUserAdd.visibility = View.GONE
                binding.theUserAcceptFriendRequest.visibility = View.GONE
                binding.theUserCancelAdd.visibility = View.GONE
            }
            FriendShipStatus.THEY_SENT_A_FRIEND_REQUEST -> {
                binding.theUserFriends.visibility = View.GONE
                binding.theUserAdd.visibility = View.GONE
                binding.theUserAcceptFriendRequest.visibility = View.VISIBLE
                binding.theUserCancelAdd.visibility = View.GONE
            }
            FriendShipStatus.YOU_SENT_A_FRIEND_REQUEST -> {
                binding.theUserFriends.visibility = View.GONE
                binding.theUserAdd.visibility = View.GONE
                binding.theUserAcceptFriendRequest.visibility = View.GONE
                binding.theUserCancelAdd.visibility = View.VISIBLE
            }
            FriendShipStatus.NONE ->{
                binding.theUserFriends.visibility = View.GONE
                binding.theUserAdd.visibility = View.VISIBLE
                binding.theUserAcceptFriendRequest.visibility = View.GONE
                binding.theUserCancelAdd.visibility = View.GONE
            }
        }
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