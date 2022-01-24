package com.example.letschat.chatroom.ui

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.letschat.R
import com.example.letschat.chatroom.chat.ChatMessage
import com.example.letschat.chatroom.util.DateUtil
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class ChatMessagesAdapter @Inject constructor(val auth: FirebaseAuth) : RecyclerView.Adapter<ChatMessagesAdapter.ChatMessageHolder>() {
    lateinit var chatMessages: ArrayList<ChatMessage>

    companion object {
        const val CURRENT_USER_VIEW_TYPE = 0
        const val OTHER_USER_VIEW_TYPE = 1
    }

    lateinit var viewHolder: ChatMessageHolder


    class ChatMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatMessageTextView: TextView = itemView.findViewById(R.id.chat_message_text_view)
        private val messageTimeContainer: LinearLayout = itemView.findViewById(R.id.the_message_time)
        val messageTimeTextView: TextView = messageTimeContainer.findViewById(R.id.message_time)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatMessageHolder {
        when (viewType) {
            CURRENT_USER_VIEW_TYPE ->
                viewHolder = ChatMessageHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.chat_message_current_user, parent, false)
                )

            OTHER_USER_VIEW_TYPE ->
                viewHolder = ChatMessageHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.chat_message_other_user, parent, false)
                )
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ChatMessageHolder, position: Int) {
        holder.chatMessageTextView.text = chatMessages[position].message


        val messageDate =  chatMessages[position].time
        var dateString :String? = null
        if (DateUtils.isToday(messageDate?.time!!)){ // today
            dateString = DateUtil.dateToString(messageDate, "hh:mm aa").uppercase()
        }
        else if (DateUtil.isYesterday(messageDate)){
            dateString = "Yesterday " + DateUtil.dateToString(messageDate, "hh:mm aa").uppercase()
        }
        else{
            dateString = DateUtil.dateToString(messageDate, "EEE-hh:mm aa").uppercase()
        }
        holder.messageTimeTextView.text = dateString
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatMessages[position].senderId == auth.currentUser?.uid!!) CURRENT_USER_VIEW_TYPE
        else {
            OTHER_USER_VIEW_TYPE
        }
    }

    fun setMessages(messages: ArrayList<ChatMessage>){
        chatMessages = messages
    }

    fun addMessage(message: ChatMessage){
        chatMessages.add(message)
    }

}