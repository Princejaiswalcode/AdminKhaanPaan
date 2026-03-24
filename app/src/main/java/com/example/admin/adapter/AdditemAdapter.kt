package com.example.admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.databinding.ItemItemBinding

class AddItemAdapter(private val MenuItemName: ArrayList<String>,private val MenuItemPrice: ArrayList<String>,private val MenuItemImage: ArrayList<Int>) : RecyclerView.Adapter<AddItemAdapter.AddItemViewHolder>() {

    private val itemQuantities= IntArray(MenuItemName.size){1}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemViewHolder {
        val binding= ItemItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AddItemViewHolder(binding)
    }



    override fun onBindViewHolder(holder: AddItemViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount(): Int=MenuItemName.size

    inner class AddItemViewHolder(private val binding: ItemItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val quantity=itemQuantities[position]
                foodnametextview.text=MenuItemName[position]
                foodpricetextveiw.text=MenuItemPrice[position]
                foodimageview.setImageResource(MenuItemImage[position])
                quantityTextview.text=quantity.toString()
                minusButton.setOnClickListener {
                    decreasequantity(position)
                }
                increasebutton.setOnClickListener {
                    increasequantity(position)
                }
                trashbutton.setOnClickListener {
                    deletequantity(position)
                }
            }
        }
        private fun decreasequantity(position: Int) {
            if(itemQuantities[position]>1){
                itemQuantities[position]--
                binding.quantityTextview.text=itemQuantities[position].toString()
            }
        }
        private fun increasequantity(position: Int) {
            if(itemQuantities[position]<10){
                itemQuantities[position]++
                binding.quantityTextview.text=itemQuantities[position].toString()
            }        }
        private fun deletequantity(position: Int) {
            MenuItemName.removeAt(position)
            MenuItemPrice.removeAt(position)
            MenuItemImage.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,MenuItemName.size)



        }
    }
}