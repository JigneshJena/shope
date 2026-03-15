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
            binding.tvItemPrice.text = "₹${item.price} | Qty: ${item.quantity}"
            
            if (item.itemImage.isNotEmpty()) {
                binding.ivItemImage.setImageBitmap(ImageUtils.base64ToBitmap(item.itemImage))
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
            oldItem == newItem

        override fun areContentsTheSame(oldItem: UniformItem, newItem: UniformItem): Boolean =
            oldItem == newItem
    }
}
