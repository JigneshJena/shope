package com.example.shope.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class School(
    @DocumentId
    var schoolId: String = "",
    var schoolName: String = "",
    var principalName: String = "",
    var contactNumber: String = "",
    var email: String = "",
    var address: String = "",
    var schoolLogo: String = "", // Base64 string
    var uniformDetails: String = "",
    var status: String = "active",
    var totalOrders: Int = 0,
    var totalRevenue: Double = 0.0,
    var addedBy: String = "", // Owner UID
    var active: Boolean = true,
    var uniformItems: List<UniformItem> = emptyList(),
    @ServerTimestamp
    var createdAt: Date? = null,
    @ServerTimestamp
    var updatedAt: Date? = null
) {
    constructor() : this("", "", "", "", "", "", "", "", "active", 0, 0.0, "", true, emptyList(), null, null)
    
    fun checkActiveStatus(): Boolean = status == "active"
}
