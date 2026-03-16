package com.example.shope.ui.owner

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shope.data.models.UniformItem
import com.example.shope.databinding.DialogAddUniformItemBinding
import com.example.shope.databinding.FragmentSchoolProductsBinding
import com.example.shope.ui.adapter.UniformConfigAdapter
import com.example.shope.utils.ImageUtils
import com.example.shope.viewmodel.OwnerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SchoolProductsFragment : Fragment() {

    private var _binding: FragmentSchoolProductsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OwnerViewModel by viewModels()
    private val schoolId: String? get() = arguments?.getString("schoolId")
    
    private lateinit var itemAdapter: UniformConfigAdapter
    private val products = mutableListOf<UniformItem>()
    
    // Image selection logic
    private var pendingImageUpdate: ((String) -> Unit)? = null
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val base64 = ImageUtils.uriToBase64(requireContext(), it)
            base64?.let { b64 -> pendingImageUpdate?.invoke(b64) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSchoolProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupListeners()
        viewModel.loadSchoolById(schoolId ?: "")
    }

    private fun setupRecyclerView() {
        itemAdapter = UniformConfigAdapter(
            onEdit = { position ->
                showSampleDialog(products[position]) { updatedItem ->
                    products[position] = updatedItem
                    updateProductsInFirestore()
                }
            },
            onRemove = { position ->
                products.removeAt(position)
                updateProductsInFirestore()
            }
        )
        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProducts.adapter = itemAdapter
    }

    private fun setupObservers() {
        viewModel.currentSchool.observe(viewLifecycleOwner) { school ->
            school?.let {
                binding.tvSchoolName.text = it.schoolName
                products.clear()
                products.addAll(it.uniformItems)
                itemAdapter.submitList(products.toList())
                binding.emptyState.visibility = if (products.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        
        binding.btnAddProduct.setOnClickListener {
            showSampleDialog(null) { newItem ->
                products.add(newItem)
                updateProductsInFirestore()
            }
        }
    }

    private fun updateProductsInFirestore() {
        viewModel.updateProductsForSchool(schoolId ?: "", products.toList())
    }

    private fun showSampleDialog(existingItem: UniformItem?, onItemSaved: (UniformItem) -> Unit) {
        val itemBinding = DialogAddUniformItemBinding.inflate(layoutInflater)
        var selectedItemImage = existingItem?.itemImage ?: ""

        if (existingItem != null) {
            itemBinding.etItemName.setText(existingItem.itemName)
            itemBinding.etCategory.setText(existingItem.category)
            itemBinding.etPrice.setText(existingItem.price.toString())
            itemBinding.etQuantity.setText(existingItem.quantity.toString())
            if (existingItem.itemImage.isNotEmpty()) {
                itemBinding.ivItemImage.setImageBitmap(ImageUtils.base64ToBitmap(existingItem.itemImage))
            }
        }

        itemBinding.cvItemImage.setOnClickListener {
            pendingImageUpdate = { base64 ->
                selectedItemImage = base64
                itemBinding.ivItemImage.setImageBitmap(ImageUtils.base64ToBitmap(base64))
            }
            pickImage.launch("image/*")
        }

        val title = if (existingItem == null) "Add Sample Product" else "Edit Sample Product"

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(itemBinding.root)
            .setPositiveButton("Save") { _, _ ->
                val name = itemBinding.etItemName.text.toString()
                val category = itemBinding.etCategory.text.toString()
                val priceStr = itemBinding.etPrice.text.toString()
                val qtyStr = itemBinding.etQuantity.text.toString()
                
                if (name.isNotEmpty()) {
                    val school = viewModel.currentSchool.value
                    onItemSaved(UniformItem(
                        id = existingItem?.id ?: "item_${System.currentTimeMillis()}",
                        itemName = name,
                        category = if (category.isEmpty()) "Regular" else category,
                        itemImage = selectedItemImage,
                        price = priceStr.toDoubleOrNull() ?: 0.0,
                        quantity = qtyStr.toIntOrNull() ?: 0,
                        schoolId = school?.schoolId ?: schoolId ?: "",
                        schoolName = school?.schoolName ?: ""
                    ))
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
