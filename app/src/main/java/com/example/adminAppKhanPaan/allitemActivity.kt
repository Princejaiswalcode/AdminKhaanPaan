package com.example.adminAppKhanPaan

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminAppKhanPaan.adapter.AddItemAdapter
import com.example.adminAppKhanPaan.databinding.ActivityAllitemBinding
import com.example.adminAppKhanPaan.model.AllMenu
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class allitemActivity : AppCompatActivity() {

    private lateinit var menuItems: ArrayList<AllMenu>
    private lateinit var adapter: AddItemAdapter

    private val binding: ActivityAllitemBinding by lazy {
        ActivityAllitemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        menuItems = ArrayList()

        binding.backbutton.setOnClickListener { finish() }

        retrieveMenuItems()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun retrieveMenuItems() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ── Fetch all rows from menu table ────────────────────────────
                val result = SupabaseClient.client.postgrest
                    .from("menu")
                    .select()
                    .decodeList<AllMenu>()

                withContext(Dispatchers.Main) {
                    menuItems.clear()
                    menuItems.addAll(result)
                    setAdapter()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@allitemActivity,
                        "Failed to load items: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun deleteMenuItem(item: AllMenu, position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ── Delete row by food_name (use id if you add it to AllMenu) ─
                SupabaseClient.client.postgrest
                    .from("menu")
                    .delete {
                        filter {
                            eq("id", item.id!!)
                        }
                    }

                withContext(Dispatchers.Main) {
                    adapter.removeItem(position)
                    Toast.makeText(
                        this@allitemActivity,
                        "${item.foodName} deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@allitemActivity,
                        "Failed to delete: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun setAdapter() {
        adapter = AddItemAdapter(
            context = this@allitemActivity,
            menuList = menuItems,
            onDeleteClick = { item, position -> deleteMenuItem(item, position) }
        )
        binding.MenuRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.MenuRecyclerView.adapter = adapter
    }
}