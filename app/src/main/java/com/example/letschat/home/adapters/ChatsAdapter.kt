package com.example.letschat.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.letschat.R
import com.example.letschat.chatroom.chat.ChatMessage
import com.example.letschat.home.ui.ChatsInterface
import com.example.letschat.server.remote.FireBaseService
import com.example.letschat.user.User
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class ChatsAdapter @Inject constructor(val auth: FirebaseAuth) : RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>() {
    lateinit var chats: ArrayList<Pair<ChatMessage, User>>
    lateinit var chatsInterface: ChatsInterface

    class ChatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameOfChattingUser: TextView = itemView.findViewById(R.id.name_of_chatting_user)
        val lastMessageTextView: TextView = itemView.findViewById(R.id.last_message_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        return ChatsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.chat, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        holder.nameOfChattingUser.text = chats[position].second.userName

        if (auth.currentUser?.uid == chats[position].first.senderId){
            val message = "You: " + chats[position].first.message
            holder.lastMessageTextView.text = message
        }
        else{
            holder.lastMessageTextView.text = chats[position].first.message
        }

        holder.itemView.setOnClickListener {
            chatsInterface.onChatClicked(chats[position].second)
        }
    }

    override fun getItemCount(): Int = chats.size

    fun setTheChats(chats: ArrayList<Pair<ChatMessage, User>>){
        this.chats = chats
    }

    fun setTheChatsInterface(chatsInterface: ChatsInterface){
        this.chatsInterface = chatsInterface
    }
}