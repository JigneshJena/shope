package com.example.shope.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shope.data.models.OrderItem
import com.example.shope.databinding.ItemInventoryBinding
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private val onRemoveItem: (String) -> Unit
) : ListAdapter<OrderItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = com.example.shope.databinding.ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(private val binding: com.example.shope.databinding.ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrderItem) {
            binding.tvItemName.text = item.itemName
            val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
            binding.tvItemPrice.text = currencyFormatter.format(item.price)
            binding.tvItemQuantity.text = "x${item.quantity}"
            binding.tvItemSubtotal.text = currencyFormatter.format(item.subtotal)
            
            binding.btnRemove.setOnClickListener {
                onRemoveItem(item.itemId)
            }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<OrderItem>() {
        override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean =
            oldItem.itemId == newItem.itemId

        override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean =
            oldItem == newItem
    }
}
