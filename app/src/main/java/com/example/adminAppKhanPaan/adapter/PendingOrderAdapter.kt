package com.example.adminAppKhanPaan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminAppKhanPaan.databinding.ItemPendingOrderBinding
import com.example.adminAppKhanPaan.model.OrderDetails
import org.json.JSONArray

class PendingOrderAdapter(
    private val context: Context,
    private val orders: MutableList<OrderDetails>,
    private val onAcceptClick: (OrderDetails, Int) -> Unit
) : RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderViewHolder {
        val binding = ItemPendingOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PendingOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PendingOrderViewHolder, position: Int) {
        holder.bind(orders[position], position)
    }

    override fun getItemCount(): Int = orders.size

    inner class PendingOrderViewHolder(private val binding: ItemPendingOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderDetails, position: Int) {
            binding.customerName.text    = order.userName ?: "Unknown"
            binding.customerPhone.text   = order.phone ?: ""
            binding.customerAddress.text = order.address ?: ""
            binding.totalAmount.text     = order.totalAmount ?: ""

            // ── Parse JSON food names ──────────────────────────────────────
            val foodNames = parseJsonArray(order.foodNames)
            binding.foodItems.text = foodNames.joinToString(", ")

            // ── Accept button marks order complete ─────────────────────────
            binding.acceptButton.setOnClickListener {
                onAcceptClick(order, position)
            }
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