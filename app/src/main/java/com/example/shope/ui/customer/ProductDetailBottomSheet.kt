package com.example.shope.ui.customer

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shope.R
import com.example.shope.data.models.Inventory
import com.example.shope.databinding.BottomSheetProductDetailBinding
import com.example.shope.viewmodel.CustomerViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.fragment.app.viewModels
import java.text.NumberFormat
import java.util.Locale

class ProductDetailBottomSheet(
    private val product: Inventory,
    private val onAddToCart: (Inventory, Int) -> Unit,
    private val onBuyNow: (Inventory, Int) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetProductDetailBinding? = null
    private val binding get() = _binding!!
    private var quantity = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.tvProductName.text = product.itemName
        binding.tvProductCategory.text = product.category
        
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
        binding.tvProductPrice.text = currencyFormatter.format(product.sellingPrice)

        if (product.itemImage.isNotEmpty()) {
            try {
                val decodedString = Base64.decode(product.itemImage, Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                binding.ivProduct.setImageBitmap(decodedByte)
            } catch (e: Exception) {
                binding.ivProduct.setImageResource(R.drawable.ic_placeholder)
            }
        } else {
            binding.ivProduct.setImageResource(R.drawable.ic_placeholder)
        }

        binding.btnPlus.setOnClickListener {
            quantity++
            updateQuantityText()
        }

        binding.btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateQuantityText()
            }
        }

        binding.btnAddToCart.setOnClickListener {
            onAddToCart(product, quantity)
            dismiss()
        }

        binding.btnBuyNow.setOnClickListener {
            onBuyNow(product, quantity)
            dismiss()
        }
    }

    private fun updateQuantityText() {
        binding.tvQuantity.text = quantity.toString()
        val total = product.sellingPrice * quantity
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
        binding.tvProductPrice.text = currencyFormatter.format(total)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ProductDetailBottomSheet"
    }
}
