package com.example.shope.ui.employee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shope.databinding.FragmentCustomerManagementBinding

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shope.data.models.Customer
import com.example.shope.databinding.DialogAddCustomerBinding
// import com.example.shope.ui.adapter.CustomerAdapter // Create this later or use placeholder
import com.example.shope.viewmodel.EmployeeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CustomerManagementFragment : Fragment() {
    
    private var _binding: FragmentCustomerManagementBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmployeeViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerManagementBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // setupRecyclerView()
        setupListeners()
        setupObservers()
        
        viewModel.loadCustomers()
    }
    
    private fun setupListeners() {
        binding.fabAddCustomer.setOnClickListener {
            showAddCustomerDialog()
        }
    }
    
    private fun setupObservers() {
        viewModel.customers.observe(viewLifecycleOwner) { customers ->
            // adapter.submitList(customers)
        }
    }

    private fun showAddCustomerDialog() {
        val dialogBinding = DialogAddCustomerBinding.inflate(layoutInflater)
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Customer")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val name = dialogBinding.etCustomerName.text.toString()
                val phone = dialogBinding.etCustomerPhone.text.toString()
                val email = dialogBinding.etCustomerEmail.text.toString()
                val address = dialogBinding.etCustomerAddress.text.toString()
                
                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    val customer = Customer(
                        name = name,
                        phone = phone,
                        email = email,
                        address = address,
                        status = "active"
                    )
                    viewModel.addCustomer(customer)
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
