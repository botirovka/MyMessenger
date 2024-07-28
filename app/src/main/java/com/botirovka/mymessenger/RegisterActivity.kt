package com.botirovka.mymessenger

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.botirovka.mymessenger.databinding.ActivityRegisterBinding
import com.botirovka.mymessenger.users.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goToLoginActivityTv.setOnClickListener {
            finish()
        }
        binding.registerActivityButtonSignUp.setOnClickListener {
            if (binding.registerActivityEditTextEmail.text.toString().isEmpty() ||
                binding.registerActivityEditTextUsername.text.toString().isEmpty()||
                binding.registerActivityEditTextPassword.text.toString().isEmpty()){
                Toast.makeText(this,"Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
            else{
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.registerActivityEditTextEmail.text.toString(),
                    binding.registerActivityEditTextPassword.text.toString()).addOnCompleteListener { task ->
                      if (task.isSuccessful){
                          FirebaseAuth.getInstance().currentUser?.uid?.let { id ->
                              val email = binding.registerActivityEditTextEmail.text.toString()
                              val username = binding.registerActivityEditTextUsername.text.toString()
                              val user = User(email,username,"")
                              FirebaseDatabase.getInstance().reference.child("Users").child(id).setValue(user).addOnCompleteListener {
                                  if (it.isSuccessful){
                                      Toast.makeText(this, "Succesfull added to DB", Toast.LENGTH_SHORT ).show()
                                  }
                                  else{
                                      Toast.makeText(this, "Problem with DB", Toast.LENGTH_SHORT ).show()
                                  }
                              }
                          }
                          startActivity(Intent(this, MainActivity::class.java))
                      }
                    else{
                        Toast.makeText(this, "Something went WRONG", Toast.LENGTH_SHORT).show()
                      }
                }

            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}