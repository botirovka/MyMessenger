package com.botirovka.mymessenger

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.botirovka.mymessenger.databinding.ActivityChatBinding
import com.botirovka.mymessenger.message.Message
import com.botirovka.mymessenger.message.MessagesAdapter
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Date

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var profileImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyWindowInsets()
        val chatId = intent.getStringExtra("chatId")
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
        loadMessages(chatId)
        loadProfile(chatId)
        binding.buttonSendMessage.setOnClickListener {
            val date = simpleDateFormat.format(Date())
            if(binding.messageEt.text.toString().isEmpty()){
                Toast.makeText(this,"Message cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendMessage(chatId, date)
        }

        binding.buttonChatSettings.setOnClickListener {
            val popupMenu = PopupMenu(baseContext, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId){
                    R.id.delete_chat -> deleteChat(chatId)
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.chat_settings_menu)
            popupMenu.show()
        }

        binding.backToChatIv.setOnClickListener{
            finish()
        }

    }

    private fun loadProfile(chatId: String?) {
            if(chatId == null) return
        val chatRef = FirebaseDatabase.getInstance().reference
            .child("Chats")
            .child(chatId)

        chatRef.get().addOnSuccessListener {chatSnapshot ->
            if(!chatSnapshot.exists()) return@addOnSuccessListener
            val firstUserId = chatSnapshot.child("firstUserId").value.toString()
            val secondUserId = chatSnapshot.child("secondUserId").value.toString()
            val senderId = FirebaseAuth.getInstance().currentUser?.uid.toString()

            val receiverUserId: String
            if(senderId == firstUserId){
                receiverUserId = secondUserId
            }
            else{
                receiverUserId = firstUserId
            }

            val receiverUserRef = FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(receiverUserId)

            receiverUserRef.get().addOnSuccessListener { userSnapshot ->
                val username = userSnapshot.child("nickname").value.toString()
                profileImage = userSnapshot.child("profileImage").value.toString()

                binding.profileNicknameTv.text = username
                if(profileImage.isNotEmpty()){
                    Glide.with(this).load(profileImage).into(binding.profileImageIv)
                }
            }
        }
    }

    private fun deleteChat(chatId: String?): Boolean {
        if(chatId == null) return false

        val alertCustomDialog = LayoutInflater.from(this).inflate(R.layout.custom_dialog_delete, null)
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setView(alertCustomDialog)

        val cancelButton = alertCustomDialog.findViewById<TextView>(R.id.cancel_delete_chat_tv)
        val confirmButton = alertCustomDialog.findViewById<TextView>(R.id.delete_chat_tv)
        val profileImageView = alertCustomDialog.findViewById<CircleImageView>(R.id.dialog_profile_image_iv)

        val dialog = alertDialog.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if(profileImage.isNotEmpty()){
            Glide.with(this).load(profileImage).into(profileImageView)
        }

        dialog.show()
        confirmButton.setOnClickListener {
            dialog.cancel()
            val chatRef = FirebaseDatabase.getInstance().reference
                .child("Chats")
                .child(chatId)

            chatRef.get().addOnSuccessListener {chatSnapshot ->
                if(!chatSnapshot.exists()) return@addOnSuccessListener
                val firstUserId = chatSnapshot.child("firstUserId").value.toString()
                val secondUserId = chatSnapshot.child("secondUserId").value.toString()
                chatRef.removeValue().addOnSuccessListener {
                    for (user in listOf(firstUserId,secondUserId)){
                        FirebaseDatabase.getInstance().reference
                            .child("Users")
                            .child(user)
                            .child("chats").get().addOnSuccessListener { userChatsSnapshot ->
                                val userChats = userChatsSnapshot.children
                                val chatForDeleteKey = userChats.find { it.value == chatId }?.key.toString()
                                FirebaseDatabase.getInstance().reference
                                    .child("Users")
                                    .child(user)
                                    .child("chats")
                                    .child(chatForDeleteKey)
                                    .removeValue().addOnSuccessListener {
                                        finish()
                                    }
                            }
                    }
                }
            }
        }
        cancelButton.setOnClickListener {
            dialog.cancel()
        }

        return true
    }

    private fun sendMessage(chatId: String?, date: String){
        if(chatId == null) return

        val newMessageRef = FirebaseDatabase.getInstance().reference
            .child("Chats")
            .child(chatId)
            .child("messages")
            .push()

        val messageId = newMessageRef.key ?: return
        val ownerId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val text = binding.messageEt.text.toString()
        val newMessage = Message(messageId,ownerId,text,date)

        newMessageRef.setValue(newMessage).addOnSuccessListener {
            FirebaseDatabase.getInstance().reference
                .child("Chats")
                .child(chatId)
                .child("isNewFrom")
                .setValue(ownerId)
        }
        binding.messageEt.text.clear()
    }

    private fun loadMessages(chatId: String?){
        if(chatId == null) return
        val messagesRef = FirebaseDatabase.getInstance().reference.child("Chats").child(chatId).child("messages")
        messagesRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()) return
                val listOfMessages = ArrayList<Message>()
                for (message in snapshot.children){
                    val messageId = message.key ?: return
                    val ownerId = message.child("ownerId").value.toString()
                    val text = message.child("text").value.toString()
                    val date = message.child("date").value.toString()

                    listOfMessages.add(
                        Message(messageId, ownerId, text, date))
                }
                binding.messagesRv.layoutManager = LinearLayoutManager(baseContext)
                binding.messagesRv.adapter = MessagesAdapter(listOfMessages)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
         val isNewFromRef = FirebaseDatabase.getInstance().reference
            .child("Chats")
            .child(chatId)
            .child("isNewFrom")

            isNewFromRef.get().addOnSuccessListener { snapshot ->
                val ownerId = FirebaseAuth.getInstance().currentUser?.uid ?: return@addOnSuccessListener

                if (snapshot.exists() && snapshot.value.toString() != ownerId) {
                    isNewFromRef.setValue("")
                }


            }

    }

    private fun applyWindowInsets(){
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}