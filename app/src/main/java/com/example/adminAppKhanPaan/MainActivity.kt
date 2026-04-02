package com.example.adminAppKhanPaan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminAppKhanPaan.databinding.ActivityMainBinding
import com.example.adminAppKhanPaan.model.OrderDetails
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // ── Navigation clicks ──────────────────────────────────────────────
        binding.addmenu.setOnClickListener {
            startActivity(Intent(this, additemActivity::class.java))
        }
        binding.createnewadmin.setOnClickListener {
            startActivity(Intent(this, CreateNewAdminActivity::class.java))
        }
        binding.profile.setOnClickListener {
            startActivity(Intent(this, profileActivity::class.java))
        }
        binding.allitemmenu.setOnClickListener {
            startActivity(Intent(this, allitemActivity::class.java))
        }
        binding.orderdispatch.setOnClickListener {
            startActivity(Intent(this, orderdispatchActivity::class.java))
        }
        binding.logout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // ── Click on Pending section (text + number both) ──────────────────
        binding.textView6.setOnClickListener {
            startActivity(Intent(this, PendingOrderActivity::class.java))
        }
        binding.textView.setOnClickListener {
            startActivity(Intent(this, PendingOrderActivity::class.java))
        }
        binding.imageView2.setOnClickListener {
            startActivity(Intent(this, PendingOrderActivity::class.java))
        }

        // ── Click on Completed section (text + number both) ────────────────
        binding.textView8.setOnClickListener {
            startActivity(Intent(this, CompletedOrderActivity::class.java))
        }
        binding.textView12.setOnClickListener {
            startActivity(Intent(this, CompletedOrderActivity::class.java))
        }
        binding.imageView3.setOnClickListener {
            startActivity(Intent(this, CompletedOrderActivity::class.java))
        }

        // ── Load order counts + earning ────────────────────────────────────
        loadOrderCounts()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadOrderCounts() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val allOrders = SupabaseClient.client.postgrest
                    .from("order_details")
                    .select()
                    .decodeList<OrderDetails>()

                val pendingOrders   = allOrders.filter { it.orderAccepted == false }
                val completedOrders = allOrders.filter { it.orderAccepted == true }

                // ── Calculate total earning from completed orders ───────────
                val totalEarning = completedOrders.sumOf { order ->
                    calculateOrderTotal(order)
                }

                withContext(Dispatchers.Main) {
                    binding.textView.text   = pendingOrders.size.toString()
                    binding.textView12.text = completedOrders.size.toString()
                    binding.totalEarning.text = "$$totalEarning"
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to load: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // ── Calculate total for one order ──────────────────────────────────────
    private fun calculateOrderTotal(order: OrderDetails): Int {
        return try {
            val prices     = JSONArray(order.foodPrices ?: "[]")
            val quantities = JSONArray(order.foodQuantities ?: "[]")
            var total = 0
            for (i in 0 until prices.length()) {
                val price = prices.getString(i)
                    .replace("$", "")
                    .trim()
                    .toIntOrNull() ?: 0
                val qty = quantities.getInt(i)
                total += price * qty
            }
            total
        } catch (e: Exception) {
            // ── Fallback: use total_amount field directly ──────────────────
            order.totalAmount
                ?.replace("$", "")
                ?.trim()
                ?.toIntOrNull() ?: 0
        }
    }
}