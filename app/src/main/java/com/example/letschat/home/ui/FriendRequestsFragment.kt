package com.example.letschat.home.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letschat.R
import com.example.letschat.databinding.FragmentFriendRequestsBinding
import com.example.letschat.di.AppContainer
import com.example.letschat.home.adapters.GenericFriendsAdapter
import com.example.letschat.home.view_models.FriendRequestsViewModel
import com.example.letschat.user.User
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FriendRequestsFragment : Fragment(), IGenericFriends {
    private lateinit var appContainer: AppContainer
    private lateinit var binding: FragmentFriendRequestsBinding
    private lateinit var viewModel: FriendRequestsViewModel

    @Inject
    lateinit var adapter: GenericFriendsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_friend_requests, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()
        observeFriendRequests()
        observeFriendRequestReaction()
    }

    private fun setUp() {
        appContainer = AppContainer(requireContext())

        viewModel = appContainer.friendRequestsViewModel
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        adapter.setGenericFriendsInterface(this)
    }

    private fun observeFriendRequests() {
        viewModel.getAllFriendRequests()
        viewModel.allReceivedFriendRequestsLiveData.observe(viewLifecycleOwner, { users ->
            if (users.isNotEmpty()) {
                showFriendRequests(users)
            }else{
                showNoFriendRequests()
            }
        })
    }

    private fun showNoFriendRequests() {
        binding.noFriendRequestsTextView.visibility = View.VISIBLE
        binding.friendRequestsRecyclerView.visibility = View.GONE
    }

    private fun observeFriendRequestReaction() {
        viewModel.friendRequestReactionLiveData.observe(viewLifecycleOwner, { result ->
            result.getContentIfNotHandled()?.let {
                if (it.result) {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    removeFriendRequestFromRecyclerView(it.uid)
                } else {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun removeFriendRequestFromRecyclerView(uid: String) {
        adapter.removeFriendRequest(uid)
    }

    private fun showFriendRequests(users: List<User>?) {
        users?.let {
            adapter.addFriendRequests(it as ArrayList<User>)
            binding.friendRequestsRecyclerView.adapter = adapter
            binding.friendRequestsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onClick(user: User, eventType: String) {
        viewModel.reactToFriendRequest(user, eventType)
    }

    override fun onFriendClicked(user: User) {
        TODO("Not yet implemented")
    }
}