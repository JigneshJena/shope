package com.example.shope.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shope.data.models.Inventory
import com.example.shope.data.repository.InventoryRepository
import com.example.shope.data.repository.OrderRepository
import kotlinx.coroutines.launch

class CustomerViewModel : ViewModel() {
    private val inventoryRepo = InventoryRepository()
    private val orderRepo = OrderRepository()

    private val _products = MutableLiveData<List<Inventory>>()
    val products: LiveData<List<Inventory>> = _products

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = inventoryRepo.getAllInventoryItems()
            _products.value = result.getOrDefault(emptyList())
            _isLoading.value = false
        }
    }
}
