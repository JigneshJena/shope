package com.example.shope.ui.owner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shope.databinding.FragmentEmployeeManagementBinding

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shope.data.models.Employee
import com.example.shope.databinding.DialogAddEmployeeBinding
import com.example.shope.ui.adapter.EmployeeAdapter
import com.example.shope.viewmodel.OwnerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EmployeeManagementFragment : Fragment() {
    
    private var _binding: FragmentEmployeeManagementBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OwnerViewModel by viewModels()
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
        
        // Initial load
        viewModel.loadEmployees()
    }
    
    private fun setupRecyclerView() {
        adapter = EmployeeAdapter { employee ->
            // Handle employee click
        }
        binding.rvEmployees.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEmployees.adapter = adapter
    }
    
    private fun setupObservers() {
        viewModel.employees.observe(viewLifecycleOwner) { employees ->
            adapter.submitList(employees)
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Handle loading
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
                val name = dialogBinding.etEmployeeName.text.toString()
                val email = dialogBinding.etEmployeeEmail.text.toString()
                val password = dialogBinding.etEmployeePassword.text.toString()
                val phone = dialogBinding.etEmployeePhone.text.toString()
                
                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    val employee = Employee(
                        name = name,
                        email = email,
                        phone = phone,
                        role = "employee",
                        status = "active"
                    )
                    viewModel.addEmployee(employee, password)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
