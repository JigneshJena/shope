package com.example.shope.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shope.R
import com.example.shope.data.models.Employee
import com.example.shope.databinding.ItemEmployeeBinding
import com.example.shope.utils.Constants
import com.example.shope.utils.ImageUtils

class EmployeeAdapter(
    private val onEmployeeClick: (Employee) -> Unit,
    private val onEditClick: (Employee) -> Unit,
    private val onDeleteClick: (Employee) -> Unit,
    private val onChangePasswordClick: (Employee) -> Unit
) : ListAdapter<Employee, EmployeeAdapter.EmployeeViewHolder>(EmployeeDiffCallback()) {

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
            binding.tvEmployeePhone.text = employee.phone
            
            if (employee.profilePicture.isNotEmpty()) {
                val bitmap = ImageUtils.base64ToBitmap(employee.profilePicture)
                binding.ivEmployeeProfile.setImageBitmap(bitmap)
            } else {
                binding.ivEmployeeProfile.setImageResource(R.drawable.ic_avatar)
            }
            
            val (statusColor, badgeBg) = if (employee.status == Constants.STATUS_ACTIVE) {
                itemView.context.getColor(R.color.success) to R.drawable.bg_badge_success
            } else {
                itemView.context.getColor(R.color.error) to R.drawable.bg_badge_error
            }
            binding.tvEmployeeStatus.setTextColor(statusColor)
            binding.tvEmployeeStatus.setBackgroundResource(badgeBg)
            
            binding.btnOptions.setOnClickListener { view ->
                val popup = PopupMenu(itemView.context, view)
                popup.menu.add("Edit")
                popup.menu.add("Delete")
                popup.menu.add("Change Password")
                
                popup.setOnMenuItemClickListener { item ->
                    when (item.title) {
                        "Edit" -> onEditClick(employee)
                        "Delete" -> onDeleteClick(employee)
                        "Change Password" -> onChangePasswordClick(employee)
                    }
                    true
                }
                popup.show()
            }
            
            itemView.setOnClickListener { onEmployeeClick(employee) }
        }
    }

    class EmployeeDiffCallback : DiffUtil.ItemCallback<Employee>() {
        override fun areItemsTheSame(oldItem: Employee, newItem: Employee) = oldItem.employeeId == newItem.employeeId
        override fun areContentsTheSame(oldItem: Employee, newItem: Employee) = oldItem == newItem
    }
}
