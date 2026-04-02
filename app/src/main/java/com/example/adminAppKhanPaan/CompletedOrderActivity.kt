package com.example.adminAppKhanPaan

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminAppKhanPaan.adapter.CompletedOrderAdapter
import com.example.adminAppKhanPaan.databinding.ActivityCompletedOrderBinding
import com.example.adminAppKhanPaan.model.OrderDetails
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CompletedOrderActivity : AppCompatActivity() {

    private val binding: ActivityCompletedOrderBinding by lazy {
        ActivityCompletedOrderBinding.inflate(layoutInflater)
    }

    private val completedOrders = mutableListOf<OrderDetails>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        loadCompletedOrders()
    }

    private fun loadCompletedOrders() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ── Fetch only accepted/completed orders ───────────────────
                val result = SupabaseClient.client.postgrest
                    .from("order_details")
                    .select {
                        filter { eq("order_accepted", true) }
                    }
                    .decodeList<OrderDetails>()

                withContext(Dispatchers.Main) {
                    completedOrders.clear()
                    completedOrders.addAll(result)
                    setAdapter()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CompletedOrderActivity,
                        "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setAdapter() {
        val adapter = CompletedOrderAdapter(this, completedOrders)
        binding.completedOrderRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.completedOrderRecyclerView.adapter = adapter
    }
}