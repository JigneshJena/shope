package com.example.shope.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shope.R
import com.example.shope.data.models.Inventory
import com.example.shope.databinding.ItemInventoryBinding
import com.example.shope.databinding.ItemInventoryGridBinding
import java.text.NumberFormat
import java.util.Locale

class InventoryAdapter(
    private val isGrid: Boolean = false,
    private val onItemClick: (Inventory) -> Unit
) : ListAdapter<Inventory, RecyclerView.ViewHolder>(InventoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (isGrid) {
            GridViewHolder(
                ItemInventoryGridBinding.inflate(layoutInflater, parent, false)
            )
        } else {
            ListViewHolder(
                ItemInventoryBinding.inflate(layoutInflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (holder is ListViewHolder) {
            holder.bind(item)
        } else if (holder is GridViewHolder) {
            holder.bind(item)
        }
    }

    inner class ListViewHolder(private val binding: ItemInventoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(bindingAdapterPosition))
                }
            }
        }

        fun bind(item: Inventory) {
            binding.tvItemName.text = item.itemName
            val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
            binding.tvItemPrice.text = currencyFormatter.format(item.sellingPrice)
            binding.tvItemQuantity.text = item.quantity.toString()
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
                    binding.ivItem.setImageResource(R.drawable.ic_placeholder)
                }
            } else {
                binding.ivItem.setImageResource(R.drawable.ic_placeholder)
            }
        }
    }

    inner class GridViewHolder(private val binding: ItemInventoryGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(bindingAdapterPosition))
                }
            }
        }

        fun bind(item: Inventory) {
            binding.tvItemName.text = item.itemName
            binding.tvItemCategory.text = item.category
            val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
            binding.tvItemPrice.text = currencyFormatter.format(item.sellingPrice)
            binding.tvItemQuantity.text = item.quantity.toString()
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
                    binding.ivItem.setImageResource(R.drawable.ic_placeholder)
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
