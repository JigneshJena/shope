package com.example.shope.ui.owner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shope.databinding.FragmentSchoolManagementBinding

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shope.data.models.School
import com.example.shope.databinding.DialogAddSchoolBinding
import com.example.shope.ui.adapter.SchoolAdapter
import com.example.shope.viewmodel.OwnerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SchoolManagementFragment : Fragment() {
    
    private var _binding: FragmentSchoolManagementBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OwnerViewModel by viewModels()
    private lateinit var adapter: SchoolAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSchoolManagementBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        setupListeners()
        
        viewModel.loadSchools()
    }
    
    private fun setupRecyclerView() {
        adapter = SchoolAdapter { school ->
            // TODO: Show school details or edit
        }
        binding.rvSchools.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSchools.adapter = adapter
    }
    
    private fun setupObservers() {
        viewModel.schools.observe(viewLifecycleOwner) { schools ->
            adapter.submitList(schools)
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // TODO: Show/hide loading
        }
    }
    
    private fun setupListeners() {
        binding.fabAddSchool.setOnClickListener {
            showAddSchoolDialog()
        }
    }
    
    private fun showAddSchoolDialog() {
        val dialogBinding = DialogAddSchoolBinding.inflate(layoutInflater)
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add School")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val name = dialogBinding.etSchoolName.text.toString()
                val principal = dialogBinding.etPrincipalName.text.toString()
                val contact = dialogBinding.etContactNumber.text.toString()
                val address = dialogBinding.etAddress.text.toString()
                
                if (name.isNotEmpty()) {
                    val school = School(
                        schoolName = name,
                        principalName = principal,
                        contactNumber = contact,
                        address = address
                    )
                    viewModel.addSchool(school)
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
