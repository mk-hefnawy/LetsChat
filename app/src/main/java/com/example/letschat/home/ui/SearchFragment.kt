package com.example.letschat.home.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.letschat.R
import com.example.letschat.databinding.FragmentSearchBinding
import com.example.letschat.di.AppContainer
import com.example.letschat.home.view_models.AddFriendViewModel
import com.example.letschat.user.User

class SearchFragment : Fragment(), View.OnClickListener {
    private lateinit var binding:  FragmentSearchBinding
    private lateinit var appContainer: AppContainer
    private lateinit var addFriendViewModel: AddFriendViewModel
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DataBindingUtil.bind(view.findViewById(R.id.add_friend_fragment_root)!!)!!
        appContainer = AppContainer(requireContext())
        addFriendViewModel = appContainer.addFriendViewModel

        binding.viewModel = addFriendViewModel
        binding.lifecycleOwner = this

        navController = findNavController()

        setUpClickListeners()
    }

    private fun setUpClickListeners() {
        binding.btnOkFriendRequest.setOnClickListener(this)
        binding.btnCancelFriendRequest.setOnClickListener(this)
        binding.resultCardView.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.btnCancelFriendRequest -> onCancelClicked()
            binding.btnOkFriendRequest -> onOkButtonClicked()
            binding.resultCardView -> onSearchedUserClicked()
        }
    }

    private fun onSearchedUserClicked() {
        navController.navigate(
            SearchFragmentDirections.actionAddFriendDialogToProfileFragment(addFriendViewModel.searchedUserLiveData.value!!)
        )
    }

    private fun onOkButtonClicked() {
        val (isUserNameValid, message) = addFriendViewModel.isUserNameValid()
        if (isUserNameValid) searchUser()
        else showErrorMessage(message)
    }

    private fun searchUser() {
        addFriendViewModel.searchUser()
        addFriendViewModel.searchedUserLiveData.observe(viewLifecycleOwner) {
            showUser(it)
        }
    }

    private fun showUser(user: User?) {
        if (user == null) {
            binding.resultCardView.visibility = View.GONE
            binding.userNameNotFound.visibility = View.VISIBLE
            Log.d("Here", "There is NO user with that user name")

        } else {
            Log.d("Here", "There is a user with that user name")
            Log.d("Here", "FriendShipStatus: ${user.friendShipStatus}")
            binding.userNameNotFound.visibility = View.GONE
            binding.resultCardView.visibility = View.VISIBLE

            binding.nameOfPotentialUser.text = user.userName
            Glide.with(requireContext())
                .load(user.profilePictureUrl)
                .placeholder(R.drawable.outline_account_circle_black_24)
                .into(binding.imageOfPotentialUser)
        }
    }

    private fun showErrorMessage(message: String) {
        binding.editTextFriendUserName.error = message
    }

    private fun onCancelClicked() {
        navController.navigateUp()
    }
}