package com.example.admin

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.admin.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private val binding: ActivitySignupBinding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.createaccountbutton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.alreadyhaveaccountbutton.setOnClickListener {
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
}