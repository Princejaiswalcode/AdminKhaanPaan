package com.example.admin.adapter
import android.R
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.databinding.DeliveryitemBinding

class DeliveryAdapter(private val costomerNames: ArrayList<String>,private val moneystatus: ArrayList<String>) : RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
    val binding= DeliveryitemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DeliveryViewHolder(binding)
    }


    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
    holder.bind(position)
    }
    override fun getItemCount(): Int=costomerNames.size

    inner class DeliveryViewHolder(private val binding: DeliveryitemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                costumername.text=costomerNames[position]
                statusofmoney.text=moneystatus[position]
                val colormap=mapOf(
                    "received" to Color.GREEN,
                    "Notreceived" to Color.RED,
                    "Pending" to Color.GRAY

                )
                statusofmoney.setTextColor(colormap[moneystatus[position]]?:Color.BLACK)
                status.backgroundTintList= ColorStateList.valueOf(colormap[moneystatus[position]]?:Color.BLACK)

            }
        }

    }
}