package com.example.shope.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Employee(
    @DocumentId
    var employeeId: String = "", // Same as userId
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var profilePicture: String = "", // Base64 string
    var role: String = "employee", // Always employee
    var status: String = "active", // active, inactive
    var addedBy: String = "", // Owner UID who added this employee
    @ServerTimestamp
    var createdAt: Date? = null,
    @ServerTimestamp
    var updatedAt: Date? = null
) {
    // No-argument constructor for Firestore
    constructor() : this("", "", "", "", "", "employee", "active", "", null, null)
    
    /**
     * Get formatted join date
     */
    fun getFormattedJoinDate(): String {
        return createdAt?.let {
            com.example.shope.utils.DateUtils.formatDate(it.time)
        } ?: "N/A"
    }
    
    /**
     * Check if employee is active
     */
    fun isActive(): Boolean {
        return status == "active"
    }
}
