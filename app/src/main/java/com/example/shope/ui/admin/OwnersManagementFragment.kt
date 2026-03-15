package com.example.shope.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shope.data.models.User
import com.example.shope.databinding.DialogAddOwnerBinding
import com.example.shope.databinding.FragmentOwnersManagementBinding
import com.example.shope.viewmodel.AdminState
import com.example.shope.viewmodel.AdminViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class OwnersManagementFragment : Fragment() {

    private var _binding: FragmentOwnersManagementBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var adapter: OwnerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOwnersManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = OwnerAdapter(
            onEditClick = { owner -> showEditOwnerDialog(owner) },
            onDeleteClick = { owner -> showDeleteConfirmation(owner) }
        )
        binding.rvOwners.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOwners.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.owners.observe(viewLifecycleOwner) { owners ->
            adapter.submitList(owners)
        }

        viewModel.adminState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AdminState.Loading -> binding.progressBar.visibility = View.VISIBLE
                is AdminState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                is AdminState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabAddOwner.setOnClickListener {
            showAddOwnerDialog()
        }
    }

    private fun showAddOwnerDialog() {
        val dialogBinding = DialogAddOwnerBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val name = dialogBinding.etName.text.toString()
                val email = dialogBinding.etEmail.text.toString()
                val phone = dialogBinding.etPhone.text.toString()
                val password = dialogBinding.etPassword.text.toString()
                
                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    val user = User(name = name, email = email, phone = phone)
                    viewModel.addOwner(user, password)
                } else {
                    Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditOwnerDialog(owner: User) {
        // Implementation for edit dialog
    }

    private fun showDeleteConfirmation(owner: User) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Owner")
            .setMessage("Are you sure you want to delete ${owner.name}? This will remove all their data.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteOwner(owner.userId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
