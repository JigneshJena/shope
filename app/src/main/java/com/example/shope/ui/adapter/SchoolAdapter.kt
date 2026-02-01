package com.example.shope.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shope.data.models.School
import com.example.shope.databinding.ItemSchoolBinding

class SchoolAdapter(private val onSchoolClick: (School) -> Unit) :
    ListAdapter<School, SchoolAdapter.SchoolViewHolder>(SchoolDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolViewHolder {
        val binding = ItemSchoolBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SchoolViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SchoolViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SchoolViewHolder(private val binding: ItemSchoolBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onSchoolClick(getItem(adapterPosition))
                }
            }
        }

        fun bind(school: School) {
            binding.tvSchoolName.text = school.schoolName
            binding.tvLocation.text = school.address
            // binding.tvSchoolOrders.text = "0 Orders" // TODO: Implement order count
        }
    }

    class SchoolDiffCallback : DiffUtil.ItemCallback<School>() {
        override fun areItemsTheSame(oldItem: School, newItem: School): Boolean {
            return oldItem.schoolId == newItem.schoolId
        }

        override fun areContentsTheSame(oldItem: School, newItem: School): Boolean {
            return oldItem == newItem
        }
    }
}
