package com.example.shope.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shope.databinding.FragmentShopBinding

import androidx.fragment.app.viewModels
import com.example.shope.viewmodel.CustomerViewModel

import androidx.recyclerview.widget.GridLayoutManager
import com.example.shope.ui.adapter.InventoryAdapter

class ShopFragment : Fragment() {
    
    private var _binding: FragmentShopBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CustomerViewModel by viewModels()
    private lateinit var adapter: InventoryAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        viewModel.loadProducts()
    }
    
    private fun setupRecyclerView() {
        adapter = InventoryAdapter { item ->
            // TODO: Handle item click (e.g. view details, add to cart)
        }
        binding.rvShop.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvShop.adapter = adapter
    }
    
    private fun setupObservers() {
        viewModel.products.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Show/Hide loading indicator
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
