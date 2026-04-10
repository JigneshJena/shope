package com.example.shope.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shope.data.models.Inventory
import com.example.shope.data.repository.InventoryRepository
import com.example.shope.data.repository.OrderRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    private val _orders = MutableLiveData<List<com.example.shope.data.models.Order>>()
    val orders: LiveData<List<com.example.shope.data.models.Order>> = _orders

    private val _orderState = MutableLiveData<Result<String>?>(null)
    val orderState: LiveData<Result<String>?> = _orderState

    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            val inventoryResult = inventoryRepo.getAllInventoryItems()
            val inventoryList = inventoryResult.getOrDefault(emptyList()).toMutableList()
            
            val schoolResult = schoolRepo.getAllSchools()
            if (schoolResult.isSuccess) {
                val schools = schoolResult.getOrDefault(emptyList())
                for (school in schools) {
                    for (item in school.uniformItems) {
                        val inventoryItem = Inventory(
                            itemId = if (item.id.isEmpty()) java.util.UUID.randomUUID().toString() else item.id,
                            itemName = "${school.schoolName} - ${item.itemName}",
                            category = item.category,
                            sellingPrice = item.price,
                            quantity = item.quantity,
                            itemImage = item.itemImage,
                            status = item.status,
                            schoolId = school.schoolId,
                            description = "School: ${school.schoolName}\nDetails: ${school.uniformDetails}"
                        )
                        inventoryList.add(inventoryItem)
                    }
                }
            }
            _products.value = inventoryList
            _isLoading.value = false
        }
    }

    fun loadMyOrders(customerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = orderRepo.getOrdersByCustomer(customerId)
            _orders.value = result.getOrDefault(emptyList())
            _isLoading.value = false
        }
    }

    fun placeOrder(customerId: String, customerName: String, customerPhone: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val items = _cartItems.value ?: emptyList()
            if (items.isEmpty()) {
                _isLoading.value = false
                return@launch
            }

            val totalAmount = items.sumOf { it.subtotal }
            val order = com.example.shope.data.models.Order(
                customerId = customerId,
                customerName = customerName,
                customerPhone = customerPhone,
                orderType = com.example.shope.utils.Constants.ORDER_TYPE_READYMADE,
                items = items,
                totalAmount = totalAmount,
                advancePaid = totalAmount, // Fully paid for customer app
                balanceAmount = 0.0,
                status = com.example.shope.utils.Constants.ORDER_STATUS_PENDING,
                paymentStatus = com.example.shope.utils.Constants.PAYMENT_STATUS_PAID,
                orderDate = System.currentTimeMillis()
            )

            val result = orderRepo.createOrder(order)
            _orderState.value = result
            if (result.isSuccess) {
                clearCart()
            }
            _isLoading.value = false
        }
    }

    fun clearOrderState() {
        _orderState.value = null
    }

    fun updateUserProfile(userId: String, name: String, phone: String, address: String, prefManager: com.example.shope.utils.PreferenceManager) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection(com.example.shope.utils.Constants.COLLECTION_USERS)
                    .document(userId)
                    .update(mapOf(
                        "name" to name,
                        "phone" to phone,
                        "address" to address
                    )).await()
                
                prefManager.saveUserName(name)
                prefManager.saveUserPhone(phone)
                // Note: PreferenceManager might not have saveUserAddress, but we can add or ignore for now
                
                Log.d("CustomerViewModel", "Profile updated successfully")
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Profile update failed", e)
            }
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
                subtotal = qty * inventory.sellingPrice,
                itemImage = inventory.itemImage,
                schoolId = inventory.schoolId
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

    // ---- Measurements ----

    private val _measurement = MutableLiveData<com.example.shope.data.models.Measurement?>()
    val measurement: LiveData<com.example.shope.data.models.Measurement?> = _measurement

    /**
     * Load the customer's measurement from Firestore.
     * Measurements are stored per customer in the measurements collection.
     */
    fun loadMeasurement(customerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection(com.example.shope.utils.Constants.COLLECTION_MEASUREMENTS)
                    .whereEqualTo("customerId", customerId)
                    .limit(1)
                    .get()
                    .await()

                if (!snapshot.isEmpty) {
                    val doc = snapshot.documents[0]
                    val m = doc.toObject(com.example.shope.data.models.Measurement::class.java)
                    _measurement.value = m
                } else {
                    _measurement.value = null
                }
                Log.d("CustomerViewModel", "Measurement loaded: ${_measurement.value}")
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Failed to load measurement", e)
                _measurement.value = null
            }
            _isLoading.value = false
        }
    }

    /**
     * Save or update the customer's measurement in Firestore
     */
    fun saveMeasurement(measurement: com.example.shope.data.models.Measurement) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                val collection = firestore.collection(com.example.shope.utils.Constants.COLLECTION_MEASUREMENTS)

                if (measurement.measurementId.isNotEmpty()) {
                    // Update existing
                    collection.document(measurement.measurementId)
                        .set(measurement)
                        .await()
                } else {
                    // Create new - use auto-generated ID
                    val docRef = collection.document()
                    measurement.measurementId = docRef.id
                    docRef.set(measurement).await()
                }
                _measurement.value = measurement
                Log.d("CustomerViewModel", "Measurement saved successfully")
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Failed to save measurement", e)
            }
            _isLoading.value = false
        }
    }
}
