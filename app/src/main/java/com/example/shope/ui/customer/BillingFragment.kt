package com.example.shope.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.shope.R
import com.example.shope.databinding.FragmentBillingBinding
import com.example.shope.viewmodel.CustomerViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.Locale

class BillingFragment : Fragment() {

    private var _binding: FragmentBillingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CustomerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBillingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSummary()
        setupListeners()
    }

    private fun setupSummary() {
        val cartItems = viewModel.cartItems.value ?: emptyList()
        val total = cartItems.sumOf { it.subtotal }
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
        
        binding.tvSubtotal.text = currencyFormatter.format(total)
        binding.tvTotal.text = currencyFormatter.format(total)
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnPayNow.setOnClickListener {
            val method = when {
                binding.rbUpi.isChecked -> "UPI"
                binding.rbCard.isChecked -> "Card"
                else -> "Cash"
            }
            
            binding.btnPayNow.isEnabled = false
            binding.btnPayNow.text = "Processing..."
            
            // Logic to create order in Firestore would go here
            // For now, we simulate a successful transaction
            Snackbar.make(binding.root, "Processing payment via $method...", Snackbar.LENGTH_SHORT).show()
            
            // Simulation of network delay
            binding.root.postDelayed({
                viewModel.clearCart()
                findNavController().navigate(R.id.action_navigation_billing_to_orderSuccessFragment)
            }, 1500)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
