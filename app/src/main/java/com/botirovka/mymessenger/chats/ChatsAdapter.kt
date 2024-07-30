package com.botirovka.mymessenger.chats

import android.content.Intent
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.botirovka.mymessenger.ChatActivity
import com.botirovka.mymessenger.R
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter(private val chats: ArrayList<Chat>) :
    RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chat_iv: CircleImageView
        val chat_name_tv: TextView
        val last_message_tv: TextView
        val last_message_date_tv: TextView
        val new_message_status_tv: TextView

        init {
            chat_iv = view.findViewById(R.id.person_item_profile_iv)
            chat_name_tv = view.findViewById(R.id.person_item_username_tv)
            last_message_tv = view.findViewById(R.id.person_item_message_text)
            last_message_date_tv = view.findViewById(R.id.person_item_message_date)
            new_message_status_tv = view.findViewById(R.id.person_item_new_message_tv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.chat_item_rv, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return chats.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userId: String
        if (chats[position].firstUserId == uid){
            userId = chats[position].secondUserId
        }
        else{
            userId = chats[position].firstUserId
        }

            FirebaseDatabase.getInstance().reference.child("Users")
                .child(userId)
                .get()
                .addOnCompleteListener {
                task ->
                if(task.isSuccessful){

                    try {
                        holder.chat_name_tv.text = task.result.child("nickname").value.toString()
                        val profileImageUrl = task.result.child("profileImage").value.toString()
                        if (profileImageUrl.isNotEmpty()){
                            Glide.with(holder.itemView.context)
                                .load(profileImageUrl).into(holder.chat_iv)
                            }
                        loadLastMessageIfItExist(uid,userId,holder)
                        getMessageStatus(uid,userId,holder)
                    }catch (e: Exception){
                        Toast.makeText(holder.itemView.context, "Failed to get profile image link", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ChatActivity::class.java)
            intent.putExtra("chatId", chats.get(position).chatId)
            holder.itemView.context.startActivity(intent)
            holder.new_message_status_tv.visibility = View.INVISIBLE
        }
    }

    private fun getMessageStatus(
        senderUserId: String,
        receiverUserId: String,
        holder: ChatsAdapter.ViewHolder) {

        val chatId = Chat(senderUserId,receiverUserId).chatId
        FirebaseDatabase.getInstance().reference
            .child("Chats")
            .child(chatId)
            .child("isNewFrom")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(!snapshot.exists()) return
                    Log.d("mydebug", snapshot.value.toString())
                    when(snapshot.value.toString()){

                        senderUserId -> {
                            holder.new_message_status_tv.text = "✓"
                            val color = ContextCompat.getColor(holder.itemView.context, R.color.transparent)
                            holder.new_message_status_tv.backgroundTintList = ColorStateList.valueOf(color)
                            holder.new_message_status_tv.visibility = View.VISIBLE
                        }

                        receiverUserId -> {
                            holder.new_message_status_tv.text = "new"
                            val color = ContextCompat.getColor(holder.itemView.context, R.color.primary_purple)
                            holder.new_message_status_tv.backgroundTintList = ColorStateList.valueOf(color)
                            holder.new_message_status_tv.visibility = View.VISIBLE
                        }

                        else -> {
                            holder.new_message_status_tv.text = "✓✓"
                            val color = ContextCompat.getColor(holder.itemView.context, R.color.transparent)
                            holder.new_message_status_tv.backgroundTintList = ColorStateList.valueOf(color)
                            holder.new_message_status_tv.visibility = View.VISIBLE
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun loadLastMessageIfItExist(
        senderUserId: String,
        receiverUserId: String,
        holder: ViewHolder
    ) {
        val chatId = Chat(senderUserId,receiverUserId).chatId
        FirebaseDatabase.getInstance().reference
            .child("Chats")
            .child(chatId)
            .child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) return
                    val lastMessage = snapshot.children.last()
                    holder.last_message_tv.text = lastMessage.child("text").value.toString()
                    holder.last_message_date_tv.text = lastMessage.child("date").value.toString().split(" ")[1]
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }


}