package com.example.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.admin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.addmenu.setOnClickListener {
            val intent = Intent(this, additemActivity::class.java)
            startActivity(intent)
        }
        binding.createnewadmin.setOnClickListener {
            val intent = Intent(this, CreateNewAdminActivity::class.java)
            startActivity(intent)
        }
        binding.profile.setOnClickListener {
            val intent = Intent(this, profileActivity::class.java)
            startActivity(intent)
        }
        binding.allitemmenu.setOnClickListener {
            val intent = Intent(this, allitemActivity::class.java)
            startActivity(intent)
        }
        binding.orderdispatch.setOnClickListener {
            val intent = Intent(this, orderdispatchActivity::class.java)
            startActivity(intent)
        }
        binding.logout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}