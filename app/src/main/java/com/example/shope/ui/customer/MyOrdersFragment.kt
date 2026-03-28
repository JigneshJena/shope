package com.example.shope.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.shope.databinding.FragmentMyOrdersBinding
import com.example.shope.ui.adapter.OrderAdapter
import com.example.shope.viewmodel.CustomerViewModel

class MyOrdersFragment : Fragment() {
    
    private var _binding: FragmentMyOrdersBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    private lateinit var adapter: OrderAdapter
    private val viewModel: CustomerViewModel by activityViewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        
        val prefManager = com.example.shope.utils.PreferenceManager(requireContext())
        prefManager.getUserId()?.let { userId ->
            viewModel.loadMyOrders(userId)
        }
    }
    
    private fun setupRecyclerView() {
        adapter = OrderAdapter()
        binding.rvOrders.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            adapter = this@MyOrdersFragment.adapter
        }
    }
    
    private fun setupObservers() {
        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            adapter.submitList(orders)
            binding.layoutEmpty.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
            binding.rvOrders.visibility = if (orders.isEmpty()) View.GONE else View.VISIBLE
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Show/hide progress if needed
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
