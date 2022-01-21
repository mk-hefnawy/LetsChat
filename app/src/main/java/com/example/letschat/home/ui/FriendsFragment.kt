package com.example.letschat.home.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letschat.R
import com.example.letschat.chatroom.ui.ChatRoomFragment
import com.example.letschat.databinding.FragmentFriendsBinding
import com.example.letschat.home.adapters.GenericFriendsAdapter
import com.example.letschat.home.view_models.FriendsViewModel
import com.example.letschat.user.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FriendsFragment : Fragment(), IGenericFriends {
    private lateinit var binding: FragmentFriendsBinding
    private val viewModel: FriendsViewModel by viewModels()

    @Inject
    lateinit var navController: NavController

    @Inject
    lateinit var adapter: GenericFriendsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_friends, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(requireContext(), "Friends Destroyed", Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
        getFriends()
        observeFriendsLiveData()
    }

    private fun getFriends() {
        viewModel.getAllFriends()
    }

    private fun observeFriendsLiveData() {
        viewModel.friendsLiveData.observe(requireActivity(), { result->
            result.getContentIfNotHandled()?.let {
                showFriends(it)
            }

        })
    }

    private fun setUp() {
        binding.friendRecyclerView.adapter = adapter
        binding.friendRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.setGenericFriendsInterface(this)
    }

    private fun showFriends(users: List<User>?) {
        users?.let {
            adapter.addFriendRequests(it as ArrayList<User>)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onClick(user: User, eventType: String) {
    }

    override fun onFriendClicked(user: User) {

        activity?.findNavController(R.id.fragmentContainerView)?.navigate(HomeFragmentDirections.actionHomeFragmentToChatRoomFragment(user))

      /*  val chatRoomFragment = ChatRoomFragment()
        val bundle = Bundle()
        bundle.putString("chattingUser", Gson().toJson(user))

        chatRoomFragment.arguments = bundle
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.home_fragment_root, chatRoomFragment)

        ft?.addToBackStack(null)
        activity?.findViewById<FloatingActionButton>(R.id.add_friend_button)?.visibility = View.GONE
        ft?.commit()*/
    }
}