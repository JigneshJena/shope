package com.example.shope.ui.customer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shope.databinding.FragmentCustomerProfileBinding
import com.example.shope.ui.auth.LoginActivity
import com.example.shope.utils.PreferenceManager

class CustomerProfileFragment : Fragment() {
    
    private var _binding: FragmentCustomerProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PreferenceManager
    
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
        
        binding.btnLogout.setOnClickListener {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
            prefManager.clear()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
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
