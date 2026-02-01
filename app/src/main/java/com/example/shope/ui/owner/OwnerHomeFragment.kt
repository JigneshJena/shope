package com.example.shope.ui.owner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shope.databinding.FragmentOwnerHomeBinding

import androidx.fragment.app.viewModels
import com.example.shope.viewmodel.OwnerViewModel
import java.text.NumberFormat
import java.util.Locale

class OwnerHomeFragment : Fragment() {
    
    private var _binding: FragmentOwnerHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OwnerViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOwnerHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()
        viewModel.loadDashboardStats()
    }
    
    private fun setupObservers() {
        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            binding.tvTotalEmployees.text = stats.totalEmployees.toString()
            binding.tvActiveEmployees.text = stats.activeEmployees.toString()
            binding.tvTotalSchools.text = stats.totalSchools.toString()
            binding.tvLowStockCount.text = stats.lowStockCount.toString()
            
            val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
            binding.tvTodaysSales.text = currencyFormatter.format(stats.todaysSales)
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // TODO: Show/hide loading progress
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
