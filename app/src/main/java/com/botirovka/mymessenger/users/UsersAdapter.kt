package com.botirovka.mymessenger.users

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.botirovka.mymessenger.MainActivity
import com.botirovka.mymessenger.R
import com.botirovka.mymessenger.chats.Chat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView

class UsersAdapter(
    private val users: ArrayList<User>,
    private val fragment: Fragment
) :

    RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage_iv: CircleImageView
        val username_tv: TextView

        init {
            profileImage_iv = view.findViewById(R.id.person_item_profile_iv)
            username_tv = view.findViewById(R.id.person_item_username_tv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.person_item_rv, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.username_tv.text = user.nickname

        if (user.profileImage.isNotEmpty()) {
            Glide.with(holder.itemView.context).load(user.profileImage)
                .into(holder.profileImage_iv)
        }

        holder.itemView.setOnClickListener {
            onUserClicked(user)


            goToSelectedChat()

        }
    }

    private fun onUserClicked(user: User) {
        val myUID = FirebaseAuth.getInstance().currentUser?.uid
        if (myUID != null) {
            val myUserRef = FirebaseDatabase.getInstance().reference.child("Users").child(myUID)
            myUserRef.child("chats").get().addOnSuccessListener { snapshot ->
                val listOfChats = snapshot.children.mapNotNull { it.value }
                val newChat = Chat(myUID, user.uid)

                if (listOfChats.contains(newChat.chatId)) {
                    goToSelectedChat()
                    Log.d("mydebug", "chat already exists")
                } else {
                    createNewChatWithUser(newChat)

                }

            }
        }

    }

    private fun createNewChatWithUser(newChat: Chat) {
        val newChatId = newChat.chatId
        val newChatRef = FirebaseDatabase.getInstance().reference.child("Chats").child(newChatId)

        newChatRef.setValue(newChat).addOnSuccessListener {


        }
        addChatIdReferenceForUsers(newChatId, listOf(newChat.firstUserId, newChat.secondUserId))
    }

    private fun addChatIdReferenceForUsers(chatId: String, listOfUsers: List<String>) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
        var loadedCount = 0
        for (userID in listOfUsers) {
            usersRef.child(userID).child("chats")
                .get().addOnSuccessListener { task ->
                    val userChats = task.children.map { it.value }

                    val newChats = if (userChats != null) {
                        userChats + chatId
                    } else {
                        listOf(chatId)
                    }
                    usersRef.child(userID).child("chats").setValue(newChats).addOnSuccessListener {
                        loadedCount++
                        if (loadedCount == listOfUsers.size) {
                            goToSelectedChat()
                        }
                    }

                }
        }
    }

    private fun goToSelectedChat() {
        val mainActivity = fragment.activity as? MainActivity
        mainActivity?.navigateToChatFragment()

    }

}