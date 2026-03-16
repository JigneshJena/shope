package com.example.shope.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shope.data.models.Inventory
import com.example.shope.data.repository.InventoryRepository
import com.example.shope.data.repository.OrderRepository
import kotlinx.coroutines.launch
import android.util.Log

class CustomerViewModel : ViewModel() {
    private val inventoryRepo = InventoryRepository()
    private val schoolRepo = com.example.shope.data.repository.SchoolRepository()
    private val orderRepo = OrderRepository()

    private val _products = MutableLiveData<List<Inventory>>()
    val products: LiveData<List<Inventory>> = _products

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _cartItems = MutableLiveData<List<com.example.shope.data.models.OrderItem>>(emptyList())
    val cartItems: LiveData<List<com.example.shope.data.models.OrderItem>> = _cartItems

    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("CustomerViewModel", "loadProducts: Starting")
            
            // 1. Load regular inventory
            val inventoryResult = inventoryRepo.getAllInventoryItems()
            val inventoryList = inventoryResult.getOrDefault(emptyList()).toMutableList()
            Log.d("CustomerViewModel", "loadProducts: Loaded ${inventoryList.size} regular items")
            if (inventoryResult.isFailure) {
                Log.e("CustomerViewModel", "Inventory load failed: ${inventoryResult.exceptionOrNull()?.message}")
            }
            
            // 2. Load school uniforms
            val schoolResult = schoolRepo.getAllSchools()
            if (schoolResult.isSuccess) {
                val schools = schoolResult.getOrDefault(emptyList())
                Log.d("CustomerViewModel", "loadProducts: Found ${schools.size} schools")
                for (school in schools) {
                    val items = school.uniformItems
                    Log.d("CustomerViewModel", "School ${school.schoolName} has ${items.size} items")
                    for (item in items) {
                        Log.d("CustomerViewModel", "Mapping item: ${item.itemName} for ${school.schoolName}")
                        // Map UniformItem to Inventory for display in the same adapter
                        val inventoryItem = Inventory(
                            itemId = if (item.id.isEmpty()) java.util.UUID.randomUUID().toString() else item.id,
                            itemName = "${school.schoolName} - ${item.itemName}",
                            category = item.category,
                            sellingPrice = item.price,
                            quantity = item.quantity,
                            itemImage = item.itemImage,
                            status = item.status,
                            description = "School: ${school.schoolName}\nDetails: ${school.uniformDetails}"
                        )
                        inventoryList.add(inventoryItem)
                    }
                }
            } else {
                Log.e("CustomerViewModel", "Schools load failed: ${schoolResult.exceptionOrNull()?.message}")
            }
            
            Log.d("CustomerViewModel", "loadProducts: Total products to display: ${inventoryList.size}")
            _products.value = inventoryList
            _isLoading.value = false
        }
    }

    fun addToCart(inventory: Inventory, qty: Int) {
        val currentItems = _cartItems.value?.toMutableList() ?: mutableListOf()
        val existingItem = currentItems.find { it.itemId == inventory.itemId }
        
        if (existingItem != null) {
            existingItem.quantity += qty
            existingItem.subtotal = existingItem.quantity * existingItem.price
        } else {
            currentItems.add(com.example.shope.data.models.OrderItem(
                itemId = inventory.itemId,
                itemName = inventory.itemName,
                quantity = qty,
                price = inventory.sellingPrice,
                subtotal = qty * inventory.sellingPrice
            ))
        }
        _cartItems.value = currentItems
    }

    fun removeFromCart(itemId: String) {
        val currentItems = _cartItems.value?.toMutableList() ?: return
        currentItems.removeAll { it.itemId == itemId }
        _cartItems.value = currentItems
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }
}
