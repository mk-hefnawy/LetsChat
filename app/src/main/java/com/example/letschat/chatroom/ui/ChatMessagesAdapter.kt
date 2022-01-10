package com.example.letschat.chatroom.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.letschat.R
import com.example.letschat.chatroom.chat.ChatMessage

class ChatMessagesAdapter: RecyclerView.Adapter<ChatMessagesAdapter.ChatMessageHolder>() {
    private val chatMessages = ArrayList<ChatMessage>()

    class ChatMessageHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val chatMessageTextView: TextView = itemView.findViewById(R.id.chat_message_text_view)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatMessagesAdapter.ChatMessageHolder {
        return ChatMessageHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_message, parent, false))
    }

    override fun onBindViewHolder(holder: ChatMessagesAdapter.ChatMessageHolder, position: Int) {
        holder.chatMessageTextView.text = chatMessages[position].message
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

}