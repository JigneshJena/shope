package com.example.shope.ui.owner

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shope.databinding.FragmentOwnerSettingsBinding
import com.example.shope.ui.auth.LoginActivity
import com.example.shope.utils.PreferenceManager

class OwnerSettingsFragment : Fragment() {
    
    private var _binding: FragmentOwnerSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PreferenceManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOwnerSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PreferenceManager(requireContext())
        
        setupUserData()
        setupClickListeners()
    }
    
    private fun setupUserData() {
        binding.tvOwnerName.text = prefManager.getUserName()
        binding.tvOwnerEmail.text = prefManager.getUserEmail()
    }
    
    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
            prefManager.clear()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
