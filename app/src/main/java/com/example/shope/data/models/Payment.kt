package com.example.shope.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Payment(
    @DocumentId
    var paymentId: String = "",
    var invoiceNumber: String = "",
    var customerId: String = "",
    var customerName: String = "",
    var orderId: String = "", // Optional link to order
    var items: List<InvoiceItem> = emptyList(),
    var subtotal: Double = 0.0,
    var tax: Double = 0.0,
    var discount: Double = 0.0,
    var totalAmount: Double = 0.0,
    var paymentMethod: String = "Cash",
    var amountReceived: Double = 0.0,
    var balanceAmount: Double = 0.0,
    var transactionId: String = "",
    var paymentDate: Long = System.currentTimeMillis(),
    var receivedBy: String = "", // Employee UID
    var employeeName: String = "",
    var notes: String = "",
    var status: String = "Completed",
    @ServerTimestamp
    var createdAt: Date? = null,
    @ServerTimestamp
    var updatedAt: Date? = null
) {
    constructor() : this("", "", "", "", "", emptyList(), 0.0, 0.0, 0.0, 0.0, "Cash", 0.0, 0.0, "",
        System.currentTimeMillis(), "", "", "", "Completed", null, null)
    
    fun hasBalance(): Boolean = balanceAmount > 0
    
    fun getFormattedPaymentDate(): String {
        return com.example.shope.utils.DateUtils.formatDate(paymentDate)
    }
}

data class InvoiceItem(
    var itemName: String = "",
    var quantity: Int = 0,
    var rate: Double = 0.0,
    var amount: Double = 0.0
) {
    constructor() : this("", 0, 0.0, 0.0)
}
