package com.example.shope.ui.customer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.shope.databinding.FragmentCustomerProfileBinding
import com.example.shope.ui.auth.LoginActivity
import com.example.shope.utils.PreferenceManager
import com.example.shope.viewmodel.CustomerViewModel

class CustomerProfileFragment : Fragment() {
    
    private var _binding: FragmentCustomerProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PreferenceManager
    private val viewModel: CustomerViewModel by activityViewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PreferenceManager(requireContext())
        
        setupUserData()
        
        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        binding.btnLogout.setOnClickListener {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
            prefManager.clear()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun showEditProfileDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(com.example.shope.R.layout.dialog_edit_profile, null)
        val etName = dialogView.findViewById<android.widget.EditText>(com.example.shope.R.id.etName)
        val etPhone = dialogView.findViewById<android.widget.EditText>(com.example.shope.R.id.etPhone)
        val etAddress = dialogView.findViewById<android.widget.EditText>(com.example.shope.R.id.etAddress)
        
        etName.setText(prefManager.getUserName())
        etPhone.setText(prefManager.getUserPhone())
        
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                val address = etAddress.text.toString().trim()
                
                if (name.isNotEmpty()) {
                    viewModel.updateUserProfile(prefManager.getUserId() ?: "", name, phone, address, prefManager)
                    // Refresh UI after a small delay to allow save
                    binding.root.postDelayed({ setupUserData() }, 1000)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun setupUserData() {
        binding.tvCustomerName.text = prefManager.getUserName()
        binding.tvCustomerEmail.text = prefManager.getUserEmail()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
