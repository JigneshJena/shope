package com.example.shope.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shope.data.repository.OrderRepository
import kotlinx.coroutines.launch
import com.example.shope.data.models.Inventory
import com.example.shope.data.repository.InventoryRepository

data class EmployeeStats(
    val pendingOrdersCount: Int = 0,
    val todaysSales: Double = 0.0
)



class EmployeeViewModel : ViewModel() {
    private val orderRepo = OrderRepository()
    private val inventoryRepo = InventoryRepository()

    private val _stats = MutableLiveData<EmployeeStats>()
    val stats: LiveData<EmployeeStats> = _stats
    
    private val _inventory = MutableLiveData<List<Inventory>>()
    val inventory: LiveData<List<Inventory>> = _inventory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadDashboardStats() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val pendingCount = orderRepo.getPendingOrdersCount()
                val todaysSales = orderRepo.getTodaysTotalSales().getOrDefault(0.0)

                _stats.value = EmployeeStats(
                    pendingOrdersCount = pendingCount,
                    todaysSales = todaysSales
                )
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadInventory() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = inventoryRepo.getAllInventoryItems()
            _inventory.value = result.getOrDefault(emptyList())
            _isLoading.value = false
        }
    }
    
    fun addInventoryItem(item: Inventory) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = inventoryRepo.addInventoryItem(item)
            if (result.isSuccess) {
                loadInventory()
            }
            _isLoading.value = false
        }
    }

    // Customer Management
    private val customerRepo = com.example.shope.data.repository.CustomerRepository()
    private val _customers = MutableLiveData<List<com.example.shope.data.models.Customer>>()
    val customers: LiveData<List<com.example.shope.data.models.Customer>> = _customers
    
    fun loadCustomers() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = customerRepo.getAllCustomers()
            _customers.value = result.getOrDefault(emptyList())
            _isLoading.value = false
        }
    }
    
    fun addCustomer(customer: com.example.shope.data.models.Customer) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = customerRepo.addCustomer(customer)
            if (result.isSuccess) {
                loadCustomers()
            }
             _isLoading.value = false
        }
    }

    // Order Management
    private val _orders = MutableLiveData<List<com.example.shope.data.models.Order>>()
    val orders: LiveData<List<com.example.shope.data.models.Order>> = _orders
    
    fun loadOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = orderRepo.getAllOrders()
            _orders.value = result.getOrDefault(emptyList())
            _isLoading.value = false
        }
    }
    
    fun addOrder(order: com.example.shope.data.models.Order) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = orderRepo.createOrder(order)
            if (result.isSuccess) {
                loadOrders()
                loadDashboardStats()
            }
            _isLoading.value = false
        }
    }
    
    suspend fun searchCustomer(query: String): com.example.shope.data.models.Customer? {
        val result = customerRepo.searchCustomers(query)
        return result.getOrDefault(emptyList()).firstOrNull()
    }
}
