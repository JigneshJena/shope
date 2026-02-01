package com.example.shope.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Order(
    @DocumentId
    var orderId: String = "",
    var orderNumber: String = "",
    var customerId: String = "",
    var customerName: String = "",
    var customerPhone: String = "",
    var orderType: String = "", // Alteration, New Stitching, Readymade, School Uniform
    var schoolId: String = "", // If school order
    var orderDetails: Map<String, Any> = emptyMap(), // Type-specific details
    var items: List<OrderItem> = emptyList(), // For readymade orders
    var totalAmount: Double = 0.0,
    var advancePaid: Double = 0.0,
    var balanceAmount: Double = 0.0,
    var orderDate: Long = System.currentTimeMillis(),
    var deliveryDate: Long = 0L,
    var status: String = "Pending", // Pending, In Progress, Ready, Delivered, Cancelled
    var paymentStatus: String = "Unpaid", // Paid, Partial, Unpaid
    var deliveryAddress: String = "",
    var specialInstructions: String = "",
    var createdBy: String = "", // Employee UID
    var employeeName: String = "",
    var notes: String = "",
    @ServerTimestamp
    var createdAt: Date? = null,
    @ServerTimestamp
    var updatedAt: Date? = null
) {
    constructor() : this("", "", "", "", "", "", "", emptyMap(), emptyList(), 0.0, 0.0, 0.0, 
        System.currentTimeMillis(), 0L, "Pending", "Unpaid", "", "", "", "", "", null, null)
    
    fun isPending(): Boolean = status == "Pending"
    fun isInProgress(): Boolean = status == "In Progress"
    fun isReady(): Boolean = status == "Ready"
    fun isDelivered(): Boolean = status == "Delivered"
    fun isCancelled(): Boolean = status == "Cancelled"
    
    fun isPaid(): Boolean = paymentStatus == "Paid"
    fun hasBalance(): Boolean = balanceAmount > 0
    
    fun getFormattedOrderDate(): String {
        return com.example.shope.utils.DateUtils.formatDate(orderDate)
    }
    
    fun getFormattedDeliveryDate(): String {
        return if (deliveryDate > 0) {
            com.example.shope.utils.DateUtils.formatDate(deliveryDate)
        } else "Not set"
    }
}

data class OrderItem(
    var itemId: String = "",
    var itemName: String = "",
    var quantity: Int = 0,
    var price: Double = 0.0,
    var subtotal: Double = 0.0
) {
    constructor() : this("", "", 0, 0.0, 0.0)
}
