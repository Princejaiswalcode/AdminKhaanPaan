package com.example.adminAppKhanPaan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminAppKhanPaan.databinding.ActivitySignupBinding
import com.example.adminAppKhanPaan.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var userName: String
    private lateinit var nameOfRestaurant: String
    private lateinit var database: DatabaseReference


    private val binding: ActivitySignupBinding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth= Firebase.auth

        database= Firebase.database.reference


        binding.createUserButton.setOnClickListener {
            userName=binding.name.text.toString().trim()
            nameOfRestaurant=binding.resturantName.text.toString().trim()
            email=binding.emailOrPhone.text.toString().trim()
            password=binding.password.text.toString().trim()

            if(userName.isBlank() || nameOfRestaurant.isBlank() || email.isBlank() || password.isBlank()){
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }else{
                CreateAccount(email,password)
            }

        }

        binding.alreadyHaveAccountButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        val listoflocation = arrayOf("Jaipur","Ujjain","Indore","Delhi","Mumbai","Agra")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            listoflocation
        )

        binding.listoflocation.setAdapter(adapter)
    }
    private fun CreateAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){task->
                if(task.isSuccessful){
                    Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                    saveUserData()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this, "Account Creation Failed", Toast.LENGTH_SHORT).show()
                    Log.d("Account","createAccount:failure",task.exception)
                }
            }
    }

    private fun saveUserData(){
        userName=binding.name.text.toString().trim()
        nameOfRestaurant=binding.resturantName.text.toString().trim()
        email=binding.emailOrPhone.text.toString().trim()
        password=binding.password.text.toString().trim()
        val user= UserModel(userName,nameOfRestaurant,email,password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("user").child(userId).setValue(user)
    }
}