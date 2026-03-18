package com.example.shope.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.shope.R
import com.example.shope.databinding.FragmentOrderSuccessBinding

class OrderSuccessFragment : Fragment() {

    private var _binding: FragmentOrderSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.navigation_shop)
        }

        binding.btnViewOrders.setOnClickListener {
            findNavController().navigate(R.id.navigation_my_orders)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
