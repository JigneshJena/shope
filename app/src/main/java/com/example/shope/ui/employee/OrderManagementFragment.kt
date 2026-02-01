package com.example.shope.ui.employee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shope.databinding.FragmentOrderManagementBinding

import android.app.DatePickerDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shope.data.models.Order
import com.example.shope.databinding.DialogAddOrderBinding
// import com.example.shope.ui.adapter.OrderAdapter // Create this later
import com.example.shope.viewmodel.EmployeeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

class OrderManagementFragment : Fragment() {
    
    private var _binding: FragmentOrderManagementBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmployeeViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderManagementBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // setupRecyclerView()
        setupListeners()
        setupObservers()
        
        viewModel.loadOrders()
    }

    private fun setupListeners() {
        binding.fabAddOrder.setOnClickListener {
            showAddOrderDialog()
        }
    }

    private fun setupObservers() {
        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            // adapter.submitList(orders)
        }
    }
    
    private fun showAddOrderDialog() {
        val dialogBinding = DialogAddOrderBinding.inflate(layoutInflater)
        var selectedDateInMillis: Long = 0
        var foundCustomerId = ""
        var foundCustomerName = ""
        var foundCustomerPhone = ""
        
        // Date Picker
        dialogBinding.etDeliveryDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    selectedDateInMillis = calendar.timeInMillis
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    dialogBinding.etDeliveryDate.setText(sdf.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        
        // Customer Search (simplified for demo)
        dialogBinding.etCustomerSearch.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val query = dialogBinding.etCustomerSearch.text.toString()
                if (query.isNotEmpty()) {
                    lifecycleScope.launch {
                        val customer = viewModel.searchCustomer(query)
                        if (customer != null) {
                            foundCustomerId = customer.customerId
                            foundCustomerName = customer.name
                            foundCustomerPhone = customer.phone
                            dialogBinding.etCustomerSearch.error = null
                        } else {
                            dialogBinding.etCustomerSearch.error = "Customer not found"
                        }
                    }
                }
            }
        }
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("New Order")
            .setView(dialogBinding.root)
            .setPositiveButton("Create") { _, _ ->
                val description = dialogBinding.etOrderDescription.text.toString()
                val totalAmount = dialogBinding.etTotalAmount.text.toString().toDoubleOrNull() ?: 0.0
                val advanceAmount = dialogBinding.etAdvanceAmount.text.toString().toDoubleOrNull() ?: 0.0
                
                // Allow creation even if customer not found (manually entered) - fallback
                if (foundCustomerId.isEmpty()) {
                     foundCustomerName = dialogBinding.etCustomerSearch.text.toString()
                }

                if (description.isNotEmpty()) {
                    val order = Order(
                        customerId = foundCustomerId,
                        customerName = foundCustomerName,
                        customerPhone = foundCustomerPhone,
                        orderType = "Store Order",
                        specialInstructions = description,
                        totalAmount = totalAmount,
                        advancePaid = advanceAmount,
                        deliveryDate = selectedDateInMillis,
                        status = "Pending"
                    )
                    viewModel.addOrder(order)
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
