package com.example.shope.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shope.data.models.User
import com.example.shope.databinding.ItemOwnerBinding
import com.example.shope.utils.ImageUtils

class OwnerAdapter(
    private val onEditClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit
) : ListAdapter<User, OwnerAdapter.OwnerViewHolder>(OwnerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
        val binding = ItemOwnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OwnerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OwnerViewHolder(private val binding: ItemOwnerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(owner: User) {
            binding.tvOwnerName.text = owner.name
            binding.tvOwnerEmail.text = owner.email
            binding.tvOwnerPhone.text = owner.phone
            
            if (owner.profilePicture.isNotEmpty()) {
                val bitmap = ImageUtils.base64ToBitmap(owner.profilePicture)
                binding.ivOwnerProfile.setImageBitmap(bitmap)
            }
            
            binding.btnOptions.setOnClickListener {
                // Show a popup menu or just trigger edit for now
                onEditClick(owner)
            }
            
            binding.root.setOnLongClickListener {
                onDeleteClick(owner)
                true
            }
        }
    }

    class OwnerDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.userId == newItem.userId
        override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
    }
}
