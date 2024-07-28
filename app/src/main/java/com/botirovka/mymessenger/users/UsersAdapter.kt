package com.botirovka.mymessenger.users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.botirovka.mymessenger.R
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class UsersAdapter(private val users: ArrayList<User>):
RecyclerView.Adapter<UsersAdapter.ViewHolder>(){

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
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
        holder.username_tv.text = users[position].nickname

        if(users[position].profileImage.isNotEmpty()){
            Glide.with(holder.itemView.context).
            load(users[position].profileImage).
            into(holder.profileImage_iv)
        }
    }
}