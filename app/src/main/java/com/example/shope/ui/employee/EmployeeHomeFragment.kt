package com.example.shope.ui.employee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shope.databinding.FragmentEmployeeHomeBinding

import androidx.fragment.app.viewModels
import com.example.shope.viewmodel.EmployeeViewModel

class EmployeeHomeFragment : Fragment() {
    
    private var _binding: FragmentEmployeeHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmployeeViewModel by viewModels()
    
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
        
        setupObservers()
        viewModel.loadDashboardStats()
    }
    
    private fun setupObservers() {
        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            binding.tvPendingCount.text = stats.pendingOrdersCount.toString()
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Handle loading
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
