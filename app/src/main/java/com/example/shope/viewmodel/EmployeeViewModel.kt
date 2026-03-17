package com.example.shope.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shope.data.repository.OrderRepository
import kotlinx.coroutines.launch
import com.example.shope.data.repository.InventoryRepository
import com.example.shope.data.models.UniformItem
import com.example.shope.data.models.Inventory
import com.example.shope.data.repository.SchoolRepository
import com.example.shope.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import kotlinx.coroutines.tasks.await

data class EmployeeStats(
    val pendingOrdersCount: Int = 0,
    val todaysSales: Double = 0.0
)



class EmployeeViewModel : ViewModel() {
    private val orderRepo = OrderRepository()
    private val inventoryRepo = InventoryRepository()

    private val schoolRepo = SchoolRepository()

    private val _stats = MutableLiveData<EmployeeStats>()
    val stats: LiveData<EmployeeStats> = _stats
    
    private val _stockItems = MutableLiveData<List<UniformItem>>()
    val stockItems: LiveData<List<UniformItem>> = _stockItems

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
    

    fun loadStockItems() {
        viewModelScope.launch {
            _isLoading.value = true
            val allCombinedItems = mutableListOf<UniformItem>()

            // 1. Load Regular Inventory (Assets)
            val inventoryResult = inventoryRepo.getAllInventoryItems()
            if (inventoryResult.isSuccess) {
                val inventoryItems = inventoryResult.getOrDefault(emptyList()).map { item ->
                    UniformItem(
                        id = item.itemId,
                        itemName = item.itemName,
                        itemImage = item.itemImage,
                        category = item.category,
                        price = item.sellingPrice,
                        quantity = item.quantity,
                        status = item.status,
                        schoolId = "general",
                        schoolName = "General Stock"
                    )
                }
                allCombinedItems.addAll(inventoryItems)
            }

            // 2. Load School Uniforms
            val schoolsResult = schoolRepo.getAllSchools()
            if (schoolsResult.isSuccess) {
                val schools = schoolsResult.getOrDefault(emptyList())
                for (school in schools) {
                    val schoolItems = school.uniformItems.map { item ->
                        item.copy(
                            schoolId = school.schoolId,
                            schoolName = school.schoolName
                        )
                    }
                    allCombinedItems.addAll(schoolItems)
                }
            }
            
            _stockItems.value = allCombinedItems
            _isLoading.value = false
        }
    }
    
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun updateStockQuantity(item: UniformItem, newQuantity: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("EmployeeViewModel", "Updating stock for item: ${item.itemName}, new qty: $newQuantity")
            
            if (item.schoolId == "general") {
                // Update Regular Inventory
                val result = inventoryRepo.setStockQuantity(item.id, newQuantity)
                if (result.isSuccess) {
                    _message.value = "Stock updated successfully!"
                    loadStockItems()
                } else {
                    _message.value = "Update failed: ${result.exceptionOrNull()?.message}"
                }
            } else {
                // Update School Uniform Item
                val schoolResult = schoolRepo.getSchoolById(item.schoolId)
                if (schoolResult.isSuccess) {
                    val school = schoolResult.getOrNull()
                    if (school != null) {
                        val updatedItems = school.uniformItems.map { 
                            if (it.itemName == item.itemName) {
                                it.copy(quantity = newQuantity)
                            } else {
                                it
                            }
                        }
                        school.uniformItems = updatedItems
                        val updateResult = schoolRepo.updateSchool(school)
                        if (updateResult.isSuccess) {
                            _message.value = "Stock updated successfully!"
                            loadStockItems()
                        } else {
                            _message.value = "Update failed: ${updateResult.exceptionOrNull()?.message}"
                        }
                    } else {
                        _message.value = "School not found"
                    }
                } else {
                    _message.value = "Error fetching school data"
                }
            }
            _isLoading.value = false
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
