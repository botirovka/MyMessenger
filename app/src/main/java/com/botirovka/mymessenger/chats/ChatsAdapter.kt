package com.botirovka.mymessenger.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.botirovka.mymessenger.R
import com.botirovka.mymessenger.users.User
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter(private val chats: ArrayList<Chat>) :
    RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chat_iv: CircleImageView
        val chat_name_tv: TextView

        init {
            chat_iv = view.findViewById(R.id.person_item_profile_iv)
            chat_name_tv = view.findViewById(R.id.person_item_username_tv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.person_item_rv, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid
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
                    }catch (e: Exception){
                        Toast.makeText(holder.itemView.context, "Failed to get profile image link", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}