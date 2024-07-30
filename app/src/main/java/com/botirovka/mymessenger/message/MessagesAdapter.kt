package com.botirovka.mymessenger.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.botirovka.mymessenger.R
import com.google.firebase.auth.FirebaseAuth

class MessagesAdapter(private val messages: List<Message>) :
    RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val messageText: TextView
        val messageDate: TextView
        init {
            messageText = view.findViewById(R.id.message_text_tv)
            messageDate = view.findViewById(R.id.message_date_tv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(viewType, parent, false)

        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if(messages[position].ownerId == currentUserUid){
            return R.layout.message_from_curr_user_rv_item
        }
        else{
            return R.layout.message_rv_item
        }
    }
    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message: Message = messages[position]

        holder.messageText.text = message.text
        holder.messageDate.text = message.date
    }
}