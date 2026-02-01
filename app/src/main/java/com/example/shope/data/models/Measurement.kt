package com.example.shope.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Measurement(
    @DocumentId
    var measurementId: String = "",
    var customerId: String = "",
    var customerName: String = "",
    var measurementType: String = "", // Shirt, Pant, Suit, Dress, Blouse, School Uniform
    var measurements: Map<String, String> = emptyMap(), // All measurements
    var takenBy: String = "", // Employee UID
    var employeeName: String = "",
    var takenDate: Long = System.currentTimeMillis(),
    var linkedOrderId: String = "", // Optional
    var notes: String = "",
    @ServerTimestamp
    var createdAt: Date? = null,
    @ServerTimestamp
    var updatedAt: Date? = null
) {
    constructor() : this("", "", "", "", emptyMap(), "", "", System.currentTimeMillis(), "", "", null, null)
    
    fun getFormattedDate(): String {
        return com.example.shope.utils.DateUtils.formatDate(takenDate)
    }
}

data class Cart(
    @DocumentId
    var cartId: String = "",
    var customerId: String = "",
    var itemId: String = "",
    var itemName: String = "",
    var itemImage: String = "", // Base64
    var price: Double = 0.0,
    var quantity: Int = 1,
    var subtotal: Double = 0.0,
    var addedAt: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", "", 0.0, 1, 0.0, System.currentTimeMillis())
}
