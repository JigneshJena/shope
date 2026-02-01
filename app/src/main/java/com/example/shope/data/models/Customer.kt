package com.example.shope.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Customer(
    @DocumentId
    var customerId: String = "",
    var name: String = "",
    var phone: String = "",
    var email: String = "",
    var address: String = "",
    var profilePicture: String = "", // Base64 string
    var customerType: String = "Regular", // Regular, School/Institution
    var notes: String = "",
    var totalOrders: Int = 0,
    var totalSpent: Double = 0.0,
    var lastVisitDate: Long = 0L,
    var addedBy: String = "", // Employee UID who added
    var status: String = "active",
    @ServerTimestamp
    var createdAt: Date? = null,
    @ServerTimestamp
    var updatedAt: Date? = null
) {
    constructor() : this("", "", "", "", "", "", "Regular", "", 0, 0.0, 0L, "", "active", null, null)
    
    fun isActive(): Boolean = status == "active"
    
    fun getLastVisit(): String {
        return if (lastVisitDate > 0) {
            com.example.shope.utils.DateUtils.formatDate(lastVisitDate)
        } else "Never"
    }
}
