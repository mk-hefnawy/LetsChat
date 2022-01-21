package com.example.letschat.home.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letschat.R
import com.example.letschat.chatroom.chat.ChatMessage
import com.example.letschat.chatroom.ui.ChatRoomFragment
import com.example.letschat.databinding.FragmentChatsBinding
import com.example.letschat.di.AppContainer
import com.example.letschat.home.adapters.ChatsAdapter
import com.example.letschat.home.view_models.ChatsViewModel
import com.example.letschat.server.remote.FireBaseService
import com.example.letschat.user.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatsFragment : Fragment(), ChatsInterface {
    private lateinit var binding: FragmentChatsBinding

    val viewModel: ChatsViewModel by viewModels()

    @Inject
    lateinit var adapter: ChatsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
        observeAllChatsFromCache()
        observeLastMessagesForEachChat()
        observeTheListOfChattingUsersWithTheLastMessage()
        getAllChatsFromCache()

    }

    private fun setUp() {

    }

    private fun getAllChatsFromCache(){
        viewModel.getAllChatsFromCache()
    }

    private fun observeAllChatsFromCache(){
        viewModel.allChatsLiveData.observe(viewLifecycleOwner, {listOfChatDocsIds ->
            listOfChatDocsIds.getContentIfNotHandled()?.let {
                getLastMessageOfEachChat(it)
            }

        })
    }

    private fun getLastMessageOfEachChat(listOfChatDocsIds: List<String>?) {
        viewModel.getLastMessageOfEachChat(listOfChatDocsIds)
    }

    private fun observeLastMessagesForEachChat(){
        viewModel.listOfLastMessagesForEachChatLiveData.observe(viewLifecycleOwner, {
                viewModel.getTheChattingUsers(it)
            }
        )
    }

    private fun observeTheListOfChattingUsersWithTheLastMessage(){
        viewModel.listOfChattingUsersWithTheLastMessageLiveData.observe(viewLifecycleOwner, {
                showChats(it)
            }
        )
    }

    private fun showChats(chats: List<Pair<ChatMessage, User>>?) {
        adapter.setTheChats(chats as ArrayList<Pair<ChatMessage, User>>)
        adapter.setTheChatsInterface(this)
        binding.chatsRecyclerView.adapter = adapter
        binding.chatsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onChatClicked(user: User) {
        activity?.findNavController(R.id.fragmentContainerView)?.navigate(HomeFragmentDirections.actionHomeFragmentToChatRoomFragment(user))
    }

}