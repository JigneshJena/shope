package com.example.shope.ui.employee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shope.databinding.FragmentInventoryManagementBinding

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shope.data.models.Inventory
import com.example.shope.databinding.DialogAddInventoryBinding
import com.example.shope.ui.adapter.InventoryAdapter
import com.example.shope.viewmodel.EmployeeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import androidx.activity.result.contract.ActivityResultContracts
import java.io.ByteArrayOutputStream

class InventoryManagementFragment : Fragment() {
    
    private var _binding: FragmentInventoryManagementBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmployeeViewModel by viewModels()
    private lateinit var adapter: InventoryAdapter
    
    private var selectedImageBase64: String = ""
    private var dialogBinding: DialogAddInventoryBinding? = null
    
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                selectedImageBase64 = encodeImageToBase64(bitmap)
                dialogBinding?.ivItemImage?.setImageBitmap(bitmap)
            }
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryManagementBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        setupListeners()
        
        viewModel.loadInventory()
    }
    
    private fun setupRecyclerView() {
        adapter = InventoryAdapter { item ->
            // TODO: Edit item
        }
        binding.rvInventory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInventory.adapter = adapter
    }
    
    private fun setupObservers() {
        viewModel.inventory.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // TODO: Show/hide loading
        }
    }
    
    private fun setupListeners() {
        binding.fabAddItem.setOnClickListener {
            showAddItemDialog()
        }
    }
    
    private fun showAddItemDialog() {
        dialogBinding = DialogAddInventoryBinding.inflate(layoutInflater)
        selectedImageBase64 = "" // Reset
        
        dialogBinding?.ivItemImage?.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Stock Item")
            .setView(dialogBinding?.root)
            .setPositiveButton("Add") { _, _ ->
                try {
                    val name = dialogBinding?.etItemName?.text.toString()
                    val category = dialogBinding?.etCategory?.text.toString()
                    val size = dialogBinding?.etSize?.text.toString()
                    val color = dialogBinding?.etColor?.text.toString()
                    val fabric = dialogBinding?.etFabric?.text.toString()
                    val pPrice = dialogBinding?.etPurchasePrice?.text.toString().toDoubleOrNull() ?: 0.0
                    val sPrice = dialogBinding?.etSellingPrice?.text.toString().toDoubleOrNull() ?: 0.0
                    val qty = dialogBinding?.etQuantity?.text.toString().toIntOrNull() ?: 0
                    val reorder = dialogBinding?.etReorderLevel?.text.toString().toIntOrNull() ?: 5
                    
                    if (!name.isNullOrEmpty()) {
                        val item = Inventory(
                            itemName = name,
                            category = category,
                            size = size,
                            color = color,
                            fabric = fabric,
                            purchasePrice = pPrice,
                            sellingPrice = sPrice,
                            quantity = qty,
                            minimumStockLevel = reorder,
                            itemImage = selectedImageBase64,
                            status = "active"
                        )
                        viewModel.addInventoryItem(item)
                    }
                } catch (e: Exception) {
                    // Handle input error
                }
                dialogBinding = null
            }
            .setNegativeButton("Cancel") { _, _ ->
                dialogBinding = null
            }
            .setOnDismissListener {
                dialogBinding = null
            }
            .show()
    }
    
    private fun encodeImageToBase64(bitmap: Bitmap): String {
        // Resize bitmap to avoid Firestore limit (max 1MB for doc)
        val maxDimension = 800
        val ratio = Math.min(
            maxDimension.toDouble() / bitmap.width,
            maxDimension.toDouble() / bitmap.height
        )
        val width = (bitmap.width * ratio).toInt()
        val height = (bitmap.height * ratio).toInt()
        
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
        
        val byteArrayOutputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream) // 60% quality
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
