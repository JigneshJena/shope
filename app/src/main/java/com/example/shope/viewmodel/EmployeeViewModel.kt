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
    
    suspend fun getOwnerUid(): String? {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return null
        return try {
            val snapshot = FirebaseFirestore.getInstance().collectionGroup(Constants.COLLECTION_EMPLOYEES)
                .whereEqualTo("employeeId", currentUserId)
                .get()
                .await()
            val employee = snapshot.documents.firstOrNull()
            employee?.getString("addedBy")
        } catch (e: Exception) {
            null
        }
    }

    fun loadStockItems() {
        viewModelScope.launch {
            _isLoading.value = true
            val ownerUid = getOwnerUid()
            if (ownerUid != null) {
                val schoolsResult = schoolRepo.getAllSchools(ownerUid)
                if (schoolsResult.isSuccess) {
                    val schools = schoolsResult.getOrDefault(emptyList())
                    val allItems = mutableListOf<UniformItem>()
                    for (school in schools) {
                        allItems.addAll(school.uniformItems)
                    }
                    _stockItems.value = allItems
                }
            }
            _isLoading.value = false
        }
    }
    
    fun updateStockQuantity(item: UniformItem, newQuantity: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val ownerUid = getOwnerUid()
            if (ownerUid != null) {
                val schoolResult = schoolRepo.getSchoolById(ownerUid, item.schoolId)
                if (schoolResult.isSuccess) {
                    val school = schoolResult.getOrNull()
                    if (school != null) {
                         val updatedItems = school.uniformItems.map { 
                             if (it.id == item.id && it.itemName == item.itemName) {
                                 it.copy(quantity = newQuantity)
                             } else {
                                 it
                             }
                         }
                         school.uniformItems = updatedItems
                         schoolRepo.updateSchool(ownerUid, school)
                         loadStockItems() // reload
                    }
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
