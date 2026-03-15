package com.example.shope.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shope.databinding.FragmentAdminEmployeesBinding
import com.example.shope.viewmodel.AdminViewModel

class AdminEmployeesFragment : Fragment() {

    private var _binding: FragmentAdminEmployeesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminEmployeesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Similar setup to Owners but for Employees
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
