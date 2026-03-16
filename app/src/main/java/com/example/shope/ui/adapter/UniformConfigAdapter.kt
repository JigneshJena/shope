package com.example.shope.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shope.R
import com.example.shope.data.models.UniformItem
import com.example.shope.databinding.ItemUniformConfigBinding
import com.example.shope.utils.ImageUtils

class UniformConfigAdapter(
    private val onEdit: (Int) -> Unit,
    private val onRemove: (Int) -> Unit
) : ListAdapter<UniformItem, UniformConfigAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemUniformConfigBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UniformItem, position: Int) {
            binding.tvItemName.text = item.itemName
            binding.tvItemCategory.text = item.category
            binding.tvItemPrice.text = "₹${item.price}"
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
            binding.tvStockStatus.setTextColor(androidx.core.content.ContextCompat.getColor(binding.root.context, color))
            
            if (item.itemImage.isNotEmpty()) {
                val bitmap = ImageUtils.base64ToBitmap(item.itemImage)
                if (bitmap != null) {
                    binding.ivItemImage.setImageBitmap(bitmap)
                } else {
                    binding.ivItemImage.setImageResource(R.drawable.ic_placeholder)
                }
            } else {
                binding.ivItemImage.setImageResource(R.drawable.ic_placeholder)
            }

            binding.btnEdit.setOnClickListener { onEdit(position) }
            binding.btnRemove.setOnClickListener { onRemove(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemUniformConfigBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<UniformItem>() {
        override fun areItemsTheSame(oldItem: UniformItem, newItem: UniformItem): Boolean =
            (oldItem.id.isNotEmpty() && oldItem.id == newItem.id) ||
            (oldItem.itemName == newItem.itemName && oldItem.schoolId == newItem.schoolId)

        override fun areContentsTheSame(oldItem: UniformItem, newItem: UniformItem): Boolean =
            oldItem == newItem
    }
}
