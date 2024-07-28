package com.botirovka.mymessenger.bottomnav.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.botirovka.mymessenger.databinding.FragmentChatsBinding

class ChatFragment: Fragment() {
    private lateinit var binding: FragmentChatsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }
}