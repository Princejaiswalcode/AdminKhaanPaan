package com.example.adminAppKhanPaan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminAppKhanPaan.databinding.ItemItemBinding
import com.example.adminAppKhanPaan.model.AllMenu

class AddItemAdapter(
    private val context: Context,
    private val menuList: ArrayList<AllMenu>,
    private val onDeleteClick: (AllMenu, Int) -> Unit   // ← delete callback
) : RecyclerView.Adapter<AddItemAdapter.AddItemViewHolder>() {

    private val itemQuantities = IntArray(menuList.size) { 1 }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemViewHolder {
        val binding = ItemItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuList.size

    inner class AddItemViewHolder(private val binding: ItemItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.apply {
                val menuItem: AllMenu = menuList[position]

                foodnametextview.text = menuItem.foodName
                foodpricetextveiw.text = menuItem.foodPrice
                quantityTextview.text = itemQuantities[position].toString()

                // ── Load image via Glide ───────────────────────────────────────
                Glide.with(context)
                    .load(menuItem.foodImage)   // direct URL string, no Uri.parse needed
                    .into(foodimageview)

                // ── Quantity controls ─────────────────────────────────────────
                minusButton.setOnClickListener {
                    if (itemQuantities[position] > 1) {
                        itemQuantities[position]--
                        quantityTextview.text = itemQuantities[position].toString()
                    }
                }

                increasebutton.setOnClickListener {
                    if (itemQuantities[position] < 10) {
                        itemQuantities[position]++
                        quantityTextview.text = itemQuantities[position].toString()
                    }
                }

                // ── Delete — delegates to Activity via callback ───────────────
                trashbutton.setOnClickListener {
                    onDeleteClick(menuItem, position)
                }
            }
        }
    }

    // Call this from Activity after confirming delete in Supabase
    fun removeItem(position: Int) {
        menuList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, menuList.size)
    }
}