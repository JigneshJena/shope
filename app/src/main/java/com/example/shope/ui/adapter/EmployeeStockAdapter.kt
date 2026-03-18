package com.example.shope.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shope.R
import com.example.shope.data.models.UniformItem
import com.example.shope.databinding.ItemInventoryBinding
import com.example.shope.utils.ImageUtils
import java.text.NumberFormat
import java.util.Locale

class EmployeeStockAdapter(
    private val onUpdateQuantity: (UniformItem) -> Unit
) : ListAdapter<UniformItem, EmployeeStockAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemInventoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: UniformItem) {
            binding.tvItemName.text = item.itemName
            // We use the same layout but repurpose some fields if needed
            // Let's add the school name to the item name or price area
            val displayName = if (item.schoolName.isNotEmpty()) {
                "${item.itemName} (${item.schoolName})"
            } else {
                item.itemName
            }
            binding.tvItemName.text = displayName
            
            val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
            binding.tvItemPrice.text = currencyFormatter.format(item.price)
            binding.tvItemQuantity.text = item.quantity.toString()
            
            // Stock Status
            val status = when {
                item.quantity == 0 -> "Out of Stock"
                item.quantity < 5 -> "Low Stock"
                else -> "In Stock"
            }
            binding.tvStockStatus.text = status
            
            val color = when (status) {
                "Out of Stock" -> R.color.error
                "Low Stock" -> R.color.warning
                else -> R.color.success
            }
            binding.tvStockStatus.setTextColor(ContextCompat.getColor(binding.root.context, color))
            binding.tvStockStatus.setBackgroundResource(
                when (status) {
                    "Out of Stock" -> R.drawable.bg_badge_error
                    "Low Stock" -> R.drawable.bg_badge_warning
                    else -> R.drawable.bg_badge_success
                }
            )

            if (item.itemImage.isNotEmpty()) {
                val bitmap = ImageUtils.base64ToBitmap(item.itemImage)
                if (bitmap != null) {
                    binding.ivItem.setImageBitmap(bitmap)
                } else {
                    binding.ivItem.setImageResource(R.drawable.ic_placeholder)
                }
            } else {
                binding.ivItem.setImageResource(R.drawable.ic_placeholder)
            }

            binding.root.setOnClickListener {
                onUpdateQuantity(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemInventoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<UniformItem>() {
        override fun areItemsTheSame(oldItem: UniformItem, newItem: UniformItem): Boolean =
            oldItem.id == newItem.id && oldItem.schoolId == newItem.schoolId

        override fun areContentsTheSame(oldItem: UniformItem, newItem: UniformItem): Boolean =
            oldItem == newItem
    }
}
