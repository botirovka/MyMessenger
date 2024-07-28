package com.botirovka.mymessenger.bottomnav.new_chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.botirovka.mymessenger.databinding.FragmentNewChatBinding
import com.botirovka.mymessenger.users.User
import com.botirovka.mymessenger.users.UsersAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class NewChatFragment: Fragment() {
    private lateinit var binding: FragmentNewChatBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewChatBinding.inflate(inflater, container, false)
        getAllUsersFromDB()
        return binding.root
    }

    private fun getAllUsersFromDB() {
        val users = ArrayList<User>()
        FirebaseDatabase.getInstance().reference.child("Users").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val context = context ?: return
                    for(userSnapshot in snapshot.children){
                        if(userSnapshot.key == FirebaseAuth.getInstance().currentUser?.uid){
                            continue
                        }
                        val email = userSnapshot.child("email").value.toString()
                        val username = userSnapshot.child("nickname").value.toString()
                        val profileImage = userSnapshot.child("profileImage").value.toString()

                        users.add(User(email,username ,profileImage))
                    }
                    val usersAdapter = UsersAdapter(users)
                    binding.usersRv.layoutManager = LinearLayoutManager(context)
                    binding.usersRv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                    binding.usersRv.adapter = usersAdapter
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}