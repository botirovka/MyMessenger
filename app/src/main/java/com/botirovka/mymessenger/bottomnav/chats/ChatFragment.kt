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
import com.google.firebase.database.FirebaseDatabase
import org.checkerframework.checker.units.qual.A

class ChatFragment: Fragment() {
    private lateinit var binding: FragmentChatsBinding
    private lateinit var chatsAdapter: ChatsAdapter
    private val chatList = ArrayList<Chat>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        loadChats()


    }

    private fun initRecyclerView() {
        chatsAdapter = ChatsAdapter(chatList)
        binding.chatsRv.layoutManager = LinearLayoutManager(context)
        binding.chatsRv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        binding.chatsRv.adapter = chatsAdapter
    }

    private fun loadChats() {
        val chatsIdForLoad = ArrayList<String>()
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if(uid != null) {
            val userChatsRef = FirebaseDatabase.getInstance().reference.child("Users").child(uid).child("chats")
            userChatsRef.get().addOnSuccessListener {snapshot ->

                for(chat in snapshot.children){
                    chatsIdForLoad.add(chat.value.toString())
                }
                getChatListById(chatsIdForLoad) { chatList ->

                    this.chatList.clear()
                    this.chatList.addAll(chatList)
                    chatsAdapter.notifyDataSetChanged()

                }
            }
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