package com.example.shope.ui.owner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shope.R
import com.example.shope.data.models.Employee
import com.example.shope.databinding.DialogAddEmployeeBinding
import com.example.shope.databinding.DialogEditEmployeeBinding
import com.example.shope.databinding.DialogChangePasswordBinding
import com.example.shope.databinding.FragmentEmployeeManagementBinding
import com.example.shope.ui.adapter.EmployeeAdapter
import com.example.shope.utils.ImageUtils
import com.example.shope.viewmodel.OwnerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EmployeeManagementFragment : Fragment() {
    
    private var _binding: FragmentEmployeeManagementBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OwnerViewModel by activityViewModels()
    private lateinit var adapter: EmployeeAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmployeeManagementBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        
        // Start real-time listener
        viewModel.startEmployeesListener()
    }
    
    private fun setupRecyclerView() {
        adapter = EmployeeAdapter(
            onEmployeeClick = { employee -> showEmployeeDetails(employee) },
            onEditClick = { employee -> showEditEmployeeDialog(employee) },
            onDeleteClick = { employee -> showDeleteConfirmation(employee) },
            onChangePasswordClick = { employee -> showChangePasswordDialog(employee) }
        )
        binding.rvEmployees.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEmployees.adapter = adapter
    }
    
    private fun setupObservers() {
        viewModel.employees.observe(viewLifecycleOwner) { employees ->
            adapter.submitList(employees)
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }

        binding.fabAddEmployee.setOnClickListener {
            showAddEmployeeDialog()
        }
    }

    private fun showAddEmployeeDialog() {
        val dialogBinding = DialogAddEmployeeBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Employee")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val name = dialogBinding.etEmployeeName.text.toString().trim()
                val email = dialogBinding.etEmployeeEmail.text.toString().trim()
                val password = dialogBinding.etEmployeePassword.text.toString().trim()
                val phone = dialogBinding.etEmployeePhone.text.toString().trim()
                
                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    val employee = Employee(
                        name = name,
                        email = email,
                        phone = phone
                    )
                    viewModel.addEmployee(employee, password)
                } else {
                    Toast.makeText(requireContext(), "Fill required fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditEmployeeDialog(employee: Employee) {
        val dialogBinding = DialogEditEmployeeBinding.inflate(layoutInflater)
        
        // Pre-fill data
        dialogBinding.etName.setText(employee.name)
        dialogBinding.etPhone.setText(employee.phone)
        dialogBinding.etRole.setText(employee.role)
        if (employee.status == "active") dialogBinding.rbActive.isChecked = true 
        else dialogBinding.rbInactive.isChecked = true
        
        if (employee.profilePicture.isNotEmpty()) {
            dialogBinding.ivEmployeeProfile.setImageBitmap(ImageUtils.base64ToBitmap(employee.profilePicture))
        }

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                val updatedEmployee = employee.copy(
                    name = dialogBinding.etName.text.toString().trim(),
                    phone = dialogBinding.etPhone.text.toString().trim(),
                    role = dialogBinding.etRole.text.toString().trim(),
                    status = if (dialogBinding.rbActive.isChecked) "active" else "inactive"
                )
                viewModel.updateEmployee(updatedEmployee)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmation(employee: Employee) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Employee")
            .setMessage("Are you sure you want to delete ${employee.name}?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteEmployee(employee.employeeId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showChangePasswordDialog(employee: Employee) {
        val dialogBinding = DialogChangePasswordBinding.inflate(layoutInflater)
        dialogBinding.tvEmployeeEmail.text = employee.email

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Reset Link") { _, _ ->
                // Per instructions: "Use sendPasswordResetEmail() as fallback"
                viewModel.resetEmployeePassword(employee.email)
                Toast.makeText(requireContext(), "Password reset email sent", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEmployeeDetails(employee: Employee) {
        // Implementation for detail screen (could be another fragment or just a simple alert)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Employee Details")
            .setMessage("Name: ${employee.name}\nEmail: ${employee.email}\nPhone: ${employee.phone}\nRole: ${employee.role}")
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
