package com.example.letschat.chatroom.ui

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.letschat.R
import com.example.letschat.chatroom.chat.ChatMessageBuilder
import com.example.letschat.chatroom.chat.ChatRoomViewModel
import com.example.letschat.chatroom.helpers.Validator
import com.example.letschat.databinding.FragmentChatRoomBinding
import com.example.letschat.user.User
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import javax.inject.Inject


@AndroidEntryPoint
class ChatRoomFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentChatRoomBinding
    lateinit var chattingUser: User

    @Inject
    lateinit var messageValidator: Validator

    private val viewModel: ChatRoomViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_room, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
        receiveArguments()
        setUpClickListeners()
        showChattingUserInfo()
        observeMessageEditTextChanges()
    }

    private fun setUp() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun showChattingUserInfo() {
        binding.chatRoomUserName.text = chattingUser.userName
    }

    private fun receiveArguments() {
        val chattingUserString = arguments?.getString("chattingUser")
        val chatUser = Gson().fromJson(chattingUserString, User::class.java)
        chattingUser = chatUser
    }

    private fun setUpClickListeners() {
        binding.chatRoomBack.setOnClickListener(this)
    }

    private fun observeMessageEditTextChanges(){
        binding.roomMessageEditText.addTextChangedListener { object : TextWatcher{

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.roomSend.isEnabled = p0.toString().trim().isNotEmpty()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

        } }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(view: View?) {
        when(view){
            binding.chatRoomBack -> onBackClicked()
            binding.roomSend -> onSendClicked()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onSendClicked() {
        val chatMessageBuilder = ChatMessageBuilder()
        chatMessageBuilder.receiverId(chattingUser.uid)
        chatMessageBuilder.message(viewModel.message)
        chatMessageBuilder.messageType("text")
        chatMessageBuilder.time(LocalDateTime.now())
        viewModel.sendChatMessage(chatMessageBuilder.getChatMessage())
    }

    private fun onBackClicked() {
        activity?.onBackPressed()
    }
}