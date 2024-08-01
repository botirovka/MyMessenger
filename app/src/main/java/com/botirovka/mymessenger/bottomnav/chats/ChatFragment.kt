package com.botirovka.mymessenger.bottomnav.chats

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.botirovka.mymessenger.chats.Chat
import com.botirovka.mymessenger.chats.ChatsAdapter
import com.botirovka.mymessenger.databinding.FragmentChatsBinding
import com.botirovka.mymessenger.users.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.checkerframework.checker.units.qual.A

class ChatFragment: Fragment() {
    private lateinit var binding: FragmentChatsBinding
      val savedChatsList = ArrayList<Chat>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        binding.chatsRv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        Log.d("mydebug", "onCreateViewStart")
        loadChats()
        return binding.root
    }

    override fun onResume() {
        if(savedChatsList.isNotEmpty()){
            binding.chatsRv.layoutManager = LinearLayoutManager(context)
            binding.chatsRv.adapter = ChatsAdapter(savedChatsList)
        }
        super.onResume()
    }

    private fun loadChats() {
        val chatsIdForLoad = ArrayList<String>()
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if(uid != null) {
            val userChatsRef = FirebaseDatabase.getInstance().reference.child("Users").child(uid).child("chats")
            userChatsRef.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatsIdForLoad.clear()
                    if(!snapshot.exists()) return
                    for(chat in snapshot.children){
                        chatsIdForLoad.add(chat.value.toString())
                    }
                    getChatListById(chatsIdForLoad) { chatList ->
                        savedChatsList.clear()
                        savedChatsList.addAll(chatList)
                        binding.chatsRv.layoutManager = LinearLayoutManager(context)
                        binding.chatsRv.adapter = ChatsAdapter(chatList)


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    private fun getChatListById(chatsIdForLoad: ArrayList<String>, onComplete: (ArrayList<Chat>) -> Unit) {
        val chatListForLoading = ArrayList<Chat>()
        val chatsRef = FirebaseDatabase.getInstance().reference.child("Chats")
        var loadedCount = 0

        for(chatId in chatsIdForLoad){
            chatsRef.child(chatId).get().addOnSuccessListener { snapshot ->
                val firstUserId = snapshot.child("firstUserId").value.toString()
                val secondUserId = snapshot.child("secondUserId").value.toString()
                chatListForLoading.add(Chat(firstUserId,secondUserId))

                loadedCount++
                if (loadedCount == chatsIdForLoad.size) {
                    onComplete(chatListForLoading)
                }
            }
        }

    }


}