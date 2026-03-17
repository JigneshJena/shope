package com.example.shope.ui.employee

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shope.R
import com.example.shope.data.models.UniformItem
import com.example.shope.databinding.FragmentInventoryManagementBinding
import com.example.shope.ui.adapter.EmployeeStockAdapter
import com.example.shope.viewmodel.EmployeeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class InventoryManagementFragment : Fragment() {
    
    private var _binding: FragmentInventoryManagementBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmployeeViewModel by viewModels()
    private lateinit var adapter: EmployeeStockAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryManagementBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        setupListeners()
        
        viewModel.loadStockItems()
    }
    
    private fun setupRecyclerView() {
        adapter = EmployeeStockAdapter { item ->
            showUpdateQuantityDialog(item)
        }
        binding.rvInventory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInventory.adapter = adapter
    }
    
    private fun setupObservers() {
        viewModel.stockItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // In a real app, show/hide a progress bar
        }

        viewModel.message.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrEmpty()) {
                android.widget.Toast.makeText(requireContext(), msg, android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun setupListeners() {
        // Hide FAB as employees shouldn't add new stock (as per user request)
        binding.fabAddItem.visibility = View.GONE
        
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterItems(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun filterItems(query: String) {
        val fullList = viewModel.stockItems.value ?: emptyList()
        if (query.isEmpty()) {
            adapter.submitList(fullList)
        } else {
            val filtered = fullList.filter { 
                it.itemName.contains(query, ignoreCase = true) || 
                it.schoolName.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
            }
            adapter.submitList(filtered)
        }
    }
    
    private fun showUpdateQuantityDialog(item: com.example.shope.data.models.UniformItem) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(com.example.shope.R.layout.dialog_update_quantity, null)
        val etQuantity = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(com.example.shope.R.id.etQuantity)
        val tvItemInfo = dialogView.findViewById<android.widget.TextView>(com.example.shope.R.id.tvItemInfo)
        
        tvItemInfo.text = "${item.itemName} (${item.schoolName})"
        etQuantity.setText(item.quantity.toString())
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Update Stock Quantity")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val newQty = etQuantity.text.toString().toIntOrNull()
                if (newQty != null) {
                    viewModel.updateStockQuantity(item, newQty)
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
