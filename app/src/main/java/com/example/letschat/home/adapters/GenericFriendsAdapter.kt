package com.example.letschat.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.letschat.R
import com.example.letschat.home.ui.IGenericFriends
import com.example.letschat.user.User
import javax.inject.Inject

class GenericFriendsAdapter:
    RecyclerView.Adapter<GenericFriendsAdapter.ViewHolder>() {

    private val users = ArrayList<User>()
    private lateinit var genericGenericFriendsInterface: IGenericFriends

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageOfUser: ImageView = itemView.findViewById(R.id.image_of_user)
        val nameOfUser: TextView = itemView.findViewById(R.id.name_of_user)
        val acceptFriendRequest: ImageView = itemView.findViewById(R.id.accept_friend_request)
        val declineFriendRequest: ImageView = itemView.findViewById(R.id.decline_friend_request)
    }

    fun setGenericFriendsInterface(genericGenericFriendsInterface: IGenericFriends){
        this.genericGenericFriendsInterface = genericGenericFriendsInterface
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.user, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameOfUser.text = users.get(position).userName


        if (users[position].friendsOrNot) {
            holder.acceptFriendRequest.visibility = View.GONE
            holder.declineFriendRequest.visibility = View.GONE

            holder.itemView.setOnClickListener {
                genericGenericFriendsInterface.onFriendClicked(users[position])
            }
        }

        else {
            holder.acceptFriendRequest.setOnClickListener {
                genericGenericFriendsInterface.onClick(users[position], "accept")
            }

            holder.declineFriendRequest.setOnClickListener {
                genericGenericFriendsInterface.onClick(users[position], "decline")
            }
        }

    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun addFriendRequests(theUsers: ArrayList<User>) {
        users.addAll(theUsers)
    }

    fun addFriendRequest(user: User) {
        users.add(user)
    }

    fun removeFriendRequest(uid: String){
        users.filter { it.uid == uid }.let {
            users.removeAll(it)
        }
        this.notifyDataSetChanged()
    }
}