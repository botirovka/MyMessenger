package com.botirovka.mymessenger.bottomnav.profile

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.botirovka.mymessenger.LoginActivity
import com.botirovka.mymessenger.databinding.FragmentProfileBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment: Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var filepath: Uri



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        loadUserInfo()


        binding.profileImageView.setOnClickListener {
            selectImage()
        }

        binding.buttonLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            activity?.finish()
            startActivity(Intent(context, LoginActivity::class.java))
        }

        return binding.root
    }

    private var selectImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                filepath = uri
                binding.profileImageView.setImageURI(uri)
                Log.d("mydebug", "image was set by LAUNCHER")
                uploadImage()
            }
        }
    }

    private fun loadUserInfo() {
        Log.d("mydebug", "UserInfo is loaded")
        val id = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val user = FirebaseDatabase.getInstance().reference.child("Users").child(id)

        user.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
              if (!isAdded) return
              val userName = snapshot.child("nickname").value.toString()
              binding.usernameTv.text = userName

              val profileImage = snapshot.child("profileImage").value.toString()
                if (profileImage.isNotEmpty()){
                    Log.d("mydebug", "image was set by GLIDE")
                 Glide.with(this@ProfileFragment).load(profileImage).into(binding.profileImageView)
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectImageLauncher.launch(intent)
    }

    private fun uploadImage(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        FirebaseStorage.getInstance().reference.child("images/$uid")
            .putFile(filepath).addOnSuccessListener {
                FirebaseStorage.getInstance().reference.child("images/$uid").downloadUrl.addOnCompleteListener {
                    task ->
                    Log.d("mydebug", task.result.toString())
                    FirebaseDatabase.getInstance().reference
                        .child("Users")
                        .child(uid)
                        .child("profileImage")
                        .setValue(task.result.toString())
                }

            }
    }
}