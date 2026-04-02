package com.example.adminAppKhanPaan

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminAppKhanPaan.adapter.DeliveryAdapter
import com.example.adminAppKhanPaan.databinding.ActivityOrderdispatchBinding
import com.example.adminAppKhanPaan.model.OrderDetails
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class orderdispatchActivity : AppCompatActivity() {

    private val binding: ActivityOrderdispatchBinding by lazy {
        ActivityOrderdispatchBinding.inflate(layoutInflater)
    }

    private val orderList = mutableListOf<OrderDetails>()
    private lateinit var adapter: DeliveryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backbutton.setOnClickListener { finish() }

        loadDispatchOrders()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadDispatchOrders() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ── Fetch accepted orders (out for delivery) ───────────────
                val result = SupabaseClient.client.postgrest
                    .from("order_details")
                    .select {
                        filter { eq("order_accepted", true) }
                    }
                    .decodeList<OrderDetails>()

                withContext(Dispatchers.Main) {
                    orderList.clear()
                    orderList.addAll(result)
                    setAdapter()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@orderdispatchActivity,
                        "Failed to load: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun setAdapter() {
        adapter = DeliveryAdapter(
            orders          = orderList,
            onPaymentReceived = { order, position -> markPaymentReceived(order, position) }
        )
        binding.deliveryrecyclerview.layoutManager = LinearLayoutManager(this)
        binding.deliveryrecyclerview.adapter = adapter
    }

    private fun markPaymentReceived(order: OrderDetails, position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ── Update payment_received = true in Supabase ─────────────
                SupabaseClient.client.postgrest
                    .from("order_details")
                    .update({ set("payment_received", true) }) {
                        filter { eq("id", order.id!!) }
                    }

                withContext(Dispatchers.Main) {
                    // ── Refresh item to show updated status ────────────────
                    val updatedOrder = order.copy(paymentReceived = true)
                    orderList[position] = updatedOrder
                    adapter.notifyItemChanged(position)
                    Toast.makeText(
                        this@orderdispatchActivity,
                        "Payment marked as received",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@orderdispatchActivity,
                        "Failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}