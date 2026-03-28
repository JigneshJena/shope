package com.example.shope.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shope.databinding.FragmentShopBinding

import androidx.fragment.app.activityViewModels
import com.example.shope.viewmodel.CustomerViewModel
import com.google.android.material.snackbar.Snackbar

import androidx.recyclerview.widget.GridLayoutManager
import com.example.shope.ui.adapter.InventoryAdapter
import androidx.navigation.fragment.findNavController
import com.example.shope.R

class ShopFragment : Fragment() {
    
    private var _binding: FragmentShopBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CustomerViewModel by activityViewModels()
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
        setupListeners()
        viewModel.loadProducts()
    }
    
    private fun setupListeners() {
        binding.btnCart.setOnClickListener {
            findNavController().navigate(R.id.navigation_cart)
        }
    }
    
    private fun setupRecyclerView() {
        adapter = InventoryAdapter(true) { item ->
            val bottomSheet = ProductDetailBottomSheet(
                item,
                onAddToCart = { product, quantity ->
                    viewModel.addToCart(product, quantity)
                    val bottomNav = requireActivity().findViewById<View>(R.id.bottom_navigation)
                    Snackbar.make(binding.root, "${product.itemName} added to cart", Snackbar.LENGTH_SHORT)
                        .setAnchorView(bottomNav)
                        .setAction("VIEW") {
                            findNavController().navigate(R.id.navigation_cart)
                        }
                        .show()
                },
                onBuyNow = { product, quantity ->
                    viewModel.addToCart(product, quantity)
                    findNavController().navigate(R.id.navigation_cart)
                }
            )
            bottomSheet.show(childFragmentManager, ProductDetailBottomSheet.TAG)
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
