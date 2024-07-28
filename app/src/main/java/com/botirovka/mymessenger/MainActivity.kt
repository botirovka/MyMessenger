package com.botirovka.mymessenger

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.botirovka.mymessenger.bottomnav.chats.ChatFragment
import com.botirovka.mymessenger.bottomnav.new_chat.NewChatFragment
import com.botirovka.mymessenger.bottomnav.profile.ProfileFragment
import com.botirovka.mymessenger.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNav: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        replaceFragment(ChatFragment())
        setupBottomNavigation()
        applyWindowInsets()
    }

    private fun setupBottomNavigation() {
        bottomNav = binding.bottomNav
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.chats -> replaceFragment(ChatFragment())
                R.id.newchat -> replaceFragment(NewChatFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
                else -> false
            }
        }
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun navigateToChatFragment() {
        bottomNav.selectedItemId = R.id.chats
        replaceFragment(ChatFragment())
    }

    private fun replaceFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        return true
    }
}