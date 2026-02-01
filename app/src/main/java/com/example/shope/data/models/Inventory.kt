package com.example.shope.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Inventory(
    @DocumentId
    var itemId: String = "",
    var itemName: String = "",
    var category: String = "", // Fabric, Readymade, Accessories, etc.
    var subCategory: String = "",
    var size: String = "",
    var color: String = "",
    var fabric: String = "",
    var quantity: Int = 0,
    var unit: String = "Pieces", // Meters, Pieces, Rolls, Kg, Pairs
    var purchasePrice: Double = 0.0,
    var sellingPrice: Double = 0.0,
    var profitMargin: Double = 0.0,
    var supplierName: String = "",
    var minimumStockLevel: Int = 5,
    var itemImage: String = "", // Base64 string
    var barcode: String = "",
    var description: String = "",
    var status: String = "active",
    var addedBy: String = "",
    @ServerTimestamp
    var createdAt: Date? = null,
    @ServerTimestamp
    var updatedAt: Date? = null
) {
    constructor() : this("", "", "", "", "", "", "", 0, "Pieces", 0.0, 0.0, 0.0, "", 5, "", "", "", "active", "", null, null)
    
    fun getStockStatus(): String {
        return when {
            quantity == 0 -> "Out of Stock"
            quantity <= minimumStockLevel -> "Low Stock"
            else -> "In Stock"
        }
    }
    
    fun isInStock(): Boolean = quantity > 0
    fun isLowStock(): Boolean = quantity > 0 && quantity <= minimumStockLevel
    fun isOutOfStock(): Boolean = quantity == 0
    
    fun getTotalValue(): Double = quantity * sellingPrice
}
