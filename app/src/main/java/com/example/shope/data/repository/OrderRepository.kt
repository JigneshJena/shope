package com.example.shope.data.repository

import android.util.Log
import com.example.shope.data.models.Order
import com.example.shope.data.models.OrderItem
import com.example.shope.utils.Constants
import com.example.shope.utils.DateUtils
import com.example.shope.utils.generateOrderNumber
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class OrderRepository {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val inventoryRepository = InventoryRepository()
    
    companion object {
        private const val TAG = "OrderRepository"
    }
    
    /**
     * Create new order - FIX for order creation issue
     */
    suspend fun createOrder(order: Order): Result<String> {
        return try {
            // Generate order number
            order.orderNumber = generateOrderNumber()
            
            // Create document with auto-generated ID
            val docRef = firestore.collection(Constants.COLLECTION_ORDERS)
                .document()
            
            order.orderId = docRef.id
            order.orderDate = System.currentTimeMillis()
            
            // Calculate balance
            order.balanceAmount = order.totalAmount - order.advancePaid
            
            // Determine payment status
            order.paymentStatus = when {
                order.advancePaid >= order.totalAmount -> Constants.PAYMENT_STATUS_PAID
                order.advancePaid > 0 -> Constants.PAYMENT_STATUS_PARTIAL
                else -> Constants.PAYMENT_STATUS_UNPAID
            }
            
            // Save order to Firestore
            docRef.set(order).await()
            
            // If readymade order, update inventory quantities
            if (order.orderType == Constants.ORDER_TYPE_READYMADE && order.items.isNotEmpty()) {
                updateInventoryForOrder(order.items)
            }
            
            // Update customer stats
            updateCustomerStats(order.customerId, order.totalAmount)
            
            Log.d(TAG, "Order created successfully: ${order.orderNumber}")
            Result.success(order.orderId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create order", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get all orders
     */
    suspend fun getAllOrders(): Result<List<Order>> {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_ORDERS)
                .orderBy("orderDate", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val orders = snapshot.toObjects(Order::class.java)
            Result.success(orders)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get orders", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get orders by status
     */
    suspend fun getOrdersByStatus(status: String): Result<List<Order>> {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_ORDERS)
                .whereEqualTo("status", status)
                .orderBy("orderDate", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val orders = snapshot.toObjects(Order::class.java)
            Result.success(orders)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get orders by status", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get orders by customer
     */
    suspend fun getOrdersByCustomer(customerId: String): Result<List<Order>> {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_ORDERS)
                .whereEqualTo("customerId", customerId)
                .orderBy("orderDate", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val orders = snapshot.toObjects(Order::class.java)
            Result.success(orders)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get customer orders", e)
            Result.failure(e)
        }
    }
    
    /**
     * Update order status
     */
    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_ORDERS)
                .document(orderId)
                .update("status", status)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update order status", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get today's sales total
     */
    suspend fun getTodaysTotalSales(): Result<Double> {
        return try {
            val startOfDay = DateUtils.getStartOfDay()
            val endOfDay = DateUtils.getEndOfDay()
            
            val snapshot = firestore.collection(Constants.COLLECTION_ORDERS)
                .whereGreaterThanOrEqualTo("orderDate", startOfDay)
                .whereLessThanOrEqualTo("orderDate", endOfDay)
                .get()
                .await()
            
            val total = snapshot.toObjects(Order::class.java).sumOf { it.totalAmount }
            Result.success(total)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get today's sales", e)
            Result.failure(e)
        }
    }

    
    /**
     * Get pending orders count
     */
    suspend fun getPendingOrdersCount(): Int {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_ORDERS)
                .whereEqualTo("status", Constants.ORDER_STATUS_PENDING)
                .get()
                .await()
            
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Update inventory quantities for readymade orders
     */
    private suspend fun updateInventoryForOrder(items: List<OrderItem>) {
        items.forEach { item ->
            inventoryRepository.adjustStock(item.itemId, -item.quantity)
        }
    }
    
    /**
     * Update customer statistics
     */
    private suspend fun updateCustomerStats(customerId: String, orderAmount: Double) {
        try {
            val customerRef = firestore.collection(Constants.COLLECTION_CUSTOMERS)
                .document(customerId)
            
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(customerRef)
                val currentOrders = snapshot.getLong("totalOrders") ?: 0
                val currentSpent = snapshot.getDouble("totalSpent") ?: 0.0
                
                transaction.update(customerRef, mapOf(
                    "totalOrders" to currentOrders + 1,
                    "totalSpent" to currentSpent + orderAmount,
                    "lastVisitDate" to System.currentTimeMillis()
                ))
            }.await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update customer stats", e)
        }
    }
}
