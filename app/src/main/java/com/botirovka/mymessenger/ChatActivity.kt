package com.botirovka.mymessenger

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.botirovka.mymessenger.databinding.ActivityChatBinding
import com.botirovka.mymessenger.message.Message
import com.botirovka.mymessenger.message.MessagesAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyWindowInsets()
        val chatId = intent.getStringExtra("chatId")
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
        loadMessages(chatId)

        binding.buttonSendMessage.setOnClickListener {
            val date = simpleDateFormat.format(Date())
            if(binding.messageEt.text.toString().isEmpty()){
                Toast.makeText(this,"Message cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendMessage(chatId, date)
        }


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

        newMessageRef.setValue(newMessage)
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
    }

    private fun applyWindowInsets(){
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}