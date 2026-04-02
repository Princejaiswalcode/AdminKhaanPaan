package com.example.adminAppKhanPaan

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminAppKhanPaan.adapter.PendingOrderAdapter
import com.example.adminAppKhanPaan.databinding.ActivityPendingOrderBinding
import com.example.adminAppKhanPaan.model.OrderDetails
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PendingOrderActivity : AppCompatActivity() {

    private val binding: ActivityPendingOrderBinding by lazy {
        ActivityPendingOrderBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: PendingOrderAdapter
    private val pendingOrders = mutableListOf<OrderDetails>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        loadPendingOrders()
    }

    private fun loadPendingOrders() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ── Fetch only pending (not accepted) orders ───────────────
                val result = SupabaseClient.client.postgrest
                    .from("order_details")
                    .select {
                        filter { eq("order_accepted", false) }
                    }
                    .decodeList<OrderDetails>()

                withContext(Dispatchers.Main) {
                    pendingOrders.clear()
                    pendingOrders.addAll(result)
                    setAdapter()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PendingOrderActivity,
                        "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setAdapter() {
        adapter = PendingOrderAdapter(
            context = this,
            orders  = pendingOrders,
            onAcceptClick = { order, position -> acceptOrder(order, position) }
        )
        binding.pendingOrderRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.pendingOrderRecyclerView.adapter = adapter
    }

    private fun acceptOrder(order: OrderDetails, position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ── Mark order as accepted in Supabase ────────────────────
                SupabaseClient.client.postgrest
                    .from("order_details")
                    .update({ set("order_accepted", true) }) {
                        filter { eq("id", order.id!!) }
                    }

                withContext(Dispatchers.Main) {
                    pendingOrders.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    adapter.notifyItemRangeChanged(position, pendingOrders.size)
                    Toast.makeText(this@PendingOrderActivity,
                        "Order accepted!", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PendingOrderActivity,
                        "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}