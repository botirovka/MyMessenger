package com.botirovka.mymessenger.bottomnav.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.botirovka.mymessenger.chats.Chat
import com.botirovka.mymessenger.chats.ChatsAdapter
import com.botirovka.mymessenger.databinding.FragmentChatsBinding
import com.botirovka.mymessenger.users.User

class ChatFragment: Fragment() {
    private lateinit var binding: FragmentChatsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatsBinding.inflate(inflater, container, false)

        loadChats()
        return binding.root
    }

    private fun loadChats() {
        val chats = ArrayList<Chat>()
        chats.add(Chat("0","test",
            "LfzKpd2UenZOuZSz4bXV4m76yiZ2", "ikS3lEX52ZVvzyXMeM0vr0l116E2"))

        val chatsAdapter = ChatsAdapter(chats)
        binding.chatsRv.layoutManager  = LinearLayoutManager(context)
        binding.chatsRv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        binding.chatsRv.adapter = chatsAdapter

    }
}