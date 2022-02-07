package com.example.letschat.chatroom.ui

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.letschat.R
import com.example.letschat.chatroom.chat.ChatMessage
import com.example.letschat.chatroom.chat.ChatRoomViewModel
import com.example.letschat.chatroom.helpers.Validator
import com.example.letschat.databinding.FragmentChatRoomBinding
import com.example.letschat.user.User
import com.google.firebase.firestore.Query
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ChatRoomFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentChatRoomBinding

    lateinit var chattingUser: User
    lateinit var chatDocId: String


    @Inject
    lateinit var messageValidator: Validator

    @Inject
    lateinit var adapter: ChatMessagesAdapter

    private val viewModel: ChatRoomViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_room, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
        receiveArguments()
        showChattingUserInfo()

        // for first time
        // observeChatDocument()

        observeMessageEditTextChanges()
        observeChatDocumentId()
        observeChatMessageLiveData()
        observeAddedMessagesToChatRoom()
        getChatDocument(chattingUser)
    }

    private fun observeAddedMessagesToChatRoom() {
        viewModel.addedMessagesToChatRoomLiveData.observe(viewLifecycleOwner) { res ->
            res.getContentIfNotHandled()?.let {
                Log.d("Here", "Message Added")
                adapter.addMessage(it)
                adapter.notifyItemInserted(adapter.itemCount - 1)
                binding.messagesRecyclerView.scrollToPosition(adapter.itemCount - 1)
            }

        }
    }

    private fun getChatDocument(chattingUser: User) {
        viewModel.getChatRoomDocument(chattingUser)
    }

    private fun observeChatDocumentId() {
        viewModel.chatRoomDocIdLiveData.observe(viewLifecycleOwner, { result ->
            result.getContentIfNotHandled()?.let {
                chatDocId = it[0]
                // get previous messages if exist

                getPreviousMessages(it[0])
                listenForChatRoomChanges(it[0])

            }
            viewModel.chatRoomDocIdLiveData.removeObservers(viewLifecycleOwner)
        }
        )
        // why removing the observer? because we have two observers on the same live data of Event
        // which does not support multiple observers

    }

    private fun listenForChatRoomChanges(chatDocId: String) {
        viewModel.listenForChatRoomChanges(chatDocId)
    }

    private fun getPreviousMessages(docId: String) {
        viewModel.getAllPreviousMessages(docId)
        viewModel.chatMessagesLiveData.observe(viewLifecycleOwner, { messages ->
            if (messages.isEmpty()) {
                showNoPreviousMessages()
            } else {
                showPreviousMessages(messages)
            }
        })
    }

    private fun showNoPreviousMessages() {
        binding.chatRoomProgressBar.visibility = View.GONE
        binding.messagesRecyclerView.visibility = View.GONE
        binding.noPreviousMessages.visibility = View.VISIBLE
        binding.roomSend.isEnabled = true
        binding.roomSend.setOnClickListener(this)

    }

    private fun showPreviousMessages(messages: List<ChatMessage>) {
        binding.chatRoomProgressBar.visibility = View.GONE
        adapter.setMessages(messages as ArrayList<ChatMessage>)
        binding.messagesRecyclerView.adapter = adapter
        val manager = LinearLayoutManager(requireContext())
        manager.stackFromEnd = true
        binding.messagesRecyclerView.layoutManager = manager
        binding.roomSend.isEnabled = true
        binding.roomSend.setOnClickListener(this)
    }

    private fun setUp() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.chatRoomProgressBar.visibility = View.VISIBLE
    }

    private fun showChattingUserInfo() {

        activity?.findViewById<TextView>(R.id.tool_bar_title)?.text = chattingUser.userName


        val toolBarIcon = activity?.findViewById<ImageView>(R.id.tool_bar_icon)!!
        Glide.with(requireContext())
            .load(chattingUser.profilePictureUrl)
            .into(toolBarIcon)
    }

    private fun receiveArguments() {
        val chatUser = ChatRoomFragmentArgs.fromBundle(requireArguments()).chattingUser
        chattingUser = chatUser
    }

    private fun observeMessageEditTextChanges() {
        binding.roomMessageEditText.addTextChangedListener {
            object : TextWatcher {

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (binding.roomMessageEditText.text.toString().trim().isEmpty()) {
                        binding.roomSend.isEnabled = false
                        binding.roomSend.setOnClickListener(null)
                    }else{
                        binding.roomSend.isEnabled = true
                        binding.roomSend.setOnClickListener(this@ChatRoomFragment)
                    }

                    //binding.roomSend.setCl
                    // binding.roomSend.isClickable = binding.roomMessageEditText.text.toString().trim().isNotEmpty()
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                }

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(view: View?) {
        when (view) {
            //binding.chatRoomBack -> onBackClicked()
            binding.roomSend -> onSendClicked()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onSendClicked() {
        Log.d("Here", "Send Clicked")
        binding.roomSend.isEnabled = false
        binding.roomSend.setOnClickListener(null)
        sendChatMessage(chatDocId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeChatDocument() {
        viewModel.chatRoomDocIdLiveData.observe(viewLifecycleOwner, { result ->
            result.getContentIfNotHandled()?.let {
                if (it.isEmpty()) {
                    Log.d("Here", "Document Doesn't exist, init it")
                    // sendFirstMessage()
                } else {
                    Log.d("Here", "Document exists")
                    val docId = it[0]
                    //sendChatMessage(docId)

                }

            }
        })
    }

    private fun addNewMessageToAdapter(chatMessage: ChatMessage) {
        adapter.addMessage(chatMessage)
        adapter.notifyDataSetChanged()
        binding.roomMessageEditText.setText("")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendChatMessage(docId: String) {
        val chatMessage = ChatMessage("", chattingUser.uid, viewModel.message, "text", Date())
        viewModel.sendChatMessage(chatMessage, docId)
    }

    private fun observeChatMessageLiveData() {
        viewModel.chatMessageLiveData.observe(viewLifecycleOwner, { result ->
            result.getContentIfNotHandled()?.let { (res, chatMessage) ->
                if (res) {
                    //addNewMessageToAdapter(chatMessage)
                    binding.messagesRecyclerView.visibility = View.VISIBLE
                    binding.noPreviousMessages.visibility = View.GONE
                    binding.roomMessageEditText.setText("")
                    binding.roomSend.isEnabled = true
                    binding.roomSend.setOnClickListener(this)
                    Toast.makeText(
                        requireContext(),
                        "Message Sent Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    binding.roomSend.isEnabled = true
                    binding.roomSend.setOnClickListener(this)
                    Toast.makeText(
                        requireContext(),
                        "Error while sending the message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })


    }
}