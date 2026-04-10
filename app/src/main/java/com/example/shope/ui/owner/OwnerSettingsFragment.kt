package com.example.shope.ui.owner

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.shope.R
import com.example.shope.databinding.FragmentOwnerSettingsBinding
import com.example.shope.ui.auth.LoginActivity
import com.example.shope.utils.Constants
import com.example.shope.utils.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OwnerSettingsFragment : Fragment() {
    
    private var _binding: FragmentOwnerSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PreferenceManager
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
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
        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        binding.btnShopProfile.setOnClickListener {
            showShopProfileDialog()
        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            prefManager.clear()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    /**
     * Show Edit Profile dialog - allows owner to edit name, phone, and address
     */
    private fun showEditProfileDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_profile, null)

        val etName = dialogView.findViewById<TextInputEditText>(R.id.etName)
        val etPhone = dialogView.findViewById<TextInputEditText>(R.id.etPhone)
        val etAddress = dialogView.findViewById<TextInputEditText>(R.id.etAddress)

        // Pre-fill with current data
        etName?.setText(prefManager.getUserName() ?: "")
        etPhone?.setText(prefManager.getUserPhone() ?: "")

        // Load address from Firestore
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection(Constants.COLLECTION_USERS).document(userId)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        etAddress?.setText(doc.getString("address") ?: "")
                    }
                }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = etName?.text.toString().trim()
                val phone = etPhone?.text.toString().trim()
                val address = etAddress?.text.toString().trim()

                if (name.isEmpty()) {
                    Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                saveProfileToFirestore(name, phone, address)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Save profile data to Firestore and update local preferences
     */
    private fun saveProfileToFirestore(name: String, phone: String, address: String) {
        val userId = auth.currentUser?.uid ?: return

        val updates = hashMapOf<String, Any>(
            "name" to name,
            "phone" to phone,
            "address" to address
        )

        firestore.collection(Constants.COLLECTION_USERS).document(userId)
            .update(updates)
            .addOnSuccessListener {
                // Update local preferences
                prefManager.saveUserName(name)
                prefManager.saveUserPhone(phone)
                
                // Update displayed data
                binding.tvOwnerName.text = name
                
                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Show Shop Profile dialog - allows owner to edit shop details
     */
    private fun showShopProfileDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_shop_profile, null)

        val etShopName = dialogView.findViewById<TextInputEditText>(R.id.etShopName)
        val etShopAddress = dialogView.findViewById<TextInputEditText>(R.id.etShopAddress)
        val etShopPhone = dialogView.findViewById<TextInputEditText>(R.id.etShopPhone)
        val etGstNumber = dialogView.findViewById<TextInputEditText>(R.id.etGstNumber)

        // Load existing shop data from Firestore
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection(Constants.COLLECTION_SHOP_SETTINGS).document(userId)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        etShopName?.setText(doc.getString("shopName") ?: "")
                        etShopAddress?.setText(doc.getString("shopAddress") ?: "")
                        etShopPhone?.setText(doc.getString("shopPhone") ?: "")
                        etGstNumber?.setText(doc.getString("gstNumber") ?: "")
                    }
                }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Shop Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val shopName = etShopName?.text.toString().trim()
                val shopAddress = etShopAddress?.text.toString().trim()
                val shopPhone = etShopPhone?.text.toString().trim()
                val gstNumber = etGstNumber?.text.toString().trim()

                if (shopName.isEmpty()) {
                    Toast.makeText(requireContext(), "Shop name cannot be empty", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                saveShopProfileToFirestore(shopName, shopAddress, shopPhone, gstNumber)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Save shop profile data to Firestore
     */
    private fun saveShopProfileToFirestore(shopName: String, shopAddress: String, shopPhone: String, gstNumber: String) {
        val userId = auth.currentUser?.uid ?: return

        val shopData = hashMapOf<String, Any>(
            "shopName" to shopName,
            "shopAddress" to shopAddress,
            "shopPhone" to shopPhone,
            "gstNumber" to gstNumber,
            "ownerId" to userId
        )

        firestore.collection(Constants.COLLECTION_SHOP_SETTINGS).document(userId)
            .set(shopData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Shop profile updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to update shop profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
