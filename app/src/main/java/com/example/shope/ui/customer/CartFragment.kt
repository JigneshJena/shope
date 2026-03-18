package com.example.shope.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shope.R
import com.example.shope.databinding.FragmentCartBinding
import com.example.shope.ui.adapter.CartAdapter
import com.example.shope.viewmodel.CustomerViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.Locale

class CartFragment : Fragment() {
    
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CustomerViewModel by activityViewModels()
    private lateinit var adapter: CartAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = CartAdapter { itemId ->
            viewModel.removeFromCart(itemId)
            Snackbar.make(binding.root, "Item removed", Snackbar.LENGTH_SHORT).show()
        }
        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CartFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            updateTotal(items)
            
            val isEmpty = items.isEmpty()
            binding.layoutEmptyCart.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.cvCheckout.visibility = if (isEmpty) View.GONE else View.VISIBLE
            binding.rvCartItems.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }
    }

    private fun setupListeners() {
        binding.btnCheckout.setOnClickListener {
            if (viewModel.cartItems.value.isNullOrEmpty()) {
                Snackbar.make(binding.root, "Cart is empty", Snackbar.LENGTH_SHORT).show()
            } else {
                // Navigate to billing/payment
                findNavController().navigate(R.id.action_navigation_cart_to_navigation_billing)
            }
        }
    }

    private fun updateTotal(items: List<com.example.shope.data.models.OrderItem>) {
        val total = items.sumOf { it.subtotal }
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
        binding.tvCartTotal.text = currencyFormatter.format(total)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
