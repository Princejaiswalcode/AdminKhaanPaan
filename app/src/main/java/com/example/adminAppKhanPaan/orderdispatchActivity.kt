package com.example.adminAppKhanPaan

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminAppKhanPaan.adapter.DeliveryAdapter
import com.example.adminAppKhanPaan.databinding.ActivityOrderdispatchBinding

class orderdispatchActivity : AppCompatActivity() {
    private val binding: ActivityOrderdispatchBinding by lazy {
        ActivityOrderdispatchBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        val costomerName=arrayListOf(
            "Prince Jaiswal",
            "Millind AMb",
            "Mustaffa"
        )
        val moneystatus=arrayListOf(
            "received",
            "Notreceived",
            "Pending"
        )
        val adapter= DeliveryAdapter(costomerName,moneystatus)
        binding.deliveryrecyclerview.adapter=adapter
        binding.deliveryrecyclerview.layoutManager= LinearLayoutManager(this)
        binding.backbutton.setOnClickListener {
            finish()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}