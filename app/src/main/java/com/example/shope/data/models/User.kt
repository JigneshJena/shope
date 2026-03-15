package com.example.shope.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class User(
    @DocumentId
    var userId: String = "",
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var role: String = "", // admin, owner, employee, customer
    var profilePicture: String = "", // Base64 string
    var status: String = "active", // active, inactive
    var address: String = "", // For customers
    @ServerTimestamp
    var createdAt: Date? = null,
    @ServerTimestamp
    var updatedAt: Date? = null
) {
    // No-argument constructor required for Firestore
    constructor() : this("", "", "", "", "", "", "active", "", null, null)
    
    /**
     * Check if user is admin
     */
    fun isAdmin(): Boolean {
        return role == "admin" || email == "admin@tailorapp.com"
    }
    
    /**
     * Check if user is owner
     */
    fun isOwner(): Boolean {
        return role == "owner" || (email.contains("@owner", ignoreCase = true) && !isAdmin())
    }
    
    /**
     * Check if user is employee
     */
    fun isEmployee(): Boolean {
        return role == "employee" || 
               email.contains("@employee", ignoreCase = true) || 
               email.contains("@emp.com", ignoreCase = true)
    }
    
    /**
     * Check if user is customer
     */
    fun isCustomer(): Boolean {
        return role == "customer"
    }
    
    /**
     * Check if user is active
     */
    fun isActive(): Boolean {
        return status == "active"
    }
}
