package com.example.shope.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shope.data.models.Employee
import com.example.shope.databinding.ItemEmployeeBinding
import com.example.shope.utils.Constants

class EmployeeAdapter(private val onEmployeeClick: (Employee) -> Unit) :
    ListAdapter<Employee, EmployeeAdapter.EmployeeViewHolder>(EmployeeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding = ItemEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EmployeeViewHolder(private val binding: ItemEmployeeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(employee: Employee) {
            binding.tvEmployeeName.text = employee.name
            binding.tvEmployeeRole.text = employee.role
            binding.tvEmployeeStatus.text = employee.status
            
            val statusColor = if (employee.status == Constants.STATUS_ACTIVE) {
                itemView.context.getColor(com.example.shope.R.color.success)
            } else {
                itemView.context.getColor(com.example.shope.R.color.error)
            }
            binding.tvEmployeeStatus.setTextColor(statusColor)
            
            itemView.setOnClickListener { onEmployeeClick(employee) }
        }
    }

    class EmployeeDiffCallback : DiffUtil.ItemCallback<Employee>() {
        override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem.employeeId == newItem.employeeId
        }

        override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem == newItem
        }
    }
}
