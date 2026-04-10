package com.example.shope.ui.customer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.shope.R
import com.example.shope.data.models.Measurement
import com.example.shope.databinding.FragmentCustomerProfileBinding
import com.example.shope.ui.auth.LoginActivity
import com.example.shope.utils.Constants
import com.example.shope.utils.PreferenceManager
import com.example.shope.viewmodel.CustomerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

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
        
        // Load measurement data
        val customerId = prefManager.getUserId()
        if (!customerId.isNullOrEmpty()) {
            viewModel.loadMeasurement(customerId)
        }
        
        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        binding.btnMyMeasurements.setOnClickListener {
            showMeasurementDialog()
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
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etName)
        val etPhone = dialogView.findViewById<TextInputEditText>(R.id.etPhone)
        val etAddress = dialogView.findViewById<TextInputEditText>(R.id.etAddress)
        
        etName.setText(prefManager.getUserName())
        etPhone.setText(prefManager.getUserPhone())

        // Load address from Firestore
        val userId = prefManager.getUserId()
        if (!userId.isNullOrEmpty()) {
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection(Constants.COLLECTION_USERS).document(userId)
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

    /**
     * Show measurement dialog - allows customer to view and edit their measurements
     */
    private fun showMeasurementDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_measurement, null)

        // Get all the input fields
        val actvType = dialogView.findViewById<AutoCompleteTextView>(R.id.actvMeasurementType)
        val etChest = dialogView.findViewById<TextInputEditText>(R.id.etChest)
        val etShoulder = dialogView.findViewById<TextInputEditText>(R.id.etShoulder)
        val etSleeveLength = dialogView.findViewById<TextInputEditText>(R.id.etSleeveLength)
        val etShirtLength = dialogView.findViewById<TextInputEditText>(R.id.etShirtLength)
        val etNeck = dialogView.findViewById<TextInputEditText>(R.id.etNeck)
        val etWaist = dialogView.findViewById<TextInputEditText>(R.id.etWaist)
        val etHip = dialogView.findViewById<TextInputEditText>(R.id.etHip)
        val etInseam = dialogView.findViewById<TextInputEditText>(R.id.etInseam)
        val etNotes = dialogView.findViewById<TextInputEditText>(R.id.etNotes)

        // Setup measurement type dropdown
        val measurementTypes = arrayOf(
            Constants.MEASUREMENT_SHIRT,
            Constants.MEASUREMENT_PANT,
            Constants.MEASUREMENT_SUIT,
            Constants.MEASUREMENT_DRESS,
            Constants.MEASUREMENT_BLOUSE,
            Constants.MEASUREMENT_SCHOOL_UNIFORM
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, measurementTypes)
        actvType.setAdapter(adapter)

        // Pre-fill with existing measurement data if available
        val existingMeasurement = viewModel.measurement.value
        if (existingMeasurement != null) {
            actvType.setText(existingMeasurement.measurementType, false)
            etNotes?.setText(existingMeasurement.notes)

            // Fill from the measurements map
            val m = existingMeasurement.measurements
            etChest?.setText(m["chest"] ?: "")
            etShoulder?.setText(m["shoulder"] ?: "")
            etSleeveLength?.setText(m["sleeveLength"] ?: "")
            etShirtLength?.setText(m["shirtLength"] ?: "")
            etNeck?.setText(m["neck"] ?: "")
            etWaist?.setText(m["waist"] ?: "")
            etHip?.setText(m["hip"] ?: "")
            etInseam?.setText(m["inseam"] ?: "")
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("My Measurements")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val type = actvType.text.toString().trim()
                val notes = etNotes?.text.toString().trim()

                // Build measurements map
                val measurementsMap = mutableMapOf<String, String>()
                etChest?.text.toString().trim().let { if (it.isNotEmpty()) measurementsMap["chest"] = it }
                etShoulder?.text.toString().trim().let { if (it.isNotEmpty()) measurementsMap["shoulder"] = it }
                etSleeveLength?.text.toString().trim().let { if (it.isNotEmpty()) measurementsMap["sleeveLength"] = it }
                etShirtLength?.text.toString().trim().let { if (it.isNotEmpty()) measurementsMap["shirtLength"] = it }
                etNeck?.text.toString().trim().let { if (it.isNotEmpty()) measurementsMap["neck"] = it }
                etWaist?.text.toString().trim().let { if (it.isNotEmpty()) measurementsMap["waist"] = it }
                etHip?.text.toString().trim().let { if (it.isNotEmpty()) measurementsMap["hip"] = it }
                etInseam?.text.toString().trim().let { if (it.isNotEmpty()) measurementsMap["inseam"] = it }

                if (measurementsMap.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter at least one measurement", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val customerId = prefManager.getUserId() ?: ""
                val customerName = prefManager.getUserName() ?: ""

                // Create or update measurement
                val measurement = existingMeasurement?.copy(
                    measurementType = type.ifEmpty { Constants.MEASUREMENT_SHIRT },
                    measurements = measurementsMap,
                    notes = notes
                ) ?: Measurement(
                    measurementId = "",
                    customerId = customerId,
                    customerName = customerName,
                    measurementType = type.ifEmpty { Constants.MEASUREMENT_SHIRT },
                    measurements = measurementsMap,
                    notes = notes
                )

                viewModel.saveMeasurement(measurement)
                Toast.makeText(requireContext(), "Measurements saved!", Toast.LENGTH_SHORT).show()
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
