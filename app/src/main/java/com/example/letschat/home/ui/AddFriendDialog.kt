package com.example.letschat.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.letschat.R
import com.example.letschat.databinding.FragmentAddFriendBinding
import com.example.letschat.di.AppContainer
import com.example.letschat.home.view_models.AddFriendViewModel
import com.example.letschat.user.User


class AddFriendDialog : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentAddFriendBinding
    private lateinit var appContainer: AppContainer
    private lateinit var addFriendViewModel: AddFriendViewModel
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_add_friend, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.setContentView(requireActivity(), R.layout.fragment_add_friend)
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
            AddFriendDialogDirections.actionAddFriendDialogToProfileFragment(addFriendViewModel.searchedUserLiveData.value!!))
    }

    private fun onOkButtonClicked() {
        val (isUserNameValid, message) = addFriendViewModel.isUserNameValid()
        if (isUserNameValid) searchUser()
        else showErrorMessage(message)
    }

    private fun searchUser() {
        addFriendViewModel.searchUser()
        addFriendViewModel.searchedUserLiveData.observe(viewLifecycleOwner){
            showUser(it)
        }
    }

    private fun showUser(friend: User?) {
        binding.nameOfPotentialUser.text = friend?.userName
        binding.resultCardView.visibility = View.VISIBLE
    }

    private fun showErrorMessage(message: String) {
        binding.editTextFriendUserName.error = message
    }

    /*private fun onOkClicked() {
        lifecycleScope.launch(Dispatchers.Main) {
            homeViewModel.getUserDocumentId()
            homeViewModel.userDocumentIdLiveData.observe(viewLifecycleOwner, {
                addFriend(it)
            })
        }
    }*/

    private fun onCancelClicked() {
        navController.navigate(AddFriendDialogDirections.actionAddFriendToHome())
    }

    /*private fun addFriend(docId: String) {
        addFriendViewModel.addFriend(docId)
        addFriendViewModel.addFriendLiveData.observe(viewLifecycleOwner, {
            if (it) {
                showAddFriendIsSuccessful()
            } else {
                showAddFriendHasFailed()
            }
            goToHomeFragment()
        })
    }*/

    private fun goToHomeFragment() {
        //navController.navigate(AddFriendDialogDirections.actionAddFriendToHome(""))
        navController.popBackStack(R.id.homeFragment, true)
        //requireActivity().supportFragmentManager.beginTransaction().remove(this)
    }

    private fun showAddFriendIsSuccessful() {
        Toast.makeText(requireContext(), "Add Friend was sent successfully", Toast.LENGTH_SHORT)
            .show()
    }

    private fun showAddFriendHasFailed() {
        Toast.makeText(requireContext(), "Add Friend has failed", Toast.LENGTH_SHORT).show()
    }
}