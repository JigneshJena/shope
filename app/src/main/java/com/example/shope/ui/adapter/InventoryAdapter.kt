package com.example.shope.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shope.R
import com.example.shope.data.models.Inventory
import com.example.shope.databinding.ItemInventoryBinding
import java.text.NumberFormat
import java.util.Locale

class InventoryAdapter(private val onItemClick: (Inventory) -> Unit) :
    ListAdapter<Inventory, InventoryAdapter.InventoryViewHolder>(InventoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val binding = ItemInventoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InventoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class InventoryViewHolder(private val binding: ItemInventoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(adapterPosition))
                }
            }
        }

        fun bind(item: Inventory) {
            binding.tvItemName.text = item.itemName
            
            val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
            binding.tvItemPrice.text = currencyFormatter.format(item.sellingPrice)
            
            binding.tvItemQuantity.text = "Qty: ${item.quantity}"
            
            binding.tvStockStatus.text = item.getStockStatus()
            
            val statusColor = when {
                item.isOutOfStock() -> R.color.error
                item.isLowStock() -> R.color.warning
                else -> R.color.success
            }
            binding.tvStockStatus.setTextColor(ContextCompat.getColor(binding.root.context, statusColor))
            
            if (item.itemImage.isNotEmpty()) {
                try {
                    val decodedString = android.util.Base64.decode(item.itemImage, android.util.Base64.DEFAULT)
                    val decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    binding.ivItem.setImageBitmap(decodedByte)
                } catch (e: Exception) {
                    binding.ivItem.setImageResource(R.drawable.ic_placeholder) // Fallback
                }
            } else {
                binding.ivItem.setImageResource(R.drawable.ic_placeholder)
            }
        }
    }

    class InventoryDiffCallback : DiffUtil.ItemCallback<Inventory>() {
        override fun areItemsTheSame(oldItem: Inventory, newItem: Inventory): Boolean {
            return oldItem.itemId == newItem.itemId
        }

        override fun areContentsTheSame(oldItem: Inventory, newItem: Inventory): Boolean {
            return oldItem == newItem
        }
    }
}
