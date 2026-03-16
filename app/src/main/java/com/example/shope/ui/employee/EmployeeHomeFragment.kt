package com.example.shope.ui.employee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shope.databinding.FragmentEmployeeHomeBinding

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shope.R
import com.example.shope.data.models.UniformItem
import com.example.shope.ui.adapter.CustomerAdapter
import com.example.shope.ui.adapter.UniformConfigAdapter
import com.example.shope.viewmodel.EmployeeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class EmployeeHomeFragment : Fragment() {
    
    private var _binding: FragmentEmployeeHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmployeeViewModel by viewModels()
    private lateinit var stockAdapter: UniformConfigAdapter
    private lateinit var customerAdapter: CustomerAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmployeeHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerViews()
        setupObservers()
        
        viewModel.loadDashboardStats()
        viewModel.loadStockItems()
        viewModel.loadCustomers()
    }
    
    private fun setupRecyclerViews() {
        stockAdapter = UniformConfigAdapter(
            onEdit = { position -> showUpdateStockDialog(stockAdapter.currentList[position]) },
            onRemove = { position -> 
                val item = stockAdapter.currentList[position]
                viewModel.updateStockQuantity(item, 0)
            }
        )
        binding.rvStockItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = stockAdapter
        }

        customerAdapter = CustomerAdapter()
        binding.rvCustomers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = customerAdapter
        }
    }
    
    private fun setupObservers() {
        viewModel.stockItems.observe(viewLifecycleOwner) { items ->
            stockAdapter.submitList(items)
        }

        viewModel.customers.observe(viewLifecycleOwner) { customers ->
            customerAdapter.submitList(customers)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.message.observe(viewLifecycleOwner) { msg ->
            com.google.android.material.snackbar.Snackbar.make(binding.root, msg, com.google.android.material.snackbar.Snackbar.LENGTH_LONG).show()
        }
    }

    private fun showUpdateStockDialog(item: UniformItem) {
        val input = TextInputEditText(requireContext())
        input.setText(item.quantity.toString())
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Update Stock: ${item.itemName}")
            .setMessage("Current Quantity: ${item.quantity}")
            .setView(input)
            .setPositiveButton("Update") { _, _ ->
                val newQty = input.text.toString().toIntOrNull() ?: item.quantity
                viewModel.updateStockQuantity(item, newQty)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
