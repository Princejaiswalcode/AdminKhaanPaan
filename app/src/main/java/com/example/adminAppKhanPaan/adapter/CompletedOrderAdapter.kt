package com.example.adminAppKhanPaan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminAppKhanPaan.databinding.ItemCompletedOrderBinding
import com.example.adminAppKhanPaan.model.OrderDetails
import org.json.JSONArray

class CompletedOrderAdapter(
    private val context: Context,
    private val orders: MutableList<OrderDetails>
) : RecyclerView.Adapter<CompletedOrderAdapter.CompletedOrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedOrderViewHolder {
        val binding = ItemCompletedOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CompletedOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompletedOrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    inner class CompletedOrderViewHolder(private val binding: ItemCompletedOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderDetails) {
            binding.customerName.text    = order.userName ?: "Unknown"
            binding.customerPhone.text   = order.phone ?: ""
            binding.customerAddress.text = order.address ?: ""
            binding.totalAmount.text     = order.totalAmount ?: ""

            val foodNames = parseJsonArray(order.foodNames)
            binding.foodItems.text = foodNames.joinToString(", ")
        }
    }

    private fun parseJsonArray(json: String?): List<String> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            val array = JSONArray(json)
            List(array.length()) { i -> array.getString(i) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}