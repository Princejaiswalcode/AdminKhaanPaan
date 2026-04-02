package com.example.adminAppKhanPaan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminAppKhanPaan.databinding.DeliveryitemBinding
import com.example.adminAppKhanPaan.model.OrderDetails

class DeliveryAdapter(
    private val orders: MutableList<OrderDetails>,
    private val onPaymentReceived: (OrderDetails, Int) -> Unit
) : RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val binding = DeliveryitemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DeliveryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        holder.bind(orders[position], position)
    }

    override fun getItemCount(): Int = orders.size

    inner class DeliveryViewHolder(private val binding: DeliveryitemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderDetails, position: Int) {
            // ── Customer name ──────────────────────────────────────────────
            binding.costumername.text = order.userName ?: "Unknown"

            // ── Total amount ───────────────────────────────────────────────
            binding.textView11.text = order.totalAmount ?: "$0"

            // ── Payment status ─────────────────────────────────────────────
            val isReceived = order.paymentReceived == true

            binding.statusofmoney.text = if (isReceived) "Received" else "Not Received"
            binding.statusofmoney.setTextColor(
                if (isReceived)
                    android.graphics.Color.parseColor("#4CAF50")  // green
                else
                    android.graphics.Color.parseColor("#FF3B3B")  // red
            )

            // ── Status dot color ───────────────────────────────────────────
            binding.status.setCardBackgroundColor(
                if (isReceived)
                    android.graphics.Color.parseColor("#4CAF50")  // green
                else
                    android.graphics.Color.parseColor("#FF3B3B")  // red
            )

            // ── Click dot to mark payment as received ──────────────────────
            binding.status.setOnClickListener {
                if (!isReceived) {
                    onPaymentReceived(order, position)
                }
            }
        }
    }
}