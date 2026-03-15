package com.example.shope.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shope.databinding.FragmentAdminHomeBinding
import com.example.shope.viewmodel.AdminViewModel

class AdminHomeFragment : Fragment() {

    private var _binding: FragmentAdminHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.owners.observe(viewLifecycleOwner) { owners ->
            binding.tvOwnersCount.text = owners.size.toString()
        }
        
        viewModel.employees.observe(viewLifecycleOwner) { employees ->
            binding.tvEmployeesCount.text = employees.size.toString()
        }
        
        viewModel.customers.observe(viewLifecycleOwner) { customers ->
            binding.tvCustomersCount.text = customers.size.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
